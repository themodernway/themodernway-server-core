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

package com.themodernway.server.core.file.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import com.themodernway.server.core.file.FilePathUtils;
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

    private static final Logger               logger = Logger.getLogger(SimpleFileItemStorage.class);

    private static final MimetypesFileTypeMap mapper = new MimetypesFileTypeMap();

    private final String                      m_name;

    private final String                      m_base;

    private final IFolderItem                 m_root;

    private boolean                           m_open = false;

    private IFileItemMetaDataFactory          m_meta = null;

    public SimpleFileItemStorage(final String name, final String base)
    {
        m_name = StringOps.requireTrimOrNull(name);

        m_base = StringOps.requireTrimOrNull(FilePathUtils.normalize(StringOps.requireTrimOrNull(base)));

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

    @Override
    public void validate() throws IOException
    {
        if (false == isOpen())
        {
            throw new IOException("SimpleFileItemStorage(" + m_name + "," + m_base + ") closed.");
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
        public void validate() throws IOException
        {
            getFileItemStorage().validate();
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
            validate();

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
            if (getFile().isHidden())
            {
                return true;
            }
            final IFolderItem item = getParent();

            if (null != item)
            {
                return item.isHidden();
            }
            return false;
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
            return FilePathUtils.normalize(getAbsolutePath().replace(getFileItemStorage().getBasePath(), FilePathUtils.SINGLE_SLASH));
        }

        @Override
        public String getAbsolutePath()
        {
            return FilePathUtils.normalize(getFile().toPath().toString());
        }

        @Override
        public IFolderItem getParent()
        {
            if (getPath().equals(FilePathUtils.SINGLE_SLASH))
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
            validate();

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
        public long writeTo(final OutputStream output) throws IOException
        {
            validate();

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
            String path = FilePathUtils.normalize(name);

            if (false == path.startsWith(FilePathUtils.SINGLE_SLASH))
            {
                path = FilePathUtils.concat(getPath(), path);
            }
            path = FilePathUtils.concat(getFileItemStorage().getBasePath(), StringOps.toTrimOrElse(path, StringOps.EMPTY_STRING));

            if (null != path)
            {
                return MAKE(path, getFileItemStorage());
            }
            return null;
        }

        @Override
        public IFileItem create(final String name, final Resource resource) throws IOException
        {
            validate();

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
            validate();

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

                }
                final File file = new File(item.getAbsolutePath());

                file.getParentFile().mkdirs();

                file.createNewFile();

                final FileOutputStream fios = new FileOutputStream(file);

                try
                {
                    IO.copy(input, fios);

                    fios.flush();

                    IO.close(fios);

                    return MAKE(file, getFileItemStorage());
                }
                finally
                {
                    IO.close(fios);
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
            validate();

            throw new IOException("Can't read folder " + getPath());
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            validate();

            throw new IOException("Can't write folder " + getPath());
        }
    }
}
