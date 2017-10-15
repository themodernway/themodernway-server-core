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

package com.themodernway.server.core.file.vfs.simple;

import static com.themodernway.server.core.file.FileAndPathUtils.SINGLE_SLASH;
import static com.themodernway.server.core.file.FileAndPathUtils.concat;
import static com.themodernway.server.core.file.FileAndPathUtils.normalize;

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
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.NanoTimer;
import com.themodernway.server.core.file.FileAndPathUtils;
import com.themodernway.server.core.file.ICoreContentTypeMapper;
import com.themodernway.server.core.file.vfs.FileItemWrapper;
import com.themodernway.server.core.file.vfs.FolderItemWrapper;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFileItemMetaDataFactory;
import com.themodernway.server.core.file.vfs.IFileItemStorage;
import com.themodernway.server.core.file.vfs.IFileItemWrapper;
import com.themodernway.server.core.file.vfs.IFolderItem;
import com.themodernway.server.core.file.vfs.IFolderItemWrapper;
import com.themodernway.server.core.file.vfs.ItemsOptions;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.io.NoOpOutputStream;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;

public class SimpleFileItemStorage implements IFileItemStorage, ICoreCommon
{
    protected static final IFileItem MAKE(final File file, final IFileItemStorage stor)
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

    protected static final IFileItem MAKE(final Path path, final IFileItemStorage stor)
    {
        return MAKE(path.toFile(), stor);
    }

    private static final Logger      logger = Logger.getLogger(SimpleFileItemStorage.class);

    private final File               m_file;

    private final String             m_name;

    private final String             m_base;

    private final IFolderItem        m_root;

    private boolean                  m_mods = true;

    private boolean                  m_open = false;

    private ICoreContentTypeMapper   m_maps = null;

    private IFileItemMetaDataFactory m_meta = null;

