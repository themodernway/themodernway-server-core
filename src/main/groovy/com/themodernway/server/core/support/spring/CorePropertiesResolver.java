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

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;

public class CorePropertiesResolver implements IPropertiesResolver, BeanFactoryAware, Closeable
{
    private EmbeddedValueResolver                   m_factory;

    private final ConcurrentHashMap<String, String> m_docache = new ConcurrentHashMap<String, String>();

    public CorePropertiesResolver()
    {
    }

    @Override
    public String getPropertyByName(final String name)
    {
        return property(name);
    }

    @Override
    public String getPropertyByName(final String name, final String otherwise)
    {
        final String valu = property(name);

        if (null != valu)
        {
            return valu;
        }
        return otherwise;
    }

    @Override
    public String getPropertyByName(final String name, final Supplier<String> otherwise)
    {
        final String valu = property(name);

        if (null != valu)
        {
            return valu;
        }
        return otherwise.get();
    }

    @Override
    public String getResolvedExpression(final String expr)
    {
        return expression(expr);
    }

    @Override
    public String getResolvedExpression(final String expr, final String otherwise)
    {
        final String valu = expression(expr);

        if (null != valu)
        {
            return valu;
        }
        return otherwise;
    }

    @Override
    public String getResolvedExpression(final String expr, final Supplier<String> otherwise)
    {
        final String valu = expression(expr);

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
            m_factory = new EmbeddedValueResolver((ConfigurableBeanFactory) factory);
        }
        m_docache.clear();
    }

    private final String property(final String name)
    {
        CommonOps.requireNonNull(m_factory);

        final String valu = StringOps.requireTrimOrNull(name, "getPropertyByName(null)");

        return m_docache.computeIfAbsent(valu, property());
    }

    private final String expression(final String expr)
    {
        CommonOps.requireNonNull(m_factory);

        final String valu = StringOps.requireTrimOrNull(expr, "getResolvedExpression(null)");

        try
        {
            return m_factory.resolveStringValue(valu);
        }
        catch (final Exception e)
        {
            return null;
        }
    }

    private final Function<String, String> property()
    {
        return name -> {

            return expression("${" + name + "}");
        };
    }

    @Override
    public void close() throws IOException
    {
        m_factory = null;

        m_docache.clear();
    }
}
