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

package com.themodernway.server.core.file.vfs.simple;

import static com.themodernway.server.core.file.FileUtils.SINGLE_SLASH;
import static com.themodernway.server.core.file.FileUtils.concat;
import static com.themodernway.server.core.file.FileUtils.normalize;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.content.ICoreContentTypeMapper;
import com.themodernway.server.core.file.FileUtils;
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
import com.themodernway.server.core.logging.LoggingOps;

public class SimpleFileItemStorage implements IFileItemStorage, InitializingBean, ICoreCommon
{
    protected static final IFileItem make(final File file, final IFileItemStorage stor)
    {
        if (IO.isFolder(file))
        {
            return new SimpleFolderItem(file, stor);
        }
        else
        {
            return new SimpleFileItem(file, stor);
        }
    }

    protected static final IFileItem make(final Path path, final IFileItemStorage stor)
    {
        return make(path.toFile(), stor);
    }

    private static final Logger      logger = LoggingOps.getLogger(SimpleFileItemStorage.class);

    private final File               m_file;

    private final String             m_name;

    private final String             m_base;

    private final IFolderItem        m_root;

    private IFileItemCache           m_keep = null;

    private ICoreContentTypeMapper   m_maps = null;

    private IFileItemMetaDataFactory m_meta = null;

    private final AtomicBoolean      m_attr = new AtomicBoolean(true);

    private final AtomicBoolean      m_mods = new AtomicBoolean(true);

    private final AtomicBoolean      m_open = new AtomicBoolean(false);

