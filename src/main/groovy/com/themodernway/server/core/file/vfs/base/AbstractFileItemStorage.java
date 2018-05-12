/*
 * Copyright (c) 2018, The Modern Way. All rights reserved.
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

package com.themodernway.server.core.file.vfs.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.content.ICoreContentTypeMapper;
import com.themodernway.server.core.file.FileAndPathUtils;
import com.themodernway.server.core.file.vfs.FileItemWrapper;
import com.themodernway.server.core.file.vfs.FolderItemWrapper;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFileItemAttributes;
import com.themodernway.server.core.file.vfs.IFileItemMetaDataFactory;
import com.themodernway.server.core.file.vfs.IFileItemStorage;
import com.themodernway.server.core.file.vfs.IFileItemWrapper;
import com.themodernway.server.core.file.vfs.IFolderItem;
import com.themodernway.server.core.file.vfs.IFolderItemWrapper;
import com.themodernway.server.core.file.vfs.ItemsOptions;
import com.themodernway.server.core.file.vfs.cache.IFileItemCache;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public abstract class AbstractFileItemStorage implements IFileItemStorage, InitializingBean, ICoreCommon, IHasLogging
{
    private final String             m_name;

    private final String             m_base;

    private IFileItemCache           m_keep = CommonOps.NULL();

    private ICoreContentTypeMapper   m_maps = CommonOps.NULL();

    private IFileItemMetaDataFactory m_meta = CommonOps.NULL();

    private final AtomicBoolean      m_attr = new AtomicBoolean(true);

    private final AtomicBoolean      m_mods = new AtomicBoolean(true);

    private final AtomicBoolean      m_open = new AtomicBoolean(false);

    private final Logger             m_logs = LoggingOps.getLogger(getClass());

    protected AbstractFileItemStorage(final String name, final String base)
    {
        m_name = requireTrimOrNull(name);

        m_base = requireTrimOrNull(FileAndPathUtils.normalize(base));
    }

    @Override
    public Logger logger()
    {
        return m_logs;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public void close() throws IOException
    {
        setOpen(false);

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("FileItemStorage(%s,%s).close().", getName(), getBasePath()));
        }
    }

    @Override
    public void setOpen(final boolean open)
    {
        m_open.set(open);
    }

    @Override
    public boolean isOpen()
    {
        return m_open.get();
    }

    @Override
    public boolean isWritable()
    {
        return m_mods.get();
    }

    @Override
    public void setWritable(final boolean mods)
    {
        m_mods.set(mods);
    }

    @Override
    public boolean isAttributesPreferred()
    {
        return m_attr.get();
    }

    @Override
    public void setAttributesPreferred(final boolean attr)
    {
        m_attr.set(attr);
    }

    @Override
    public void validate() throws IOException
    {
        if (false == isOpen())
        {
            throw new IOException(format("FileItemStorage(%s,%s) is closed.", getName(), getBasePath()));
        }
    }

    @Override
    public String getBasePath()
    {
        return m_base;
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
    public ICoreContentTypeMapper getContentTypeMapper()
    {
        return m_maps;
    }

    @Override
    public void setContentTypeMapper(final ICoreContentTypeMapper maps)
    {
        m_maps = maps;
    }

    @Override
    public IFileItemCache getFileItemCache()
    {
        return m_keep;
    }

    @Override
    public void setFileItemCache(final IFileItemCache keep)
    {
        m_keep = keep;
    }

    @Override
    public IFileItem find(final String name) throws IOException
    {
        return getRoot().find(name);
    }

    @Override
    public IFileItem file(final String name) throws IOException
    {
        return getRoot().file(name);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (null == getContentTypeMapper())
        {
            setContentTypeMapper(getServerContext().getContentTypeMapper());
        }
    }

    protected static abstract class AbstractFileItemAttributes implements IFileItemAttributes
    {
        protected static final int FLAG_H = 1;

        protected static final int FLAG_E = 2;

        protected static final int FLAG_R = 4;

        protected static final int FLAG_W = 8;

        protected static final int FLAG_F = 16;

        protected static final int FLAG_D = 32;

        protected int              m_bits = 0;

        protected IOException      m_oops = null;

        protected static final int BITS(final int bits, final int flag)
        {
            return (bits | flag);
        }

        protected static final boolean TEST(final int bits, final int flag)
        {
            return ((bits & flag) == flag);
        }

        @Override
        public boolean exists()
        {
            return TEST(m_bits, FLAG_E);
        }

        @Override
        public boolean isHidden()
        {
            return TEST(m_bits, FLAG_H);
        }

        @Override
        public boolean isReadable()
        {
            return TEST(m_bits, FLAG_R);
        }

        @Override
        public boolean isWritable()
        {
            return TEST(m_bits, FLAG_W);
        }

        @Override
        public boolean isFile()
        {
            return TEST(m_bits, FLAG_F);
        }

        @Override
        public boolean isFolder()
        {
            return TEST(m_bits, FLAG_D);
        }

        @Override
        public boolean isValidForReading()
        {
            return exists() && isFile() && isReadable() && (false == isHidden());
        }

        public IOException getException()
        {
            return m_oops;
        }
    }

    protected static abstract class AbstractFileItem implements IFileItem, ICoreCommon, IHasLogging
    {
        private static final Logger    s_logs = LoggingOps.getLogger(AbstractFileItem.class);

        private final IFileItemStorage m_stor;

        protected AbstractFileItem(final IFileItemStorage stor)
        {
            m_stor = requireNonNull(stor);
        }

        @Override
        public Logger logger()
        {
            return s_logs;
        }

        @Override
        public IFileItemWrapper wrap()
        {
            return new FileItemWrapper(this);
        }

        @Override
        public void validate() throws IOException
        {
            getFileItemStorage().validate();
        }

        @Override
        public long checksum() throws IOException
        {
            return IO.checksum(this);
        }

        @Override
        public String getName() throws IOException
        {
            return toTrimOrElse(FileAndPathUtils.name(getPath()), FileAndPathUtils.SINGLE_SLASH);
        }

        @Override
        public String getBaseName() throws IOException
        {
            return toTrimOrElse(FileAndPathUtils.base(getPath()), StringOps.EMPTY_STRING);
        }

        @Override
        public String getExtension() throws IOException
        {
            return toTrimOrElse(FileAndPathUtils.extn(getAbsolutePath()), StringOps.EMPTY_STRING);
        }

        @Override
        public String getPath() throws IOException
        {
            return FileAndPathUtils.normalize(getAbsolutePath().replace(getFileItemStorage().getBasePath(), FileAndPathUtils.SINGLE_SLASH));
        }

        @Override
        public IFolderItem getRoot() throws IOException
        {
            validate();

            return getFileItemStorage().getRoot();
        }

        @Override
        public Stream<String> lines() throws IOException
        {
            return lines(false);
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            return IO.copy(this, requireNonNull(output));
        }

        @Override
        public long writeTo(final Writer output) throws IOException
        {
            return IO.copy(this, requireNonNull(output));
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
            final IFileItemAttributes attr = getAttributes();

            return new JSONObject().set("path", getPath()).set("size", getSize()).set("last", getLastModified()).set("type", getContentType()).set("mode", format("%s%s%s", (attr.isFolder() ? "d" : StringOps.MINUS_STRING), (attr.isReadable() ? "r" : StringOps.MINUS_STRING), (attr.isWritable() ? "w" : StringOps.MINUS_STRING)));
        }

        @Override
        public String getContentType() throws IOException
        {
            validate();

            return getFileItemStorage().getContentTypeMapper().getContentType(getAbsolutePath());
        }

        @Override
        public IFileItemStorage getFileItemStorage()
        {
            return m_stor;
        }
    }

    protected static abstract class AbstractFolderItem extends AbstractFileItem implements IFolderItem
    {
        private static final Logger s_logs = LoggingOps.getLogger(AbstractFolderItem.class);

        protected AbstractFolderItem(final IFileItemStorage stor)
        {
            super(stor);
        }

        @Override
        public Logger logger()
        {
            return s_logs;
        }

        @Override
        public IFolderItemWrapper wrap()
        {
            return new FolderItemWrapper(this);
        }

        @Override
        public Stream<? extends IFileItem> items(final ItemsOptions... options) throws IOException
        {
            return items(ItemsOptions.make(options));
        }

        @Override
        public Stream<? extends IFileItem> items(final List<ItemsOptions> options) throws IOException
        {
            return items(ItemsOptions.make(options));
        }

        @Override
        public IFolderItem getAsFolderItem() throws IOException
        {
            super.validate();

            return this;
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            super.validate();

            throw new IOException(format("Can't stream folder (%s).", getPath()));
        }

        @Override
        public BufferedReader getBufferedReader() throws IOException
        {
            super.validate();

            throw new IOException(format("Can't read folder (%s).", getPath()));
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            super.validate();

            throw new IOException(format("Can't stream folder (%s).", getPath()));
        }

        @Override
        public long writeTo(final Writer output) throws IOException
        {
            super.validate();

            throw new IOException(format("Can't write folder (%s).", getPath()));
        }

        @Override
        public IFileItem find(final String name) throws IOException
        {
            final IFileItem item = file(name);

            if ((null != item) && (item.exists()))
            {
                return item;
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
            try (InputStream stream = IO.toInputStream(path))
            {
                return create(name, stream);
            }
        }

        @Override
        public IFileItem create(final String name, final Resource resource) throws IOException
        {
            try (InputStream stream = resource.getInputStream())
            {
                return create(name, stream);
            }
        }

        @Override
        public IFileItem create(final String name, final Reader reader) throws IOException
        {
            try (InputStream stream = new ReaderInputStream(reader, IO.UTF_8_CHARSET))
            {
                return create(name, stream);
            }
        }
    }
}
