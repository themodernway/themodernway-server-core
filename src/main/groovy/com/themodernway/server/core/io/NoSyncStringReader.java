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

package com.themodernway.server.core.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

public class NoSyncStringReader extends Reader
{
    private String m_buff;

    private int    m_size;

    private int    m_next;

    private int    m_mark;

    public NoSyncStringReader(final String buff)
    {
        m_buff = Objects.requireNonNull(buff);

        m_size = m_buff.length();
    }

    private void ensureOpen() throws IOException
    {
        if (null == m_buff)
        {
            throw new IOException("NoSyncStringReader closed");
        }
    }

    @Override
    public int read() throws IOException
    {
        ensureOpen();

        if (m_next >= m_size)
        {
            return -1;
        }
        return m_buff.charAt(m_next++);
    }

    @Override
    public int read(final char chr[], final int off, final int len) throws IOException
    {
        ensureOpen();

        if ((off < 0) || (off > chr.length) || (len < 0) || ((off + len) > chr.length) || ((off + len) < 0))
        {
            throw new IndexOutOfBoundsException();
        }
        if (0 == len)
        {
            return 0;
        }
        if (m_next >= m_size)
        {
            return -1;
        }
        int siz = Math.min(m_size - m_next, len);

        m_buff.getChars(m_next, m_next + siz, chr, off);

        m_next += siz;

        return siz;
    }

    @Override
    public long skip(final long skip) throws IOException
    {
        ensureOpen();

        if (m_next >= m_size)
        {
            return 0;
        }
        long siz = Math.min(m_size - m_next, skip);

        siz = Math.max(-m_next, siz);

        m_next += siz;

        return siz;
    }

    @Override
    public boolean ready() throws IOException
    {
        ensureOpen();

        return true;
    }

    @Override
    public boolean markSupported()
    {
        return true;
    }

    @Override
    public void mark(final int limit) throws IOException
    {
        if (limit < 0)
        {
            throw new IllegalArgumentException("Read-ahead limit < 0");
        }
        ensureOpen();

        m_mark = m_next;
    }

    @Override
    public void reset() throws IOException
    {
        ensureOpen();

        m_next = m_mark;
    }

    @Override
    public void close() throws IOException
    {
        m_buff = null;
    }
}