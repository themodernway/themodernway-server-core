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
import java.util.Objects;
import java.util.stream.Stream;

import com.themodernway.server.core.json.JSONObject;

public abstract class AbstractFileItemWrapper<T extends IFileItem> extends AbstractWrappedItem<T> implements IFileItemWrapper
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
        return getWrappedItem().getFileItemStorage();
    }

    @Override
    public String getName() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getName();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public long getSize() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getSize();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean exists() throws FileStorageException
    {
        try
        {
            return getWrappedItem().exists();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isHidden() throws FileStorageException
    {
        try
        {
            return getWrappedItem().isHidden();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isReadable() throws FileStorageException
    {
        try
        {
            return getWrappedItem().isReadable();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isWritable() throws FileStorageException
    {
        try
        {
            return getWrappedItem().isWritable();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isFile() throws FileStorageException
    {
        try
        {
            return getWrappedItem().isFile();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean isFolder() throws FileStorageException
    {
        try
        {
            return getWrappedItem().isFolder();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public boolean delete() throws FileStorageException
    {
        try
        {
            return getWrappedItem().delete();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public long getLastModified() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getLastModified();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public long getFileSizeLimit() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getFileSizeLimit();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public String getPath() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getPath();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public String getAbsolutePath() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getAbsolutePath();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public String getContentType() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getContentType();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public JSONObject getMetaData() throws FileStorageException
    {
        try
        {
            return getWrappedItem().getMetaData();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFolderItemWrapper getRoot() throws FileStorageException
    {
        try
        {
            return cast(getWrappedItem().getRoot());
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFolderItemWrapper getParent() throws FileStorageException
    {
        try
        {
            return cast(getWrappedItem().getParent());
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public IFolderItemWrapper getAsFolderItem() throws FileStorageException
    {
        try
        {
            return cast(getWrappedItem().getAsFolderItem());
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public void validate() throws IOException
    {
        getWrappedItem().validate();
    }

    @Override
    public Stream<String> lines() throws FileStorageException
    {
        try
        {
            return getWrappedItem().lines();
        }
        catch (IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return getWrappedItem().getInputStream();
    }

    @Override
    public long writeTo(final OutputStream output) throws IOException
    {
        return getWrappedItem().writeTo(Objects.requireNonNull(output));
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
