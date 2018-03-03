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
import java.util.Locale;

@SuppressWarnings("unchecked")
public interface IFormatted<T extends IFormatted<T>>
{
    default T printf(final String format, final Object... args)
    {
        return format(format, args);
    }

    default T printf(final Locale locale, final String format, final Object... args)
    {
        return format(locale, format, args);
    }

    default T format(final String format, final Object... args)
    {
        try
        {
            write(String.format(format, args));
        }
        catch (final IOException e)
        {
        }
        return (T) this;
    }

    default T format(final Locale locale, final String format, final Object... args)
    {
        try
        {
            write(String.format(locale, format, args));
        }
        catch (final IOException e)
        {
        }
        return (T) this;
    }

    default void write(final String valu) throws IOException
    {
        write(valu.getBytes(IO.UTF_8_CHARSET));
    }

    default void write(final byte[] buff) throws IOException
    {
        write(buff, 0, buff.length);
    }

    default void write(final byte b[], final int off, final int len) throws IOException
    {
        if ((off | len | (b.length - (len + off)) | (off + len)) < 0)
        {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < len; i++)
        {
            write(b[off + i]);
        }
    }

    abstract public void write(int b) throws IOException;
}
