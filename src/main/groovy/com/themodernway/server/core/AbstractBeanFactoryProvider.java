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

package com.themodernway.server.core;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.server.core.logging.LoggingOps;

public abstract class AbstractBeanFactoryProvider<T extends Closeable> extends AbstractCoreLoggingBase implements ICoreCommon, IBeanFactoryProvider<T>
{
    private String               m_beansid;

    private final Class<T>       m_classof;

    private final Map<String, T> m_storage = linkedMap();

    private final AtomicBoolean  m_is_open = new AtomicBoolean(false);

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
        m_is_open.set(true);
    }

    @Override
    public Class<T> getClassOf()
    {
        return m_classof;
    }

    protected String name(final String name, final T valu)
    {
        requireNonNull(valu, () -> format("null valu in (%s).", getName()));

        return getOriginalBeanName(name);
    }

    protected boolean store(String name, final T valu)
    {
        if (null == valu)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("null valu in (%s).", getName()));
            }
            return false;
        }
        name = name(name, valu);

        if (null == name)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("null valu in (%s).", getName()));
            }
            return false;
        }
        if (null != m_storage.putIfAbsent(name, valu))
        {
            if (logger().isWarnEnabled())
            {
                logger().warn(LoggingOps.THE_MODERN_WAY_MARKER, format("duplicate name (%s) ignored in (%s).", name, getName()));
            }
            return false;
        }
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("stored name(%s) in (%s).", name, getName()));
        }
        return true;
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException
    {
        getBeansOfType(beanFactory).forEach(this::store);
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

    @Override
    public void reset()
    {
        m_storage.clear();

        m_is_open.set(false);
    }

    @Override
    public void destroy() throws Exception
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("destroy (%s).", getName()));
        }
    }

    @Override
    public boolean isOpen()
    {
        return m_is_open.get();
    }

    protected Map<String, T> getBeansOfType(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            return toUnmodifiableMap(((DefaultListableBeanFactory) factory).getBeansOfType(getClassOf()));
        }
        if (logger().isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("not DefaultListableBeanFactory (%s).", getName()));
        }
        return toUnmodifiableMap(emptyMap());
    }
}
