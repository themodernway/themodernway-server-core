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

package com.themodernway.server.core.file.vfs.cache;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public abstract class AbstractCaffieneFileItemCache implements IFileItemCache, ICaffieneRemovalListener, IHasLogging
{
    private final String         m_name;

    private final ICaffieneCache m_keep;

    private final Logger         m_logs = LoggingOps.getLogger(getClass());

    protected AbstractCaffieneFileItemCache(final String name)
    {
        m_name = CommonOps.requireNonNull(name);

        m_keep = CommonOps.CAST(Caffeine.newBuilder().recordStats().removalListener(getCaffieneRemovalListener()).expireAfterAccess(30, TimeUnit.SECONDS).build());
    }

    public ICaffieneRemovalListener getCaffieneRemovalListener()
    {
        return this;
    }

    @Override
    public Logger logger()
    {
        return m_logs;
    }

    public final ICaffieneCache getCache()
    {
        return m_keep;
    }

    @Override
    public int size()
    {
        return getCache().asMap().size();
    }

    @Override
    public void clear()
    {
        getCache().invalidateAll();
    }

    @Override
    public IFileItemCacheNode get(final String name)
    {
        CommonOps.requireNonNull(name);

        return getCache().get(name, getMappingFunction());
    }

    @Override
    public boolean isDefined(final String name)
    {
        CommonOps.requireNonNull(name);

        return (null != getCache().getIfPresent(name));
    }

    @Override
    public void remove(final String name)
    {
        CommonOps.requireNonNull(name);

        getCache().invalidate(name);
    }

    @Override
    public List<String> keys()
    {
        return CommonOps.toUnmodifiableList(getCache().asMap().keySet());
    }

    @Override
    public List<IFileItemCacheNode> values()
    {
        return CommonOps.toUnmodifiableList(getCache().asMap().values());
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public void close() throws IOException
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("cache(%s) closed().", getName()));
        }
        clear();
    }

    @Override
    public void onRemoval(final String name, final IFileItemCacheNode file, final RemovalCause cause)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("cache(%s) removed(%s) cause(%s).", getName(), name, cause));
        }
    }
}
