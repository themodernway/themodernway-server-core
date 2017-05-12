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
import java.nio.charset.Charset;

import org.apache.commons.io.output.WriterOutputStream;

public class StringBuilderOutputStream extends WriterOutputStream implements CharSequence
{
    private final NoSyncStringBuilderWriter m_writer;

    protected StringBuilderOutputStream(final NoSyncStringBuilderWriter writer, final Charset charset)
    {
        super(writer, charset);

        m_writer = writer;
    }

    public StringBuilderOutputStream()
    {
        this(IO.UTF_8_CHARSET);
    }

    public StringBuilderOutputStream(final Charset charset)
    {
        this(new NoSyncStringBuilderWriter(), charset);
    }

    public StringBuilder getStringBuilder()
    {
        return m_writer.getStringBuilder();
    }

    @Override
    public int length()
    {
        return m_writer.length();
    }

    @Override
    public char charAt(final int index)
    {
        return m_writer.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end)
    {
        return m_writer.subSequence(start, end);
    }

    @Override
    public String toString()
    {
        return m_writer.toString();
    }

    @Override
    public void flush()
    {
        m_writer.flush();
    }

    @Override
    public void close() throws IOException
    {
        m_writer.close();
    }
}
