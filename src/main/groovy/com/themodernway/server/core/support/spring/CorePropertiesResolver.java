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

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import com.themodernway.common.api.java.util.StringOps;

public class CorePropertiesResolver implements IPropertiesResolver, BeanFactoryAware, Closeable
{
    private ConfigurableBeanFactory             m_factory;

    private final LinkedHashMap<String, String> m_docache = new LinkedHashMap<String, String>();

    public CorePropertiesResolver()
    {
    }

    @Override
    public String getPropertyByName(final String name)
    {
        return doResolve(StringOps.requireTrimOrNull(name, "getPropertyByName(null)"));
    }

    @Override
    public String getPropertyByName(final String name, final String otherwise)
    {
        final String valu = doResolve(StringOps.requireTrimOrNull(name, "getPropertyByName(null)"));

        if (null != valu)
        {
            return valu;
        }
        return otherwise;
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof ConfigurableBeanFactory)
        {
            m_factory = ((ConfigurableBeanFactory) factory);
        }
        m_docache.clear();
    }

    private final String doResolve(final String name)
    {
        Objects.requireNonNull(m_factory);

        String valu = m_docache.get(Objects.requireNonNull(name));

        if (null != valu)
        {
            return valu;
        }
        try
        {
            valu = m_factory.resolveEmbeddedValue("${" + name + "}");
        }
        catch (Exception e)
        {
            return null;
        }
        if (null != valu)
        {
            synchronized (m_docache)
            {
                m_docache.put(name, valu);
            }
        }
        return valu;
    }

    @Override
    public void close() throws IOException
    {
        m_factory = null;

        m_docache.clear();
    }
}
