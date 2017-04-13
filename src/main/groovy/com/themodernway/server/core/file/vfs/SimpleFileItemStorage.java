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

import static com.themodernway.server.core.file.FilePathUtils.SINGLE_SLASH;
import static com.themodernway.server.core.file.FilePathUtils.concat;
import static com.themodernway.server.core.file.FilePathUtils.getContentTypeOf;
import static com.themodernway.server.core.file.FilePathUtils.normalize;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;

public class SimpleFileItemStorage implements IFileItemStorage, ICoreCommon
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

    private static final Logger      logger = Logger.getLogger(SimpleFileItemStorage.class);

    private final File               m_file;

    private final String             m_name;

    private final String             m_base;

    private final IFolderItem        m_root;

    private boolean                  m_open = false;

    private IFileItemMetaDataFactory m_meta = null;

    public SimpleFileItemStorage(final String name, final String base)
    {
        m_name = requireTrimOrNull(name);

        m_base = requireTrimOrNull(normalize(requireTrimOrNull(base)));

        m_file = new File(m_base);

        m_root = new SimpleFolderItem(m_file, this);

        if ((m_file.exists()) && (m_file.isDirectory()) && (m_file.canRead()))
        {
            m_open = true;

            logger.info(format("SimpleFileItemStorage(%s,%s) open.", m_name, m_base));
        }
        else
        {
            logger.error(format("SimpleFileItemStorage(%s,%s) can't access.", m_name, m_base));
        }
    }

    @Override
    public void validate() throws IOException
    {
        if (false == isOpen())
        {
            throw new IOException(format("SimpleFileItemStorage(%s,%s) is closed.", getName(), getBasePath()));
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

        logger.info(format("SimpleFileItemStorage(%s,%s).close().", getName(), getBasePath()));
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

    protected static class SimpleFileItem implements IFileItem, ICoreCommon
    {
        private final File             m_file;

        private final IFileItemStorage m_stor;

        public SimpleFileItem(final File file, final IFileItemStorage stor)
        {
            m_file = requireNonNull(file);

            m_stor = requireNonNull(stor);
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

            if (false == exists())
            {
                throw new IOException(format("Can't type missing (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't type hidden (%s).", getPath()));
            }
            return getContentTypeOf(getFile());
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

            if (false == exists())
            {
                throw new IOException(format("Can't get limit missing (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't get limit hidden (%s).", getPath()));
            }
            return getFile().getUsableSpace();
        }

        @Override
        public Stream<String> lines() throws IOException
        {
            validate();

            readtest();

            if (isFolder())
            {
                final List<String> list = arrayList();

                for (File file : listFiles())
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

            if (false == exists())
            {
                throw new IOException(format("Can't size missing (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't size hidden (%s).", getPath()));
            }
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

            return normalize(getAbsolutePath().replace(getFileItemStorage().getBasePath(), SINGLE_SLASH));
        }

        @Override
        public String getAbsolutePath() throws IOException
        {
            validate();

            return normalize(getFile().toPath().toString());
        }

        @Override
        public IFolderItem getParent() throws IOException
        {
            validate();

            if (getPath().equals(SINGLE_SLASH))
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

            if (false == exists())
            {
                throw new IOException(format("Can't date missing (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't date hidden (%s).", getPath()));
            }
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
                throw new IOException(format("Can't delete missing (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't delete hidden (%s).", getPath()));
            }
            return Files.deleteIfExists(getFile().toPath());
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            validate();

            readtest();

            return IO.copy(this, requireNonNull(output));
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

        protected File[] listFiles()
        {
            return getFile().listFiles((node, name) -> false == node.isHidden());
        }

        protected void readtest() throws IOException
        {
            if (false == exists())
            {
                throw new IOException(format("Can't read missing (%s).", getPath()));
            }
            if (false == isReadable())
            {
                throw new IOException(format("Can't read (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't read hidden (%s).", getPath()));
            }
        }
    }

    protected static class SimpleFolderItem extends SimpleFileItem implements IFolderItem, ICoreCommon
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

            final List<IFileItem> list = arrayList();

            if (isFolder())
            {
                for (File file : listFiles())
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

            String path = normalize(name);

            if (false == path.startsWith(SINGLE_SLASH))
            {
                path = concat(getPath(), path);
            }
            path = concat(getFileItemStorage().getBasePath(), toTrimOrElse(path, EMPTY_STRING));

            if (null != path)
            {
                return MAKE(path, getFileItemStorage());
            }
            return null;
        }

        @Override
        public IFileItem create(final String name, final File file) throws IOException
        {
            return create(name, file.toPath());
        }

        @Override
        public IFileItem create(final String name, final Path path) throws IOException
        {
            validate();

            InputStream stream = null;

            try
            {
                stream = Files.newInputStream(path);

                return create(name, stream);
            }
            finally
            {
                IO.close(stream);
            }
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
                    throw new IOException(format("Can't create hidden (%s).", item.getPath()));
                }
                if (item.exists())
                {
                    if (item.isFile())
                    {
                        if (false == item.delete())
                        {
                            throw new IOException(format("Can't delete (%s).", item.getPath()));
                        }
                    }
                    else
                    {
                        throw new IOException(format("Can't delete folder (%s).", item.getPath()));
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
            throw new IOException(format("Can't resolve (%s).", name));
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
            
            readtest();

            throw new IOException(format("Can't stream folder (%s).", getPath()));
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            validate();
            
            readtest();

            throw new IOException(format("Can't stream folder (%s).", getPath()));
        }
    }
}
