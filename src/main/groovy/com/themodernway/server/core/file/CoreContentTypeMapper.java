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
import java.nio.file.Path;
import java.util.Objects;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.themodernway.server.core.io.IO;

public class CoreContentTypeMapper implements ICoreContentTypeMapper, InitializingBean
{
    private Logger               m_logs = Logger.getLogger(getClass());

    private String[]             m_type = null;

    private MimetypesFileTypeMap m_maps = null;

    private Resource             m_rsrc = new ClassPathResource("mime.types", getClass());

    public CoreContentTypeMapper()
    {
    }

    public CoreContentTypeMapper(final Resource resource)
    {
        m_rsrc = Objects.requireNonNull(resource);
    }

    private final MimetypesFileTypeMap iniFileTypeMap() throws IOException
    {
        if (null != m_maps)
        {
            return m_maps;
        }
        InputStream stream = null;

        try
        {
            m_logs.info(String.format("loading (%s) mime file.", m_rsrc));

            stream = m_rsrc.getInputStream();

            m_maps = new MimetypesFileTypeMap(stream);

            if (null != m_type)
            {
                for (String type : m_type)
                {
                    m_logs.info(String.format("adding to (%s) mime type (%s).", m_rsrc, type));

                    m_maps.addMimeTypes(type);
                }
            }
            return m_maps;
        }
        finally
        {
            IO.close(stream);
        }
    }

    protected final FileTypeMap getFileTypeMap()
    {
        if (null == m_maps)
        {
            try
            {
                m_maps = iniFileTypeMap();
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Could not load specified MIME type mapping file: " + m_rsrc, e);
            }
        }
        return m_maps;
    }

    public void setMappings(final String... type)
    {
        m_type = type;
    }

    @Override
    public String getContentType(final String file)
    {
        return getFileTypeMap().getContentType(Objects.requireNonNull(file));
    }

    @Override
    public String getContentType(final File file)
    {
        return getFileTypeMap().getContentType(Objects.requireNonNull(file));
    }

    @Override
    public String getContentType(final Path path)
    {
        return getContentType(path.toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        getFileTypeMap();
    }
}