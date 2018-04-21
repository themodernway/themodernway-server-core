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

import static com.themodernway.common.api.java.util.CommonOps.requireNonNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.fasterxml.jackson.dataformat.yaml.UTF8Reader;
import com.themodernway.server.core.file.vfs.FileStorageException;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFileItemWrapper;
import com.themodernway.server.core.io.IO;

public class BasicFileItemCacheNode implements IFileItemCacheNode
{
    private final long   m_last;

    private final long   m_size;

    private final byte[] m_buff;

    private final String m_name;

    private final String m_type;

    protected static final byte[] getbytes(final IFileItem file, final long size)
    {
        try
        {
            return IO.getbytes(file, size);
        }
        catch (final IOException e)
        {
            throw new FileStorageException(e);
        }
    }

    public BasicFileItemCacheNode(final String name, final IFileItem file)
    {
        this(name, file.wrap());
    }

    public BasicFileItemCacheNode(final String name, final IFileItemWrapper wrap)
    {
        m_name = requireNonNull(name);

        m_size = wrap.getSize();

        m_type = wrap.getContentType();

        m_last = wrap.getLastModified();

        m_buff = getbytes(wrap, m_size);
    }

    @Override
    public long getSize()
    {
        return m_size;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public long getLastModified()
    {
        return m_last;
    }

    @Override
    public String getContentType()
    {
        return m_type;
    }

    @Override
    public byte[] getBytes()
    {
        return Arrays.copyOf(m_buff, m_buff.length);
    }

    @Override
    public InputStream getInputStream()
    {
        return new ByteArrayInputStream(m_buff);
    }

    @Override
    public BufferedReader getBufferedReader()
    {
        return new BufferedReader(new UTF8Reader(m_buff, 0, m_buff.length, false), m_buff.length);
    }
}
