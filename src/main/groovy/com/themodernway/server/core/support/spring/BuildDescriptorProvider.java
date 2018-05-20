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

package com.themodernway.server.core.support.spring;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.server.core.ICoreBase;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONUtils;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public class BuildDescriptorProvider implements IBuildDescriptorProvider, BeanFactoryAware, ICoreBase, IHasLogging
{
    private final Logger                                  m_logging = LoggingOps.getLogger(getClass());

    private final LinkedHashMap<String, IBuildDescriptor> m_storage = linkedMap();

    @Value("${core.server.build.descriptor.list.name:descriptors}")
    private String                                        m_oflist;

    public BuildDescriptorProvider()
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "BuildDescriptorProvider().");
        }
    }

    protected void addDescriptor(final IBuildDescriptor descriptor)
    {
        if (null != descriptor)
        {
            final String name = toTrimOrNull(descriptor.getNameSpace());

            if ((null != name) && (false == name.startsWith("@GRADLE")))
            {
                if (null == m_storage.get(name))
                {
                    m_storage.put(name, descriptor);

                    if (logger().isInfoEnabled())
                    {
                        logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("BuildDescriptorProvider.addDescriptor(%s) added.", name));
                    }
                }
                else if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("BuildDescriptorProvider.addDescriptor(%s) duplicate value.", name));
                }
            }
        }
        else if (logger().isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "BuildDescriptorProvider.addDescriptor() null value.");
        }
    }

    @Override
    public IBuildDescriptor getBuildDescriptor(final String name)
    {
        return m_storage.get(requireTrimOrNull(name));
    }

    @Override
    public List<String> getBuildDescriptorNames()
    {
        return toUnmodifiableList(m_storage.keySet());
    }

    @Override
    public List<IBuildDescriptor> getBuildDescriptors()
    {
        return toUnmodifiableList(m_storage.values());
    }

    @Override
    public JSONObject toJSONObject()
    {
        return new JSONObject(toTrimOrElse(m_oflist, JSONUtils.JSON_OBJECT_DEFAULT_ARRAY_NAME), m_storage.values());
    }

    @Override
    public Logger logger()
    {
        return m_logging;
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            for (final IBuildDescriptor descriptor : ((DefaultListableBeanFactory) factory).getBeansOfType(IBuildDescriptor.class).values())
            {
                addDescriptor(descriptor);
            }
        }
    }

    @Override
    public void close() throws IOException
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "close().");
        }
    }

    @Override
    public String toJSONString()
    {
        return toJSONObject().toJSONString();
    }
}
