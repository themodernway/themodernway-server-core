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

    private final File                        m_file;

    private final String                      m_name;

    private final String                      m_base;

    private final IFolderItem                 m_root;

    private boolean                           m_open = false;

    private IFileItemMetaDataFactory          m_meta = null;

    public SimpleFileItemStorage(final String name, final String base)
    {
        m_name = StringOps.requireTrimOrNull(name);

        m_base = StringOps.requireTrimOrNull(FilePathUtils.normalize(StringOps.requireTrimOrNull(base)));

        m_file = new File(m_base);

        m_root = new SimpleFolderItem(m_file, this);

        if ((m_file.exists()) && (m_file.isDirectory()) && (m_file.canRead()))
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
    public IFolderItem getRoot() throws IOException
    {
        validate();

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
        public String getContentType() throws IOException
        {
            validate();

            return mapper.getContentType(getFile());
        }

        @Override
        public JSONObject getMetaData() throws IOException
        {
            validate();

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
        public long getFileSizeLimit() throws IOException
        {
            validate();

            return getFile().getUsableSpace();
        }

        @Override
        public Stream<String> lines() throws IOException
        {
            validate();

            readtest();

            if (isFolder())
            {
                final ArrayList<String> list = new ArrayList<String>();

                for (File file : getFile().listFiles((node, name) -> false == node.isHidden()))
                {
                    list.add(file.getName());
                }
                return list.stream();
            }
            else
            {
                return Files.lines(getFile().toPath());
            }
        }

        @Override
        public String getName() throws IOException
        {
            validate();

            return getFile().getName();
        }

        @Override
        public long getSize() throws IOException
        {
            validate();

            return getFile().length();
        }

        @Override
        public boolean isHidden() throws IOException
        {
            validate();

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
        public boolean isReadable() throws IOException
        {
            validate();

            return getFile().canRead();
        }

        @Override
        public boolean isWritable() throws IOException
        {
            validate();

            return getFile().canWrite();
        }

        @Override
        public boolean isFile() throws IOException
        {
            validate();

            return getFile().isFile();
        }

        @Override
        public boolean isFolder() throws IOException
        {
            validate();

            return getFile().isDirectory();
        }

        @Override
        public String getPath() throws IOException
        {
            validate();

            return FilePathUtils.normalize(getAbsolutePath().replace(getFileItemStorage().getBasePath(), FilePathUtils.SINGLE_SLASH));
        }

        @Override
        public String getAbsolutePath() throws IOException
        {
            validate();

            return FilePathUtils.normalize(getFile().toPath().toString());
        }

        @Override
        public IFolderItem getParent() throws IOException
        {
            validate();

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
        public IFolderItem getAsFolderItem() throws IOException
        {
            validate();

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
        public IFolderItem getRoot() throws IOException
        {
            validate();

            return getFileItemStorage().getRoot();
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            validate();

            readtest();

            return Files.newInputStream(getFile().toPath());
        }

        @Override
        public Date getLastModified() throws IOException
        {
            validate();

            return new Date(getFile().lastModified());
        }

        @Override
        public boolean exists() throws IOException
        {
            validate();

            return getFile().exists();
        }

        @Override
        public boolean delete() throws IOException
        {
            validate();

            if (false == exists())
            {
                throw new IOException("Can't delete missing " + getPath());
            }
            if (isHidden())
            {
                throw new IOException("Can't delete hidden " + getPath());
            }
            return Files.deleteIfExists(getFile().toPath());
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            validate();

            readtest();

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

        protected void readtest() throws IOException
        {
            if (false == exists())
            {
                throw new IOException("Can't read missing " + getPath());
            }
            if (false == isReadable())
            {
                throw new IOException("Can't read " + getPath());
            }
            if (isHidden())
            {
                throw new IOException("Can't read hidden " + getPath());
            }
        }
    }

    protected static class SimpleFolderItem extends SimpleFileItem implements IFolderItem
    {
        public SimpleFolderItem(final File file, final IFileItemStorage stor)
        {
            super(file, stor);
        }

        @Override
        public Stream<IFileItem> items() throws IOException
        {
            validate();

            readtest();

            final ArrayList<IFileItem> list = new ArrayList<IFileItem>();

            if (isFolder())
            {
                for (File file : getFile().listFiles((node, name) -> false == node.isHidden()))
                {
                    list.add(MAKE(file, getFileItemStorage()));
                }
            }
            return list.stream();
        }

        @Override
        public IFileItem find(final String name) throws IOException
        {
            validate();

            final IFileItem item = file(name);

            if ((null != item) && (item.exists()))
            {
                return item;
            }
            return null;
        }

        @Override
        public IFileItem file(final String name) throws IOException
        {
            validate();

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
                if (item.isHidden())
                {
                    throw new IOException("Can't create hidden file " + item.getPath());
                }
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

                final OutputStream fios = Files.newOutputStream(file.toPath());

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
        public IFolderItem getAsFolderItem() throws IOException
        {
            validate();

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