    public SimpleFileItemStorage(final String name, final String base)
    {
        m_name = requireTrimOrNull(name);

        m_base = requireTrimOrNull(normalize(base));

        m_file = new File(m_base);

        m_root = new SimpleFolderItem(m_file, this);

        if ((IO.exists(m_file)) && (IO.isFolder(m_file)) && (IO.isReadable(m_file)))
        {
            m_open.set(true);

            if (logger.isInfoEnabled())
            {
                logger.info(LoggingOps.THE_MODERN_WAY_MARKER, format("SimpleFileItemStorage(%s,%s) open.", m_name, m_base));
            }
        }
        else if (logger.isErrorEnabled())
        {
            logger.error(LoggingOps.THE_MODERN_WAY_MARKER, format("SimpleFileItemStorage(%s,%s) can't access.", m_name, m_base));
        }
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
    public boolean isOpen()
    {
        return m_open.get();
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (null == getContentTypeMapper())
        {
            setContentTypeMapper(getServerContext().getContentTypeMapper());
        }
    }

    @Override
    public void setOpen(final boolean open)
    {
        m_open.set(open);
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
    public IFolderItem getRoot() throws IOException
    {
        validate();

        return m_root;
    }

    @Override
    public void close() throws IOException
    {
        m_open.set(false);

        if (logger.isInfoEnabled())
        {
            logger.info(LoggingOps.THE_MODERN_WAY_MARKER, format("SimpleFileItemStorage(%s,%s).close().", getName(), getBasePath()));
        }
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
    public boolean isAttributesPreferred()
    {
        return m_attr.get();
    }

    @Override
    public void setAttributesPreferred(final boolean attr)
    {
        m_attr.set(attr);
    }

    protected static class SimpleFileItemAttributes implements IFileItemAttributes
    {
        protected static final int FLAG_H = 1;

        protected static final int FLAG_E = 2;

        protected static final int FLAG_R = 4;

        protected static final int FLAG_W = 8;

        protected static final int FLAG_F = 16;

        protected static final int FLAG_D = 32;

        private int                m_bits = 0;

        private IOException        m_oops = null;

        protected static final int BITS(final int bits, final int flag)
        {
            return (bits | flag);
        }

        protected static final boolean TEST(final int bits, final int flag)
        {
            return ((bits & flag) == flag);
        }

        public SimpleFileItemAttributes(final SimpleFileItem item)
        {
            try
            {
                final File file = item.getFile();

                if (item.isHidden())
                {
                    m_bits = BITS(m_bits, FLAG_H);
                }
                if (item.exists(file))
                {
                    m_bits = BITS(m_bits, FLAG_E);

                    if (item.isReadable(file))
                    {
                        m_bits = BITS(m_bits, FLAG_R);
                    }
                    if (item.isWritable(file, item.getFileItemStorage()))
                    {
                        m_bits = BITS(m_bits, FLAG_W);
                    }
                    if (item.isFile(file))
                    {
                        m_bits = BITS(m_bits, FLAG_F);
                    }
                    else if (item.isFolder(file))
                    {
                        m_bits = BITS(m_bits, FLAG_D);
                    }
                }
            }
            catch (final IOException e)
            {
                m_bits = 0;

                m_oops = e;
            }
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

    protected static class SimpleFileItem implements IFileItem, ICoreCommon
    {
        private final File                                  m_file;

        private final IFileItemStorage                      m_stor;

        private final ThreadLocal<SimpleFileItemAttributes> m_attr;

        public SimpleFileItem(final File file, final IFileItemStorage stor)
        {
            m_file = requireNonNull(file);

            m_stor = requireNonNull(stor);

            m_attr = ThreadLocal.withInitial(() -> new SimpleFileItemAttributes(this));
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
        public String getContentType() throws IOException
        {
            validate();

            return getFileItemStorage().getContentTypeMapper().getContentType(getFile());
        }

        @Override
        public String getContentAsString() throws IOException
        {
            validate();

            final File file = readtest(getFile());

            if (isFolder(file))
            {
                return getAsFolderItem().items().map(f -> f.wrap().getName()).collect(Collectors.joining(FileUtils.SYS_JOIN_NL_STRING)).concat(FileUtils.SYS_JOIN_NL_STRING);
            }
            else
            {
                if (FileUtils.isSystemWindows())
                {
                    return IO.getStringAtMost(file, file.length()).replace(FileUtils.SYS_JOIN_CR_STRING, StringOps.EMPTY_STRING);
                }
                else
                {
                    return IO.getStringAtMost(file, file.length());
                }
            }
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
            final File file = getFile();

            return new JSONObject().set("path", getPath()).set("size", getSize()).set("last", getLastModified()).set("type", getContentType()).set("mode", format("%s%s%s", (isFolder(file) ? "d" : "-"), (isReadable(file) ? "r" : "-"), (isWritable(file, getFileItemStorage()) ? "w" : "-")));
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
            return lines(false);
        }

        @Override
        public Stream<String> lines(final boolean greedy) throws IOException
        {
            validate();

            final File file = readtest(getFile());

            if (isFolder(file))
            {
                return getAsFolderItem().items().map(f -> f.wrap().getName());
            }
            else
            {
                return IO.lines(file, greedy);
            }
        }

        @Override
        public String getName() throws IOException
        {
            return toTrimOrElse(FileUtils.name(getPath()), SINGLE_SLASH);
        }

        @Override
        public String getBaseName() throws IOException
        {
            return toTrimOrElse(FileUtils.base(getPath()), StringOps.EMPTY_STRING);
        }

        @Override
        public String getExtension() throws IOException
        {
            return toTrimOrElse(FileUtils.extn(getAbsolutePath()), StringOps.EMPTY_STRING);
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

            if (isHidden(getFile()))
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

            return isReadable(getFile());
        }

        @Override
        public boolean isWritable() throws IOException
        {
            validate();

            return isWritable(getFile(), getFileItemStorage());
        }

        @Override
        public boolean isFile() throws IOException
        {
            validate();

            return isFile(getFile());
        }

        @Override
        public boolean isFolder() throws IOException
        {
            validate();

            return isFolder(getFile());
        }

        @Override
        public String getPath() throws IOException
        {
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
            if (normalize(getAbsolutePath()).equals(normalize(getFileItemStorage().getBasePath())))
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
            final File file = getFile();

            if (isFolder(file))
            {
                return new SimpleFolderItem(file, getFileItemStorage());
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

            final File file = readtest(getFile());

            if (isFolder(file))
            {
                throw new IOException(format("Can't stream folder (%s).", getPath()));
            }
            return IO.toInputStream(file);
        }

        @Override
        public BufferedReader getBufferedReader() throws IOException
        {
            validate();

            final File file = readtest(getFile());

            if (isFolder(file))
            {
                throw new IOException(format("Can't read folder (%s).", getPath()));
            }
            return IO.toBufferedReader(file);
        }

        @Override
        public long getLastModified() throws IOException
        {
            validate();

            return getFile().lastModified();
        }

        @Override
        public boolean exists() throws IOException
        {
            validate();

            return exists(getFile());
        }

        @Override
        public boolean delete() throws IOException
        {
            validate();

            if (false == getFileItemStorage().isWritable())
            {
                throw new IOException(format("Can't delete nowrite (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't delete hidden (%s).", getPath()));
            }
            return IO.delete(getFile());
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
        public IFileItemStorage getFileItemStorage()
        {
            return m_stor;
        }

        @Override
        public IFileItemAttributes getAttributes() throws IOException
        {
            final SimpleFileItemAttributes attr = m_attr.get();

            if (null != attr.getException())
            {
                throw attr.getException();
            }
            return attr;
        }

        protected File getFile()
        {
            return m_file;
        }

        protected File readtest(final File file) throws IOException
        {
            if (getFileItemStorage().isAttributesPreferred())
            {
                final IFileItemAttributes attr = getAttributes();

                if (false == attr.exists())
                {
                    throw new IOException(format("Can't read missing (%s).", getPath()));
                }
                if (false == attr.isReadable())
                {
                    throw new IOException(format("Can't read (%s).", getPath()));
                }
                if (attr.isHidden())
                {
                    throw new IOException(format("Can't read hidden (%s).", getPath()));
                }
            }
            else
            {
                if (false == exists(file))
                {
                    throw new IOException(format("Can't read missing (%s).", getPath()));
                }
                if (false == isReadable(file))
                {
                    throw new IOException(format("Can't read (%s).", getPath()));
                }
                if (isHidden())
                {
                    throw new IOException(format("Can't read hidden (%s).", getPath()));
                }
            }
            return file;
        }

        protected boolean exists(final File file)
        {
            return IO.exists(file);
        }

        protected boolean isFolder(final File file)
        {
            return IO.isFolder(file);
        }

        protected boolean isFile(final File file)
        {
            return IO.isFile(file);
        }

        protected boolean isReadable(final File file)
        {
            return IO.isReadable(file);
        }

        protected boolean isWritable(final File file)
        {
            return IO.isWritable(file);
        }

        protected boolean isHidden(final File file)
        {
            return IO.isHidden(file);
        }

        protected boolean isWritable(final File file, final IFileItemStorage stor)
        {
            if ((null != stor) && (false == stor.isWritable()))
            {
                return false;
            }
            return IO.isWritable(file);
        }
    }

    protected static class SimpleFolderItem extends SimpleFileItem implements IFolderItem, ICoreCommon
    {
        public SimpleFolderItem(final File file, final IFileItemStorage stor)
        {
            super(file, stor);
        }

        @Override
        public IFolderItemWrapper wrap()
        {
            return new FolderItemWrapper(this);
        }

        @Override
        public Stream<IFileItem> items(final ItemsOptions... options) throws IOException
        {
            return items(ItemsOptions.make(options));
        }

        @Override
        public Stream<IFileItem> items(final List<ItemsOptions> options) throws IOException
        {
            return items(ItemsOptions.make(options));
        }

        @Override
        public Stream<IFileItem> items(EnumSet<ItemsOptions> options) throws IOException
        {
            super.validate();

            final File self = readtest(getFile());

            if (isFolder(self))
            {
                if (options.isEmpty())
                {
                    return normal(file -> true);
                }
                else if (options.contains(ItemsOptions.RECURSIVE))
                {
                    if (options.size() == 1)
                    {
                        options = EnumSet.of(ItemsOptions.FILE, ItemsOptions.FOLDER);
                    }
                    final boolean node = options.contains(ItemsOptions.FILE);

                    final boolean fold = options.contains(ItemsOptions.FOLDER);

                    final List<Path> list = new ArrayList<>();

                    final Path root = self.toPath();

                    final SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>()
                    {
                        @Override
                        public FileVisitResult preVisitDirectory(final Path path, final BasicFileAttributes attr) throws IOException
                        {
                            if (root.equals(path))
                            {
                                return FileVisitResult.CONTINUE;
                            }
                            if (IO.isHidden(path))
                            {
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                            if (fold)
                            {
                                list.add(path);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(final Path path, final BasicFileAttributes attr) throws IOException
                        {
                            if ((node) && (false == IO.isHidden(path)))
                            {
                                list.add(path);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    };
                    Files.walkFileTree(root, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);

                    final IFileItemStorage stor = getFileItemStorage();

                    return list.stream().map(file -> make(file, stor));
                }
                else
                {
                    final boolean node = options.contains(ItemsOptions.FILE);

                    final boolean fold = options.contains(ItemsOptions.FOLDER);

                    if ((node) && (fold))
                    {
                        return normal(file -> true);
                    }
                    if (node)
                    {
                        return normal(file -> IO.isFile(file));
                    }
                    if (fold)
                    {
                        return normal(file -> IO.isFolder(file));
                    }
                }
            }
            return Stream.empty();
        }

        private final Stream<IFileItem> normal(final Predicate<File> test)
        {
            final IFileItemStorage stor = getFileItemStorage();

            return Arrays.stream(getFile().listFiles()).filter(test.and(file -> false == IO.isHidden(file))).map(file -> make(file, stor));
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
        public IFileItem file(final String name) throws IOException
        {
            super.validate();

            String path = normalize(name);

            if (false == path.startsWith(SINGLE_SLASH))
            {
                path = concat(getPath(), path);
            }
            final IFileItemStorage stor = getFileItemStorage();

            path = concat(stor.getBasePath(), toTrimOrElse(path, StringOps.EMPTY_STRING));

            if (null != path)
            {
                return make(new File(path), stor);
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

        @Override
        public IFileItem create(final String name, final InputStream input) throws IOException
        {
            final IFileItem item = file(name);

            if (null != item)
            {
                if (false == getFileItemStorage().isWritable())
                {
                    throw new IOException(format("Can't create nowrite (%s).", item.getPath()));
                }
                if (item.isFolder())
                {
                    throw new IOException(format("Can't create folder (%s).", item.getPath()));
                }
                if (item.isHidden())
                {
                    throw new IOException(format("Can't create hidden (%s).", item.getPath()));
                }
                final File file = new File(item.getAbsolutePath());

                final File parn = file.getParentFile();

                if ((null != parn) && (false == parn.mkdirs()) && (false == IO.isFolder(parn)))
                {
                    throw new IOException(format("Can't create folder (%s).", item.getPath()));
                }
                if ((IO.exists(file)) && (false == IO.isWritable(file)))
                {
                    throw new IOException(format("Can't replace file (%s).", item.getPath()));
                }
                try (OutputStream fios = IO.toOutputStream(file))
                {
                    IO.copy(input, fios);

                    fios.flush();

                    return make(file, getFileItemStorage());
                }
            }
            throw new IOException(format("Can't resolve (%s).", name));
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
    }

}
