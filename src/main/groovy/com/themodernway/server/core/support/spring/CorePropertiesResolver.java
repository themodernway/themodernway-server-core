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

package com.themodernway.server.core.support.spring;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;

public class CorePropertiesResolver implements IPropertiesResolver, BeanFactoryAware, Closeable
{
    private static final Logger                     logger    = Logger.getLogger(CorePropertiesResolver.class);

    private ConfigurableBeanFactory                 m_factory;

    private final ConcurrentHashMap<String, String> m_docache = new ConcurrentHashMap<String, String>();

    public CorePropertiesResolver()
    {
    }

    @Override
    public String getPropertyByName(final String name)
    {
        return resolve(name);
    }

    @Override
    public String getPropertyByName(final String name, final String otherwise)
    {
        final String valu = resolve(name);

        if (null != valu)
        {
            return valu;
        }
        return otherwise;
    }

    @Override
    public String getPropertyByName(final String name, final Supplier<String> otherwise)
    {
        final String valu = resolve(name);

        if (null != valu)
        {
            return valu;
        }
        return otherwise.get();
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

    private final String resolve(final String name)
    {
        CommonOps.requireNonNull(m_factory);

        return m_docache.computeIfAbsent(StringOps.requireTrimOrNull(name, "getPropertyByName(null)"), resolve());
    }

    private final Function<String, String> resolve()
    {
        return name -> {

            try
            {
                logger.info("${" + name + "}");

                return m_factory.resolveEmbeddedValue("${" + name + "}");
            }
            catch (final Exception e)
            {
                return null;
            }
        };
    }

    @Override
    public void close() throws IOException
    {
        m_factory = null;

        m_docache.clear();
    }
}
