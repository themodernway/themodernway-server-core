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

package com.themodernway.server.core.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.core.io.Resource;

import com.themodernway.server.core.io.IO;

public class CoreContentTypeMapper
{
    private final MimetypesFileTypeMap m_maps;

    public CoreContentTypeMapper()
    {
        m_maps = new MimetypesFileTypeMap();
    }

    public CoreContentTypeMapper(final InputStream stream) throws IOException
    {
        m_maps = new MimetypesFileTypeMap(Objects.requireNonNull(stream));
    }

    public CoreContentTypeMapper(final Resource resource) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            m_maps = new MimetypesFileTypeMap(stream);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public CoreContentTypeMapper(final File file) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = Files.newInputStream(file.toPath());

            m_maps = new MimetypesFileTypeMap(stream);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public CoreContentTypeMapper(final Path path) throws IOException
    {
        InputStream stream = null;

        try
        {
            stream = Files.newInputStream(path);

            m_maps = new MimetypesFileTypeMap(stream);
        }
        finally
        {
            IO.close(stream);
        }
    }

    public String getContentTypeOf(final String file)
    {
        return m_maps.getContentType(Objects.requireNonNull(file));
    }

    public String getContentTypeOf(final File file)
    {
        return m_maps.getContentType(Objects.requireNonNull(file));
    }
}
