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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import com.google.common.collect.Streams;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.file.vfs.IFileItem;

public final class IO
{
    public static final int     EOF                     = CommonOps.IS_NOT_FOUND;

    public static final int     MINIMUM_BUFFER_CAPACITY = 16;

    public static final int     DEFAULT_BUFFER_CAPACITY = 4096;

    public static final int     MAXIMUM_BUFFER_CAPACITY = DEFAULT_BUFFER_CAPACITY * 4;

    public static final Charset UTF_8_CHARSET           = Charset.forName(CommonOps.CHARSET_UTF_8);

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

    public static final int toValidBufferCapacity(final int capacity)
    {
        if (capacity <= MINIMUM_BUFFER_CAPACITY)
        {
            return MINIMUM_BUFFER_CAPACITY;
        }
        if (capacity >= MAXIMUM_BUFFER_CAPACITY)
        {
            return MAXIMUM_BUFFER_CAPACITY;
        }
        return capacity + (capacity % MINIMUM_BUFFER_CAPACITY);
    }

    public static final long copy(final InputStream input, final OutputStream output, long length) throws IOException
    {
        if ((length = Math.max(0, length)) > 0)
        {
            return IOUtils.copyLarge(input, output, 0, length);
        }
        return length;
    }

    public static final long copy(final InputStream input, final OutputStream output) throws IOException
    {
        return IOUtils.copyLarge(input, output);
    }

    public static final long copy(final InputStream input, final Writer output) throws IOException
    {
        return IOUtils.copyLarge(new InputStreamReader(input, UTF_8_CHARSET), output);
    }

    public static final long copy(final InputStream input, final Writer output, long length) throws IOException
    {
        if ((length = Math.max(0, length)) > 0)
        {
            return IOUtils.copyLarge(new InputStreamReader(input, UTF_8_CHARSET), output, 0, length);
        }
        return length;
    }

    public static final long copy(final Reader input, final OutputStream output) throws IOException
    {
        final OutputStreamWriter writer = new OutputStreamWriter(output, UTF_8_CHARSET);

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
            final long leng = Math.max(0, file.length());

            if (leng > 0)
            {
                stream = toInputStream(file);

                return copy(stream, output, leng);
            }
            return leng;
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
            final long leng = Math.max(0, file.length());

            if (leng > 0)
            {
                stream = toInputStream(file);

                return copy(stream, output, leng);
            }
            return leng;
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final IFileItem file, final OutputStream output) throws IOException
    {
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

    public static final Stream<String> lines(final Path path) throws IOException
    {
        return Files.lines(CommonOps.requireNonNull(path), UTF_8_CHARSET);
    }

    public static final Stream<String> lines(final Resource resource) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            return lines(stream);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final Stream<String> lines(final Reader reader) throws IOException
    {
        return Streams.stream(IOUtils.lineIterator(CommonOps.requireNonNull(reader)));
    }

    public static final Stream<String> lines(final InputStream stream) throws IOException
    {
        return Streams.stream(IOUtils.lineIterator(CommonOps.requireNonNull(stream), UTF_8_CHARSET));
    }

    public static final Stream<String> lines(final File file) throws IOException
    {
        return lines(file.toPath());
    }

    public static final Stream<String> lines(final IFileItem file) throws IOException
    {
        return file.lines();
    }

    public static final InputStream toInputStream(final File file, final OpenOption... options) throws IOException
    {
        return toInputStream(file.toPath(), options);
    }

    public static final InputStream toInputStream(final Path path, final OpenOption... options) throws IOException
    {
        return Files.newInputStream(CommonOps.requireNonNull(path), options);
    }

    public static final OutputStream toOutputStream(final File file, final OpenOption... options) throws IOException
    {
        return toOutputStream(file.toPath(), options);
    }

    public static final OutputStream toOutputStream(final Path path, final OpenOption... options) throws IOException
    {
        return Files.newOutputStream(CommonOps.requireNonNull(path), options);
    }

    public static final Properties toProperties(final Properties prop, final InputStream stream) throws IOException
    {
        try
        {
            prop.load(CommonOps.requireNonNull(stream));
        }
        finally
        {
            IO.close(stream);
        }
        return prop;
    }

    public static final Properties toProperties(final Properties prop, final Reader reader) throws IOException
    {
        try
        {
            prop.load(CommonOps.requireNonNull(reader));
        }
        finally
        {
            IO.close(reader);
        }
        return prop;
    }

    public static final Properties toProperties(final InputStream stream) throws IOException
    {
        return toProperties(new Properties(), stream);
    }

    public static final Properties toProperties(final Reader reader) throws IOException
    {
        return toProperties(new Properties(), reader);
    }

    public static final Properties toProperties(final Properties prop, final Resource resource) throws IOException
    {
        return toProperties(prop, resource.getInputStream());
    }

    public static final Properties toProperties(final Resource resource) throws IOException
    {
        return toProperties(resource.getInputStream());
    }
}
