/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.themodernway.server.core.file.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;

public class SimpleFileItemStorage implements IFileItemStorage
{
    protected static final IFileItem MAKE(String path, IFileItemStorage stor)
    {
        return MAKE(new File(path), stor);
    }

    protected static final IFileItem MAKE(Path path, IFileItemStorage stor)
    {
        return MAKE(path.toFile(), stor);
    }

    protected static final IFileItem MAKE(File file, IFileItemStorage stor)
    {
        if (file.isDirectory())
        {
            return new SimpleFolderItem(file, stor);
        }
        else
        {
            return new SimpleFileItem(file, stor);
        }
    }

    private static final Logger                 logger = Logger.getLogger(SimpleFileItemStorage.class);

    protected static final MimetypesFileTypeMap mapper = new MimetypesFileTypeMap();

    private final String                        m_name;

    private final String                        m_base;

    private final IFolderItem                   m_root;

    private boolean                             m_open = false;

    private IFileItemMetaDataFactory            m_meta = null;

    public SimpleFileItemStorage(final String name, final String base)
    {
        m_name = StringOps.requireTrimOrNull(name);

        m_base = StringOps.requireTrimOrNull(FileItemUtils.normalize(StringOps.requireTrimOrNull(base)));

        m_root = new SimpleFolderItem(new File(m_base), this);

        if ((m_root.exists()) && (m_root.isFolder()) && (m_root.isReadable()))
        {
            m_open = true;

            logger.info("SimpleFileItemStorage(" + m_name + "," + m_base + ") open.");
        }
        else
        {
            logger.error("SimpleFileItemStorage(" + m_name + "," + m_base + ") can't access.");
        }
    }

