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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import com.themodernway.server.core.file.storage.IFileItem;

public final class IO
{
    private IO()
    {
    }

    public static final void close(final Closeable c)
    {
        IOUtils.closeQuietly(c);
    }

    public static final void close(final URLConnection c)
    {
        IOUtils.close(c);
    }

    public static final long copy(final InputStream input, final OutputStream output) throws IOException
    {
        return IOUtils.copyLarge(input, output);
    }

    public static final long copy(final InputStream input, final Writer output) throws IOException
    {
        return IOUtils.copyLarge(new InputStreamReader(input), output);
    }

    public static final long copy(final Reader input, final OutputStream output) throws IOException
    {
        final OutputStreamWriter writer = new OutputStreamWriter(output);

        final long size = IOUtils.copyLarge(input, writer);

        writer.flush();

        return size;
    }

    public static final long copy(final Reader input, final Writer output) throws IOException
    {
        return IOUtils.copyLarge(input, output);
    }

    public static final long copy(final Resource resource, final OutputStream output) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            return copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final Resource resource, final Writer output) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            return copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final File file, final OutputStream output) throws IOException
    {
        if (false == file.exists())
        {
            throw new IOException("File doesn't exist.");
        }
        if (false == file.isFile())
        {
            throw new IOException("Can't copy directory.");
        }
        if (false == file.canRead())
        {
            throw new IOException("Can't read file.");
        }
        InputStream stream = null;

        try
        {
            stream = new FileInputStream(file);

            return copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final File file, final Writer output) throws IOException
    {
        if (false == file.exists())
        {
            throw new IOException("File doesn't exist.");
        }
        if (false == file.isFile())
        {
            throw new IOException("Can't copy directory.");
        }
        if (false == file.canRead())
        {
            throw new IOException("Can't read file.");
        }
        InputStream stream = null;

        try
        {
            stream = new FileInputStream(file);

            return copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final IFileItem file, final OutputStream output) throws IOException
    {
        if (false == file.exists())
        {
            throw new IOException("File doesn't exist.");
        }
        if (false == file.isFile())
        {
            throw new IOException("Can't copy directory.");
        }
        if (false == file.isReadable())
        {
            throw new IOException("Can't read file.");
        }
        InputStream stream = null;

        try
        {
            stream = file.getInputStream();

            return copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final IFileItem file, final Writer output) throws IOException
    {
        if (false == file.exists())
        {
            throw new IOException("File doesn't exist.");
        }
        if (false == file.isFile())
        {
            throw new IOException("Can't copy directory.");
        }
        if (false == file.isReadable())
        {
            throw new IOException("Can't read file.");
        }
        InputStream stream = null;

        try
        {
            stream = file.getInputStream();

            return copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }
}
