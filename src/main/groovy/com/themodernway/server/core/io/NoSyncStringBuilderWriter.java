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

public class NoSyncStringBuilderWriter extends Writer implements CharSequence
{
    private final static int    MINIMUM_CAPACITY = 16;

    private final static int    DEFAULT_CAPACITY = 1024;

    private final static int    BOUNDRY_CAPACITY = Integer.MAX_VALUE - MINIMUM_CAPACITY;

    private final StringBuilder m_builder;

    private static final int toMinimumCapacity(int capacity)
    {
        if (capacity <= MINIMUM_CAPACITY)
        {
            return MINIMUM_CAPACITY;
        }
        if (capacity >= BOUNDRY_CAPACITY)
        {
            capacity = capacity - DEFAULT_CAPACITY;
        }
        return capacity + (capacity % MINIMUM_CAPACITY);
    }

    public NoSyncStringBuilderWriter()
    {
        this(new StringBuilder(DEFAULT_CAPACITY));
    }

    public NoSyncStringBuilderWriter(final StringBuilder builder)
    {
        super(Objects.requireNonNull(builder));

        m_builder = builder;
    }

    public NoSyncStringBuilderWriter(final int capacity)
    {
        this(new StringBuilder(toMinimumCapacity(capacity)));
    }

    public StringBuilder getStringBuilder()
    {
        return m_builder;
    }

    @Override
    public void write(final int c) throws IOException
    {
        m_builder.append((char) c);
    }

    @Override
    public void write(final char chr[], final int off, final int len) throws IOException
    {
        if (null != chr)
        {
            m_builder.append(chr, off, len);
        }
    }

    @Override
    public void write(final String str) throws IOException
    {
        if (null != str)
        {
            m_builder.append(str);
        }
    }

    @Override
    public void write(final String str, final int off, final int len) throws IOException
    {
        if (null != str)
        {
            m_builder.append(str.substring(off, off + len));
        }
    }

    @Override
    public NoSyncStringBuilderWriter append(final CharSequence chs) throws IOException
    {
        if (null != chs)
        {
            m_builder.append(chs);
        }
        return this;
    }

    @Override
    public NoSyncStringBuilderWriter append(final CharSequence chs, final int beg, final int end) throws IOException
    {
        if (null != chs)
        {
            m_builder.append(chs, beg, end);
        }
        return this;
    }

    @Override
    public NoSyncStringBuilderWriter append(final char c) throws IOException
    {
        m_builder.append(c);

        return this;
    }

    @Override
    public String toString()
    {
        return m_builder.toString();
    }

    @Override
    public void flush()
    {
    }

    @Override
    public void close() throws IOException
    {
    }

    public NoSyncStringBuilderWriter clear()
    {
        m_builder.setLength(0);

        return this;
    }

    @Override
    public int length()
    {
        return m_builder.length();
    }

    @Override
    public char charAt(int index)
    {
        return m_builder.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end)
    {
        return m_builder.subSequence(start, end);
    }
}
