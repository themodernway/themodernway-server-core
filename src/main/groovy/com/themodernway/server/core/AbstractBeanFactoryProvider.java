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
 * limitations under the License. ThreadLocal.withInitial(supplier);
 */

package com.themodernway.server.core;

import java.io.Closeable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public abstract class AbstractBeanFactoryProvider<T extends Closeable> implements IBeanFactoryProvider<T>
{
    private String                         m_beansid;

    private final Class<T>                 m_classof;

    private final LinkedHashMap<String, T> m_storage = linkedMap();

    private final Logger                   m_logging = logger(getClass());

    protected AbstractBeanFactoryProvider(final Class<T> classof)
    {
        m_classof = requireNonNull(classof, "null bean class.");
    }

    @Override
    public String getName()
    {
        if (null != m_beansid)
        {
            return m_beansid;
        }
        return format("provider_%s_of_%s", getClass().getSimpleName().toLowerCase(), getClassOf().getSimpleName().toLowerCase());
    }

    @Override
    public void setBeanName(final String name)
    {
        if (null == m_beansid)
        {
            m_beansid = getOriginalBeanName(name);
        }
    }

    @Override
    public final Class<T> getClassOf()
    {
        return m_classof;
    }

    protected String name(final String name, final T valu)
    {
        return getOriginalBeanName(name);
    }

    protected boolean store(String name, final T valu)
    {
        if (null == valu)
        {
            logger().error(format("null valu in (%s).", getName()));

            return false;
        }
        if (null == (name = name(name, valu)))
        {
            logger().error(format("null valu in (%s).", getName()));

            return false;
        }
        if (null != m_storage.putIfAbsent(name, valu))
        {
            logger().error(format("duplicate name(%s) ignored in (%s).", name, getName()));

            return false;
        }
        logger().info(format("stored name(%s) in (%s).", name, getName()));

        return true;
    }

    @Override
    public Logger logger()
    {
        return m_logging;
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
    {
        getBeansOfType(beanFactory).forEach((name, valu) -> store(name, valu));
    }

    @Override
    public List<T> items()
    {
        return toUnmodifiableList(m_storage.values());
    }

    @Override
    public List<String> names()
    {
        return toUnmodifiableList(m_storage.keySet());
    }

    @Override
    public T getItem(final String name)
    {
        return m_storage.get(getOriginalBeanName(name));
    }

    @Override
    public boolean isDefined(final String name)
    {
        return (null != getItem(name));
    }

    protected Map<String, T> getBeansOfType(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            return toUnmodifiableMap(((DefaultListableBeanFactory) factory).getBeansOfType(getClassOf()));
        }
        return toUnmodifiableMap(emptyMap());
    }
}
