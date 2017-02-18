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

package com.themodernway.server.core.support.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;

public class BuildDescriptorProvider implements IBuildDescriptorProvider, BeanFactoryAware
{
    private static final Logger                           logger        = Logger.getLogger(BuildDescriptorProvider.class);

    private final LinkedHashMap<String, IBuildDescriptor> m_descriptors = new LinkedHashMap<String, IBuildDescriptor>();

    public BuildDescriptorProvider()
    {
    }

    protected void addDescriptor(final IBuildDescriptor descriptor)
    {
        if (null != descriptor)
        {
            final String name = StringOps.toTrimOrNull(descriptor.getNameSpace());

            if (null != name)
            {
                if (false == name.startsWith("@GRADLE"))
                {
                    if (null == m_descriptors.get(name))
                    {
                        m_descriptors.put(name, descriptor);

                        logger.info("BuildDescriptorProvider.addDescriptor(" + name + ") Registered");
                    }
                    else
                    {
                        logger.error("BuildDescriptorProvider.addDescriptor(" + name + ") Duplicate ignored");
                    }
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
        return Collections.unmodifiableList(new ArrayList<String>(m_descriptors.keySet()));
    }

    @Override
    public List<IBuildDescriptor> getBuildDescriptors()
    {
        return Collections.unmodifiableList(new ArrayList<IBuildDescriptor>(m_descriptors.values()));
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            for (IBuildDescriptor descriptor : ((DefaultListableBeanFactory) factory).getBeansOfType(IBuildDescriptor.class).values())
            {
                addDescriptor(descriptor);
            }
        }
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public JSONObject toJSONObject()
    {
        final JSONArray list = new JSONArray();

        for (IBuildDescriptor descriptor : m_descriptors.values())
        {
            list.add(descriptor.toJSONObject());
        }
        return new JSONObject("build_descriptor_list", list);
    }
}
