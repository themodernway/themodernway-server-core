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

package com.themodernway.server.core.support.spring.cache;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreCaffeineCacheManager extends CaffeineCacheManager implements InitializingBean, BeanNameAware, CacheManager, ICoreCommon, INamed, IHasLogging, Closeable
{
    private final Logger m_logs = LoggingOps.getLogger(getClass());

    private String       m_name;

    private String       m_spec;

    public CoreCaffeineCacheManager()
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreCaffeineCacheManager()");
        }
    }

    public CoreCaffeineCacheManager(String caches)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreCaffeineCacheManager(String)");
        }
        caches = toTrimOrNull(caches);

        if (null != caches)
        {
            setCacheNames(toUniqueTokenStringList(caches));
        }
    }

    public CoreCaffeineCacheManager(final Collection<String> caches)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreCaffeineCacheManager(Collection)");
        }
        setCacheNames(caches);
    }

    @Override
    public void setCacheNames(final Collection<String> caches)
    {
        List<String> names = emptyList();

        if ((null != caches) && (false == caches.isEmpty()))
        {
            names = toUnique(caches);
        }
        if (false == names.isEmpty())
        {
            super.setCacheNames(names);
        }
    }

    @Override
    public void close() throws IOException
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("close (%s).", getName()));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        final String spec = toTrimOrNull(getCacheSpecification());

        if (null != spec)
        {
            setCaffeine(Caffeine.from(spec).recordStats());
        }
        else
        {
            setCaffeine(Caffeine.newBuilder().recordStats());
        }
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("init (%s).", getName()));
        }
    }

    @Override
    public void setBeanName(final String name)
    {
        if (null == m_name)
        {
            m_name = getOriginalBeanName(name);
        }
    }

    @Override
    public String getName()
    {
        if (null != m_name)
        {
            return m_name;
        }
        return format("%s_name", getClass().getSimpleName().toLowerCase());
    }

    @Override
    public Logger logger()
    {
        return m_logs;
    }

    @Override
    public void setCacheSpecification(final String spec)
    {
        m_spec = toTrimOrNull(spec);
    }

    public String getCacheSpecification()
    {
        return m_spec;
    }
}