    public SimpleFileItemStorage(final String name, final String base)
    {
        m_name = requireTrimOrNull(name);

        m_base = requireTrimOrNull(normalize(requireTrimOrNull(failIfNullBytePresent(base))));

        m_file = new File(m_base);

        m_root = new SimpleFolderItem(m_file, this);

        if ((IO.exists(m_file)) && (IO.isFolder(m_file)) && (IO.isReadable(m_file)))
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
    public boolean isWritable()
    {
        return m_mods;
    }

    public void setWritable(final boolean mods)
    {
        m_mods = mods;
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
        public String getContentType() throws IOException
        {
            validate();

            final ICoreContentTypeMapper maps = getFileItemStorage().getContentTypeMapper();

            if (null != maps)
            {
                return maps.getContentType(getFile());
            }
            return FileAndPathUtils.getContentType(getFile());
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

            return new JSONObject().set("path", getPath()).set("size", getSize()).set("last", getLastModified()).set("type", getContentType()).set("mode", String.format("%s%s%s", (isFolder(file) ? "d" : "-"), (isReadable(file) ? "r" : "-"), (isWritable(file, getFileItemStorage()) ? "w" : "-")));
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

            final File file = readtest(getFile());

            if (isFolder(file))
            {
                return getAsFolderItem().items().map(f -> f.wrap().getName());
            }
            else
            {
                return IO.lines(file);
            }
        }

        @Override
        public String getName() throws IOException
        {
            return toTrimOrElse(FileAndPathUtils.name(getPath()), SINGLE_SLASH);
        }

        @Override
        public String getBaseName() throws IOException
        {
            return toTrimOrElse(FileAndPathUtils.base(getPath()), EMPTY_STRING);
        }

        @Override
        public String getExtension() throws IOException
        {
            return toTrimOrElse(FileAndPathUtils.extn(getAbsolutePath()), EMPTY_STRING);
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
            return Files.deleteIfExists(getFile().toPath());
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

        protected File getFile()
        {
            return m_file;
        }

        protected void readtest() throws IOException
        {
            readtest(getFile());
        }

        protected File readtest(final File file) throws IOException
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
            return file;
        }

        protected String nametest(String name, final boolean make) throws IOException
        {
            validate();

            name = failIfNullBytePresent(requireNonNull(name));

            if ((make) && (false == getFileItemStorage().isWritable()))
            {
                throw new IOException(format("Can't create nowrite (%s).", name));
            }
            return name;
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
            validate();

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

                    final ArrayList<Path> list = new ArrayList<Path>();

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
                            if (Files.isHidden(path))
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
                            if ((node) && (false == Files.isHidden(path)))
                            {
                                list.add(path);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    };
                    Files.walkFileTree(root, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);

                    final IFileItemStorage stor = getFileItemStorage();

                    return list.stream().map(file -> MAKE(file, stor));
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
                        return normal(file -> file.isFile());
                    }
                    if (fold)
                    {
                        return normal(file -> file.isDirectory());
                    }
                }
            }
            return Stream.empty();
        }

        private final Stream<IFileItem> normal(final Predicate<File> test)
        {
            final IFileItemStorage stor = getFileItemStorage();

            return Arrays.stream(getFile().listFiles()).filter(test.and(file -> false == file.isHidden())).map(file -> MAKE(file, stor));
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
            String path = normalize(nametest(name, false));

            if (false == path.startsWith(SINGLE_SLASH))
            {
                path = concat(getPath(), path);
            }
            final IFileItemStorage stor = getFileItemStorage();

            path = concat(stor.getBasePath(), toTrimOrElse(path, EMPTY_STRING));

            if (null != path)
            {
                return MAKE(new File(path), stor);
            }
            return null;
        }

        @Override
        public IFileItem create(final String name, final File file) throws IOException
        {
            return create(name, file.toPath());
        }

        @Override
        public IFileItem create(String name, final Path path) throws IOException
        {
            InputStream stream = null;

            name = nametest(name, true);

            try
            {
                stream = IO.toInputStream(path);

                return create(name, stream);
            }
            finally
            {
                IO.close(stream);
            }
        }

        @Override
        public IFileItem create(String name, final Resource resource) throws IOException
        {
            InputStream stream = null;

            name = nametest(name, true);

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
        public IFileItem create(String name, final Reader reader) throws IOException
        {
            InputStream stream = null;

            name = nametest(name, true);

            try
            {
                stream = new ReaderInputStream(reader, IO.UTF_8_CHARSET);

                return create(name, stream);
            }
            finally
            {
                IO.close(stream);
            }
        }

        @Override
        public IFileItem create(String name, final InputStream input) throws IOException
        {
            name = nametest(name, true);

            final IFileItem item = file(name);

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

                if ((false == file.getParentFile().exists()) && (false == file.getParentFile().mkdirs()))
                {
                    throw new IOException(format("Can't create folder (%s).", item.getPath()));
                }
                if (false == file.createNewFile())
                {
                    throw new IOException(format("Can't create file (%s).", item.getPath()));
                }
                final OutputStream fios = IO.toOutputStream(file);

                try
                {
                    IO.copy(input, fios);

                    fios.flush();

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

            throw new IOException(format("Can't stream folder (%s).", getPath()));
        }

        @Override
        public BufferedReader getBufferedReader() throws IOException
        {
            validate();

            throw new IOException(format("Can't read folder (%s).", getPath()));
        }

        @Override
        public long writeTo(final OutputStream output) throws IOException
        {
            validate();

            throw new IOException(format("Can't stream folder (%s).", getPath()));
        }

        @Override
        public long writeTo(final Writer output) throws IOException
        {
            validate();

            throw new IOException(format("Can't stream folder (%s).", getPath()));
        }
    }

    public static void main(final String... strings)
    {
        try (final SimpleFileItemStorage stor = new SimpleFileItemStorage("content", "/content"))
        {
            stor.getRoot().wrap().items(ItemsOptions.RECURSIVE).forEach(item -> System.out.format("file (%s) type (%s) base(%s).\n", item.getPath(), item.getContentType(), item.getBaseName()));

            stor.getRoot().wrap().items(ItemsOptions.FILE).filter(item -> item.getPath().endsWith(".json")).map(item -> item.lines()).flatMap(lines -> lines).forEach(line -> System.out.println(line));

            final JSONArray list = new JSONArray();

            stor.getRoot().wrap().items(ItemsOptions.FILE).filter(item -> item.getExtension().equals("json")).forEach(item -> cat(item, list));

            System.out.println(list.toJSONString());

            final IFileItem item = stor.getRoot().file("b.json");

            final OutputStream puts = new NoOpOutputStream();

            final NanoTimer t = new NanoTimer();

            for (int i = 0; i < 1000; i++)
            {
                cat(item, puts);
            }
            System.out.println(t.toString());
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void cat(final IFileItem item, final OutputStream ou)
    {
        try
        {
            item.writeTo(ou);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void cat(final IFileItem item, final JSONArray list)
    {
        final IBinder binder = BinderType.JSON.getBinder();

        try (InputStream is = item.getInputStream())
        {
            list.add(binder.bindJSON(is));
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}
