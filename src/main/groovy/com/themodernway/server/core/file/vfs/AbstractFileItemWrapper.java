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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.stream.Stream;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;

public abstract class AbstractFileItemWrapper<T extends IFileItem> extends AbstractWrappedFileItem<T> implements IFileItemWrapper
{
    protected AbstractFileItemWrapper(final T item)
    {
        super(item);
    }

    @Override
    public IFileItemWrapper wrap()
    {
        return this;
    }

    @Override
    public IFileItemStorage getFileItemStorage()
    {
        return getWrappedFileItem().getFileItemStorage();
    }

    @Override
    public String getName() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getName();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public String getBaseName() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getBaseName();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public long getSize() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getSize();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean exists() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().exists();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isHidden() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().isHidden();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isReadable() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().isReadable();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isWritable() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().isWritable();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isFile() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().isFile();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isFolder() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().isFolder();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean delete() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().delete();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public long getLastModified() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getLastModified();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public long getFileSizeLimit() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getFileSizeLimit();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public String getPath() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getPath();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public String getAbsolutePath() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getAbsolutePath();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public String getContentType() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getContentType();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public JSONObject getMetaData() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getMetaData();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFolderItemWrapper getRoot() throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().getRoot());
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFolderItemWrapper getParent() throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().getParent());
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFolderItemWrapper getAsFolderItem() throws FileStorageException
    {
        try
        {
            return cast(getWrappedFileItem().getAsFolderItem());
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public void validate() throws IOException
    {
        getWrappedFileItem().validate();
    }

    @Override
    public Stream<String> lines() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().lines();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return getWrappedFileItem().getInputStream();
    }

    @Override
    public String getExtension() throws FileStorageException
    {
        try
        {
            return getWrappedFileItem().getExtension();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public long writeTo(final Writer output) throws IOException
    {
        return getWrappedFileItem().writeTo(CommonOps.requireNonNull(output));
    }

    @Override
    public long writeTo(final OutputStream output) throws IOException
    {
        return getWrappedFileItem().writeTo(CommonOps.requireNonNull(output));
    }

    protected IFileItemWrapper cast(final IFileItem item)
    {
        if (null == item)
        {
            return null;
        }
        return item.wrap();
    }

    protected IFolderItemWrapper cast(final IFolderItem item)
    {
        if (null == item)
        {
            return null;
        }
        return item.wrap();
    }
}
