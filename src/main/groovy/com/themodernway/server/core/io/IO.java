/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

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

    public static final Runnable onClose(final Closeable c)
    {
        return () -> IO.close(c);
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
        if ((length = Math.max(0L, length)) > 0L)
        {
            return IOUtils.copyLarge(input, output, 0L, length);
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
        if ((length = Math.max(0L, length)) > 0L)
        {
            return IOUtils.copyLarge(new InputStreamReader(input, UTF_8_CHARSET), output, 0L, length);
        }
        return length;
    }

    public static final long copy(final Reader input, final Writer output, long length) throws IOException
    {
        if ((length = Math.max(0L, length)) > 0L)
        {
            return IOUtils.copyLarge(input, output, 0L, length);
        }
        return length;
    }

    public static final long copy(final Reader input, final Writer output) throws IOException
    {
        return IOUtils.copyLarge(input, output);
    }

    public static final long copy(final Reader input, final OutputStream output) throws IOException
    {
        final OutputStreamWriter writer = new OutputStreamWriter(output, UTF_8_CHARSET);

        final long length = IOUtils.copyLarge(input, writer);

        writer.flush();

        return length;
    }

    public static final long copy(final Reader input, final OutputStream output, long length) throws IOException
    {
        if ((length = Math.max(0L, length)) > 0L)
        {
            final OutputStreamWriter writer = new OutputStreamWriter(output, UTF_8_CHARSET);

            length = IOUtils.copyLarge(input, writer, 0L, length);

            writer.flush();
        }
        return length;
    }

    public static final long copy(final Resource resource, final OutputStream output) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            return IO.copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final Resource resource, final OutputStream output, long length) throws IOException
    {
        if ((length = Math.max(0L, length)) > 0L)
        {
            InputStream stream = null;

            try
            {
                stream = resource.getInputStream();

                return IO.copy(stream, output, length);
            }
            finally
            {
                IO.close(stream);
            }
        }
        return length;
    }

    public static final long copy(final Resource resource, final Writer output) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            return IO.copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final Resource resource, final Writer output, long length) throws IOException
    {
        if ((length = Math.max(0L, length)) > 0L)
        {
            InputStream stream = null;

            try
            {
                stream = resource.getInputStream();

                return IO.copy(stream, output, length);
            }
            finally
            {
                IO.close(stream);
            }
        }
        return length;
    }

    public static final long copy(final File file, final OutputStream output) throws IOException
    {
        return copy(file, output, file.length());
    }

    public static final long copy(final File file, final OutputStream output, long length) throws IOException
    {
        if (false == IO.exists(file))
        {
            throw new IOException("File doesn't exist.");
        }
        if (false == IO.isFile(file))
        {
            throw new IOException("Can't copy directory.");
        }
        if (false == IO.isReadable(file))
        {
            throw new IOException("Can't read file.");
        }
        if ((length = Math.max(0L, Math.min(length, file.length()))) > 0L)
        {
            InputStream stream = null;

            try
            {
                stream = IO.toInputStream(file);

                return IO.copy(stream, output, length);
            }
            finally
            {
                IO.close(stream);
            }
        }
        return length;
    }

    public static final long copy(final File file, final Writer output) throws IOException
    {
        return copy(file, output, file.length());
    }

    public static final long copy(final File file, final Writer output, long length) throws IOException
    {
        if (false == IO.exists(file))
        {
            throw new IOException("File doesn't exist.");
        }
        if (false == IO.isFile(file))
        {
            throw new IOException("Can't copy directory.");
        }
        if (false == IO.isReadable(file))
        {
            throw new IOException("Can't read file.");
        }
        if ((length = Math.max(0L, Math.min(length, file.length()))) > 0L)
        {
            InputStream stream = null;

            try
            {
                stream = IO.toInputStream(file);

                return IO.copy(stream, output, length);
            }
            finally
            {
                IO.close(stream);
            }
        }
        return length;
    }

    public static final long copy(final IFileItem file, final OutputStream output) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = file.getInputStream();

            return IO.copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final IFileItem file, final OutputStream output, long length) throws IOException
    {
        if ((length = Math.max(0L, length)) > 0L)
        {
            InputStream stream = null;

            try
            {
                stream = file.getInputStream();

                return IO.copy(stream, output, length);
            }
            finally
            {
                IO.close(stream);
            }
        }
        return length;
    }

    public static final long copy(final IFileItem file, final Writer output) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = file.getInputStream();

            return IO.copy(stream, output);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final long copy(final IFileItem file, final Writer output, long length) throws IOException
    {
        if ((length = Math.max(0L, length)) > 0L)
        {
            InputStream stream = null;

            try
            {
                stream = file.getInputStream();

                return IO.copy(stream, output, length);
            }
            finally
            {
                IO.close(stream);
            }
        }
        return length;
    }

    public static final Stream<String> lines(final Path path) throws IOException
    {
        return Files.lines(CommonOps.requireNonNull(path), UTF_8_CHARSET);
    }

    public static final Stream<String> lines(final File file) throws IOException
    {
        return IO.lines(file.toPath());
    }

    public static final Stream<String> lines(final Reader reader) throws IOException
    {
        return IOUtils.toBufferedReader(reader).lines().onClose(IO.onClose(reader));
    }

    public static final Stream<String> lines(final InputStream stream) throws IOException
    {
        return IO.lines(new InputStreamReader(stream, UTF_8_CHARSET));
    }

    public static final Stream<String> lines(final Resource resource) throws IOException
    {
        return IO.lines(resource.getInputStream());
    }

    public static final Stream<String> lines(final IFileItem file) throws IOException
    {
        return file.lines();
    }

    public static final InputStream toInputStream(final File file, final OpenOption... options) throws IOException
    {
        return IO.toInputStream(file.toPath(), options);
    }

    public static final InputStream toInputStream(final Path path, final OpenOption... options) throws IOException
    {
        return Files.newInputStream(CommonOps.requireNonNull(path), options);
    }

    public static final BufferedReader toBufferedReader(final Path path) throws IOException
    {
        return Files.newBufferedReader(CommonOps.requireNonNull(path), UTF_8_CHARSET);
    }

    public static final BufferedReader toBufferedReader(final File file) throws IOException
    {
        return IO.toBufferedReader(file.toPath());
    }

    public static BufferedWriter toBufferedWriter(final Path path, final OpenOption... options) throws IOException
    {
        return Files.newBufferedWriter(CommonOps.requireNonNull(path), UTF_8_CHARSET, options);
    }

    public static BufferedWriter toBufferedWriter(final File file, final OpenOption... options) throws IOException
    {
        return IO.toBufferedWriter(file.toPath(), options);
    }

    public static final OutputStream toOutputStream(final File file, final OpenOption... options) throws IOException
    {
        return IO.toOutputStream(file.toPath(), options);
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
        return IO.toProperties(new Properties(), stream);
    }

    public static final Properties toProperties(final Reader reader) throws IOException
    {
        return IO.toProperties(new Properties(), reader);
    }

    public static final Properties toProperties(final Properties prop, final Resource resource) throws IOException
    {
        PropertiesLoaderUtils.fillProperties(prop, resource);

        return prop;
    }

    public static final Properties toProperties(final Resource resource) throws IOException
    {
        return IO.toProperties(new Properties(), resource);
    }

    public static final boolean exists(final File file)
    {
        return file.exists();
    }

    public static final boolean isFolder(final File file)
    {
        return file.isDirectory();
    }

    public static final boolean isFile(final File file)
    {
        return file.isFile();
    }

    public static final boolean isReadable(final File file)
    {
        return file.canRead();
    }

    public static final boolean isWritable(final File file)
    {
        return file.canWrite();
    }

    public static final boolean isHidden(final File file)
    {
        return file.isHidden();
    }

    public static final boolean exists(final Path path, final LinkOption... options)
    {
        return Files.exists(path, options);
    }

    public static final boolean isFolder(final Path path, final LinkOption... options)
    {
        return Files.isDirectory(path, options);
    }

    public static final boolean isFile(final Path path, final LinkOption... options)
    {
        return Files.isRegularFile(path, options);
    }

    public static final boolean isReadable(final Path path)
    {
        return Files.isReadable(path);
    }

    public static final boolean isWritable(final Path path)
    {
        return Files.isWritable(path);
    }

    public static final boolean isHidden(final Path path)
    {
        try
        {
            return Files.isHidden(path);
        }
        catch (final IOException e)
        {
            return false;
        }
    }

    public static final String getStringAtMost(final InputStream stream, final long leng, final long slop) throws IOException
    {
        final NoSyncStringBuilderWriter os = new NoSyncStringBuilderWriter();

        IO.copy(stream, os, leng + slop);

        return os.toString();
    }

    public static final String getStringAtMost(final InputStream stream, final long leng) throws IOException
    {
        return IO.getStringAtMost(stream, leng, MINIMUM_BUFFER_CAPACITY);
    }

    public static final String getStringAtMost(final Reader reader, final long leng, final long slop) throws IOException
    {
        final NoSyncStringBuilderWriter os = new NoSyncStringBuilderWriter();

        IO.copy(reader, os, leng + slop);

        return os.toString();
    }

    public static final String getStringAtMost(final Reader reader, final long leng) throws IOException
    {
        return IO.getStringAtMost(reader, leng, MINIMUM_BUFFER_CAPACITY);
    }

    public static final String getStringAtMost(final Resource resource, final long leng, final long slop) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            return IO.getStringAtMost(stream, leng, slop);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final String getStringAtMost(final Resource resource, final long leng) throws IOException
    {
        return IO.getStringAtMost(resource, leng, MINIMUM_BUFFER_CAPACITY);
    }

    public static final String getStringAtMost(final Path path, final long leng, final long slop) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = IO.toInputStream(path);

            return IO.getStringAtMost(stream, leng, slop);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final String getStringAtMost(final Path path, final long leng) throws IOException
    {
        return IO.getStringAtMost(path, leng, MINIMUM_BUFFER_CAPACITY);
    }

    public static final String getStringAtMost(final IFileItem file, final long leng, final long slop) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = file.getInputStream();

            return IO.getStringAtMost(stream, leng, slop);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final String getStringAtMost(final IFileItem file, final long leng) throws IOException
    {
        return IO.getStringAtMost(file, leng, MINIMUM_BUFFER_CAPACITY);
    }

    public static final String getStringAtMost(final File file, final long leng, final long slop) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = IO.toInputStream(file);

            return IO.getStringAtMost(stream, leng, slop);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public static final String getStringAtMost(final File file, final long leng) throws IOException
    {
        return getStringAtMost(file, leng, MINIMUM_BUFFER_CAPACITY);
    }
}
