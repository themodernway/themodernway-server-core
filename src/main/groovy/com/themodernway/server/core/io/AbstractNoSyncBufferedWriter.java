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
import java.io.Writer;
import java.util.Objects;

public abstract class AbstractNoSyncBufferedWriter<T extends AbstractNoSyncBufferedWriter<T>> extends AbstractBufferedWriter<T>
{
    private Writer m_writer;

    private int    m_sizeof;

    private int    m_nextch;

    private char   m_charbf[];

    public AbstractNoSyncBufferedWriter(final Writer writer)
    {
        this(Objects.requireNonNull(writer), IO.DEFAULT_BUFFER_CAPACITY);
    }

    public AbstractNoSyncBufferedWriter(final Writer writer, final int capacity)
    {
        super(Objects.requireNonNull(writer), IO.toValidBufferCapacity(capacity));

        m_writer = writer;

        m_sizeof = IO.toValidBufferCapacity(capacity);

        m_charbf = new char[m_sizeof];
    }

    protected void doEnsuredOpen() throws IOException
    {
        if (isClosed())
        {
            throw new IOException("Writer closed");
        }
    }

    protected void doFlushBuffer() throws IOException
    {
        doEnsuredOpen();

        if (0 == m_nextch)
        {
            return;
        }
        m_writer.write(m_charbf, 0, m_nextch);

        m_nextch = 0;
    }

    @Override
    public void write(final int c) throws IOException
    {
        doEnsuredOpen();

        if (m_nextch >= m_sizeof)
        {
            doFlushBuffer();
        }
        m_charbf[m_nextch++] = ((char) c);
    }

    @Override
    public void write(final char buf[], final int off, final int len) throws IOException
    {
        doEnsuredOpen();

        if ((off < 0) || (off > buf.length) || (len < 0) || ((off + len) > buf.length) || ((off + len) < 0))
        {
            throw new IndexOutOfBoundsException();
        }
        if (0 == len)
        {
            return;
        }
        if (len >= m_sizeof)
        {
            doFlushBuffer();

            m_writer.write(buf, off, len);

            return;
        }
        int off_sets = off;

        final int total_sz = (off + len);

        while (off_sets < total_sz)
        {
            final int put_size = Math.min(m_sizeof - m_nextch, total_sz - off_sets);

            System.arraycopy(buf, off_sets, m_charbf, m_nextch, put_size);

            off_sets = off_sets + put_size;

            m_nextch = m_nextch + put_size;

            if (m_nextch >= m_sizeof)
            {
                doFlushBuffer();
            }
        }
    }

    @Override
    public void write(final String s, final int off, final int len) throws IOException
    {
        doEnsuredOpen();

        int off_sets = off;

        final int total_sz = (off + len);

        while (off_sets < total_sz)
        {
            final int put_size = Math.min(m_sizeof - m_nextch, total_sz - off_sets);

            s.getChars(off_sets, off_sets + put_size, m_charbf, m_nextch);

            off_sets = off_sets + put_size;

            m_nextch = m_nextch + put_size;

            if (m_nextch >= m_sizeof)
            {
                doFlushBuffer();
            }
        }
    }

    @Override
    public void newLine() throws IOException
    {
        super.newLine();
    }

    @Override
    public void flush() throws IOException
    {
        doFlushBuffer();

        m_writer.flush();
    }

    @Override
    public void close() throws IOException
    {
        if (isClosed())
        {
            return;
        }
        try
        {
            flush();

            m_writer.close();
        }
        finally
        {
            clean();
        }
    }

    protected boolean isClosed()
    {
        return (null == m_writer);
    }

    protected void clean()
    {
        m_nextch = 0;

        m_sizeof = 0;

        m_writer = null;

        m_charbf = null;
    }
}
