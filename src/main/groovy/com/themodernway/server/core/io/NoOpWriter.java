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

package com.themodernway.server.core.io;

import java.io.IOException;
import java.io.Writer;

public class NoOpWriter extends Writer
{
    public NoOpWriter()
    {
    }

    @Override
    public void write(final int c) throws IOException
    {
    }

    @Override
    public void write(final char[] cbuf) throws IOException
    {
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException
    {
    }

    @Override
    public void write(final String str) throws IOException
    {
    }

    @Override
    public void write(final String str, final int off, final int len) throws IOException
    {
    }

    @Override
    public NoOpWriter append(final CharSequence csq) throws IOException
    {
        return this;
    }

    @Override
    public NoOpWriter append(final CharSequence csq, final int start, final int end) throws IOException
    {
        return this;
    }

    @Override
    public NoOpWriter append(final char c) throws IOException
    {
        return this;
    }

    @Override
    public void flush() throws IOException
    {
    }

    @Override
    public void close() throws IOException
    {
    }
}
