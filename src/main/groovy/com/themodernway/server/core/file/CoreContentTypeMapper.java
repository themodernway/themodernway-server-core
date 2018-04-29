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

package com.themodernway.server.core.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

@NotThreadSafe
public class CoreContentTypeMapper implements ICoreContentTypeMapper, InitializingBean, IHasLogging
{
    private final Logger         m_logs = LoggingOps.getLogger(getClass());

    private String[]             m_type = CommonOps.NULL();

    private MimetypesFileTypeMap m_maps = CommonOps.NULL();

    private Resource             m_rsrc = new ClassPathResource("mime.types", getClass());

    public CoreContentTypeMapper()
    {
    }

    public CoreContentTypeMapper(final Resource resource)
    {
        m_rsrc = CommonOps.requireNonNull(resource);
    }

    private final synchronized MimetypesFileTypeMap iniFileTypeMap() throws IOException
    {
        if (null != m_maps)
        {
            return m_maps;
        }
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("loading (%s) mime file.", m_rsrc));
        }
        try (InputStream stream = m_rsrc.getInputStream())
        {
            m_maps = new MimetypesFileTypeMap(stream);

            if (null != m_type)
            {
                for (final String type : m_type)
                {
                    if (logger().isInfoEnabled())
                    {
                        logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("adding to (%s) mime type (%s).", m_rsrc, type));
                    }
                    m_maps.addMimeTypes(type);
                }
            }
            return m_maps;
        }
    }

    @Override
    public FileTypeMap getFileTypeMap()
    {
        if (null == m_maps)
        {
            try
            {
                iniFileTypeMap();
            }
            catch (final IOException e)
            {
                final String oops = String.format("can not load specified MIME type mapping file (%s).", m_rsrc);

                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, oops, e);
                }
                throw new IllegalStateException(oops, e);
            }
        }
        return m_maps;
    }

    public void setMappings(final String... type)
    {
        m_type = type;
    }

    @Override
    public String getContentType(final String path)
    {
        return getFileTypeMap().getContentType(CommonOps.requireNonNull(path));
    }

    @Override
    public String getContentType(final File file)
    {
        return getFileTypeMap().getContentType(CommonOps.requireNonNull(file));
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

    @Override
    public Logger logger()
    {
        return m_logs;
    }
}
