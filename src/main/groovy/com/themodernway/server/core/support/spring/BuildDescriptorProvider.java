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
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.logging.LoggingOps;

public class BuildDescriptorProvider implements IBuildDescriptorProvider, BeanFactoryAware
{
    private static final Logger                           logger        = LoggingOps.getLogger(BuildDescriptorProvider.class);

    private final LinkedHashMap<String, IBuildDescriptor> m_descriptors = new LinkedHashMap<>();

    protected void addDescriptor(final IBuildDescriptor descriptor)
    {
        if (null != descriptor)
        {
            final String name = StringOps.toTrimOrNull(descriptor.getNameSpace());

            if ((null != name) && (false == name.startsWith("@GRADLE")))
            {
                if (null == m_descriptors.get(name))
                {
                    m_descriptors.put(name, descriptor);

                    if (logger.isInfoEnabled())
                    {
                        logger.info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("BuildDescriptorProvider.addDescriptor(%s) Registered", name));
                    }
                }
                else if (logger.isErrorEnabled())
                {
                    logger.error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("BuildDescriptorProvider.addDescriptor(%s) Duplicate ignored", name));
                }
            }
        }
    }

    @Override
    public IBuildDescriptor getBuildDescriptor(final String name)
    {
        return m_descriptors.get(StringOps.requireTrimOrNull(name));
    }

    @Override
    public List<String> getBuildDescriptorNames()
    {
        return CommonOps.toUnmodifiableList(m_descriptors.keySet());
    }

    @Override
    public List<IBuildDescriptor> getBuildDescriptors()
    {
        return CommonOps.toUnmodifiableList(m_descriptors.values());
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
        // empty by design.
    }

    @Override
    public JSONArray toJSONArray()
    {
        final JSONArray list = new JSONArray();

        for (final IBuildDescriptor descriptor : m_descriptors.values())
        {
            list.add(descriptor.toJSONObject());
        }
        return list;
    }

    @Override
    public JSONObject toJSONObject()
    {
        return toJSONObject("descriptors");
    }

    @Override
    public JSONObject toJSONObject(final String label)
    {
        return new JSONObject(StringOps.requireTrimOrNull(label), toJSONArray());
    }
}
