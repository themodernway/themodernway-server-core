/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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
import java.io.Reader;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

public class FolderItemWrapper extends AbstractFileItemWrapper<IFolderItem> implements IFolderItemWrapper
{
    public FolderItemWrapper(final IFolderItem fold)
    {
        super(fold);
    }

    @Override
    public IFolderItemWrapper wrap()
    {
        return this;
    }

    @Override
    public Stream<IFileItemWrapper> items(final ItemsOptions... options) throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().items(options).map(item -> item.wrap());
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public Stream<IFileItemWrapper> items(final List<ItemsOptions> options) throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().items(options).map(item -> item.wrap());
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public Stream<IFileItemWrapper> items(final EnumSet<ItemsOptions> options) throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().items(options).map(item -> item.wrap());
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFileItemWrapper find(final String name) throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().find(name));
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFileItemWrapper file(final String name) throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().file(name));
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFileItemWrapper create(final String name, final File file) throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().create(name, file));
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFileItemWrapper create(final String name, final Path path) throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().create(name, path));
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFileItemWrapper create(final String name, final Reader reader) throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().create(name, reader));
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFileItemWrapper create(final String name, final Resource resource) throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().create(name, resource));
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFileItemWrapper create(final String name, final InputStream input) throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().create(name, input));
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }
}
