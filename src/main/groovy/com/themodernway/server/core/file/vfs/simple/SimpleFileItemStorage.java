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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
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
import com.themodernway.server.core.json.JSONObject;

public class SimpleFileItemStorage implements IFileItemStorage, ICoreCommon
{
    protected static final IFileItem MAKE(final File file, final IFileItemStorage stor)
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

    private ICoreContentTypeMapper   m_maps = null;

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

            if (false == exists())
            {
                throw new IOException(format("Can't type missing (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't type hidden (%s).", getPath()));
            }
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

            if (false == exists())
            {
                throw new IOException(format("Can't meta-data missing (%s).", getPath()));
            }
            if (isHidden())
            {
                throw new IOException(format("Can't meta-data hidden (%s).", getPath()));
            }
            final IFileItemMetaDataFactory fact = getFileItemStorage().getFileItemMetaDataFactory();

            if (null != fact)
            {
                final JSONObject json = fact.getMetaData(this);

                if (null != json)
                {
                    return json;
                }
            }
            return new JSONObject().set("path", getPath()).set("size", getSize()).set("last", getLastModified()).set("type", getContentType()).set("mode", String.format("%s%s%s", (isFolder() ? "d" : "-"), (isReadable() ? "r" : "-"), (isWritable() ? "w" : "-")));
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
                return getAsFolderItem().items().map(file -> file.wrap().getName());
            }
            else
            {
                return IO.lines(getFile());
            }
        }

        @Override
        public String getName() throws IOException
        {
            validate();

            return toTrimOrElse(FileAndPathUtils.name(getPath()), SINGLE_SLASH);
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

            return IO.toInputStream(getFile());
        }

        @Override
        public long getLastModified() throws IOException
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
            return getFile().lastModified();
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

            readtest();

            if (isFolder())
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

                    final ArrayList<File> list = new ArrayList<File>();

                    final Path root = getFile().toPath();

                    final SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>()
                    {
                        @Override
                        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attr) throws IOException
                        {
                            if (root.equals(path))
                            {
                                return FileVisitResult.CONTINUE;
                            }
                            final File file = path.toFile();

                            if (file.isHidden())
                            {
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                            if (fold)
                            {
                                list.add(file);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException
                        {
                            final File file = path.toFile();

                            if ((node) && (false == file.isHidden()))
                            {
                                list.add(file);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    };
                    Files.walkFileTree(root, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);

                    return list.stream().map(file -> MAKE(file, getFileItemStorage()));
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
            return Arrays.stream(getFile().listFiles()).filter(file -> false == file.isHidden()).filter(test).map(file -> MAKE(file, getFileItemStorage()));
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
                return MAKE(new File(path), getFileItemStorage());
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
                stream = IO.toInputStream(path);

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
        public IFileItem create(final String name, final Reader reader) throws IOException
        {
            validate();

            InputStream stream = null;

            try
            {
                stream = new ReaderInputStream(reader);

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

    public static void main(String... strings)
    {
        try
        {
            SimpleFileItemStorage stor = new SimpleFileItemStorage("content", "/content");

            stor.getRoot().wrap().items(ItemsOptions.RECURSIVE).forEach(item -> System.out.println(String.format("file (%s) type (%s).", item.getName(), item.getMetaData())));

            stor.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