    @SuppressWarnings("resource")
    public static void main(String... args)
    {
        IFolderItem root = new SimpleFileItemStorage("content", "/Users/deanjones/content").getRoot();

        System.out.println(root.getPath());

        IFileItem item = root.find("IFileItem.java");

        try
        {
            item.writeTo(System.out);

            System.out.println();

            System.out.println(item.getPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        item = root.find("/sdl/IFolderItem.java");

        try
        {
            item.writeTo(System.out);

            System.out.println();

            System.out.println(item.getContentType());

            System.out.println(item.getPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        item = root.find("/sdl/");

        try
        {
            System.out.println();

            System.out.println(item.getContentType());

            System.out.println(item.getPath());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public IFileItemMetaDataFactory getFileItemMetaDataFactory()
    {
        return m_meta;
    }

    @Override
    public void setFileItemMetaDataFactory(final IFileItemMetaDataFactory meta)
    {
        m_meta = meta;
    }

    @Override
    public boolean isOpen()
    {
        return m_open;
    }

    @Override
    public IFolderItem getRoot()
    {
        return m_root;
    }

    @Override
    public void close() throws IOException
    {
        m_open = false;

        logger.info("SimpleFileItemStorage(" + m_name + "," + m_base + ") closed.");
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public String getBasePath()
    {
        return m_base;
    }

    @Override
    public String getAbsolutePath(final String name)
    {
        return FileItemUtils.concat(getBasePath(), StringOps.toTrimOrElse(name, StringOps.EMPTY_STRING));
    }

    protected static class SimpleFileItem implements IFileItem
    {
        private final File             m_file;

        private final IFileItemStorage m_stor;

        public SimpleFileItem(final File file, final IFileItemStorage stor)
        {
            m_file = Objects.requireNonNull(file);

            m_stor = Objects.requireNonNull(stor);
        }

        @Override
        public String getContentType()
        {
            return mapper.getContentType(getFile());
        }

        @Override
        public JSONObject getMetaData()
        {
            final IFileItemMetaDataFactory fact = getFileItemStorage().getFileItemMetaDataFactory();

            if (null != fact)
            {
                final JSONObject json = fact.getMetaData(this);

                if (null != json)
                {
                    return json;
                }
            }
            return new JSONObject();
        }

        @Override
        public long getFileSizeLimit()
        {
            return getFile().getUsableSpace();
        }

        @Override
        public Stream<String> lines() throws IOException
        {
            if (exists() && isReadable())
            {
                if (isFolder())
                {
                    return Stream.of(getFile().list());
                }
                return Files.lines(getFile().toPath());
            }
            throw new IOException("Can't read " + getPath());
        }

        @Override
        public String getName()
        {
            return getFile().getName();
        }

        @Override
        public long getSize()
        {
            return getFile().length();
        }

        @Override
        public boolean isHidden()
        {
            return getFile().isHidden();
        }

        @Override
        public boolean isReadable()
        {
            return getFile().canRead();
        }

        @Override
        public boolean isWritable()
        {
            return getFile().canWrite();
        }

        @Override
        public boolean isFile()
        {
            return getFile().isFile();
        }

        @Override
        public boolean isFolder()
        {
            return getFile().isDirectory();
        }

        @Override
        public String getPath()
        {
            return FileItemUtils.normalize(getAbsolutePath().replace(getFileItemStorage().getBasePath(), "/"));
        }

        @Override
        public String getAbsolutePath()
        {
            return FileItemUtils.normalize(getFile().toPath().toString());
        }

        @Override
        public IFolderItem getParent()
        {
            if (getPath().equals("/"))
            {
                return null;
            }
            final File parent = getFile().getParentFile();

            if (null != parent)
            {
                return new SimpleFolderItem(parent, getFileItemStorage());
            }
            return null;
        }

        @Override
        public IFolderItem getAsFolderItem()
        {
            if (this instanceof IFolderItem)
            {
                return (IFolderItem) this;
            }
            if (isFolder())
            {
                return new SimpleFolderItem(getFile(), getFileItemStorage());
            }
            return null;
        }

        @Override
        public IFolderItem getRoot()
        {
            return getFileItemStorage().getRoot();
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            return new FileInputStream(getFile());
        }

        @Override
        public Date getLastModified()
        {
            return new Date(getFile().lastModified());
        }

        @Override
        public boolean exists()
        {
            return getFile().exists();
        }

        @Override
        public boolean delete()
        {
            return getFile().delete();
        }

        @Override
        public boolean rename(final String name)
        {
            return getFile().renameTo(new File(FileItemUtils.normalize(name)));
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            return IO.copy(this, Objects.requireNonNull(output));
        }

        @Override
        public IFileItemStorage getFileItemStorage()
        {
            return m_stor;
        }

        protected File getFile()
        {
            return m_file;
        }
    }

    protected static class SimpleFolderItem extends SimpleFileItem implements IFolderItem
    {
        public SimpleFolderItem(final File file, final IFileItemStorage stor)
        {
            super(file, stor);
        }

        @Override
        public Stream<IFileItem> items()
        {
            final ArrayList<IFileItem> list = new ArrayList<IFileItem>();

            if (isFolder())
            {
                for (File file : getFile().listFiles())
                {
                    list.add(MAKE(file, getFileItemStorage()));
                }
            }
            return list.stream();
        }

        @Override
        public Stream<IFileItem> items(final IFileItemFilter filter)
        {
            final ArrayList<IFileItem> list = new ArrayList<IFileItem>();

            if (isFolder())
            {
                final FilenameFilter look = new FilenameFilter()
                {
                    @Override
                    public boolean accept(File file, String name)
                    {
                        return filter.accept(MAKE(file, getFileItemStorage()), name);
                    }
                };
                for (File file : getFile().listFiles(look))
                {
                    list.add(MAKE(file, getFileItemStorage()));
                }
            }
            return list.stream();
        }

        @Override
        public IFileItem find(final String name)
        {
            final IFileItem item = file(name);

            if ((null != item) && (item.exists()))
            {
                return item;
            }
            return null;
        }

        @Override
        public IFileItem file(final String name)
        {
            String path = FileItemUtils.normalize(name);

            if (false == path.startsWith("/"))
            {
                path = FileItemUtils.concat(getPath(), path);
            }
            path = getFileItemStorage().getAbsolutePath(path);

            if (null != path)
            {
                return MAKE(path, getFileItemStorage());
            }
            return null;
        }

        @Override
        public IFileItem create(final String name, final Resource resource) throws IOException
        {
            InputStream stream = null;

            try
            {
                stream = resource.getInputStream();

                return create(name, stream);
            }
            finally
            {
                IO.close(stream);
            }
        }

        @Override
        public IFileItem create(final String name, final InputStream input) throws IOException
        {
            IFileItem item = file(name);

            if (null != item)
            {
                if (item.exists())
                {
                    if (item.isFile())
                    {
                        if (false == item.delete())
                        {
                            throw new IOException("Can't delete file " + item.getPath());
                        }
                    }
                    else
                    {
                        throw new IOException("Can't delete folder " + item.getPath());
                    }
                    final File file = new File(item.getAbsolutePath());

                    file.mkdirs();

                    file.createNewFile();

                    final FileOutputStream fios = new FileOutputStream(file);

                    try
                    {
                        IO.copy(input, fios);

                        fios.flush();

                        return item;
                    }
                    finally
                    {
                        IO.close(fios);
                    }
                }
            }
            throw new IOException("Can't resolve file " + name);
        }

        @Override
        public IFolderItem getAsFolderItem()
        {
            return this;
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            throw new IOException("Can't read folder " + getPath());
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            throw new IOException("Can't read folder " + getPath());
        }
    }
}
