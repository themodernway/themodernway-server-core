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

package com.themodernway.server.core.file.vfs.cache;

import java.io.IOException;
import java.util.function.Function;

import com.themodernway.server.core.file.vfs.FileStorageException;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFileItemAttributes;
import com.themodernway.server.core.file.vfs.IFileItemStorage;
import com.themodernway.server.core.file.vfs.IFolderItem;
import com.themodernway.server.core.logging.LoggingOps;

public class CaffieneFileItemCache extends AbstractCaffieneFileItemCache
{
    private final IFolderItem      m_root;

    private final IFileItemStorage m_stor;

    public CaffieneFileItemCache(final String name, final IFileItemStorage stor)
    {
        super(name);

        m_stor = stor;

        try
        {
            m_root = getFileItemStorage().getRoot();
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
        if (null == getFileItemStorage().getFileItemCache())
        {
            getFileItemStorage().setFileItemCache(this);
        }
    }

    public CaffieneFileItemCache(final IFileItemStorage stor)
    {
        this(stor.getName(), stor);
    }

    public final IFolderItem getRoot()
    {
        return m_root;
    }

    public final IFileItemStorage getFileItemStorage()
    {
        return m_stor;
    }

    @Override
    public Function<String, IFileItemCacheNode> getMappingFunction()
    {
        return name -> {

            try
            {
                final IFileItem file = getRoot().file(name);

                if (null != file)
                {
                    final IFileItemAttributes attr = file.getAttributes();

                    if ((null != attr) && (attr.isValidForReading()))
                    {
                        if (logger().isInfoEnabled())
                        {
                            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("cache(%s) item(%s) created.", getName(), name));
                        }
                        return new BasicFileItemCacheNode(name, file);
                    }
                }
            }
            catch (final IOException e)
            {
                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("cache(%s) item(%s) error.", getName(), name), e);
                }
                return null;
            }
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("cache(%s) item(%s) not found.", getName(), name));
            }
            return null;
        };
    }
}
