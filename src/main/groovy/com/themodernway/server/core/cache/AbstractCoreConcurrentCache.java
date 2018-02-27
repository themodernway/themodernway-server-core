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
 * limitations under the License. ThreadLocal.withInitial(supplier);
 */

package com.themodernway.server.core.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.themodernway.common.api.java.util.CommonOps;

public abstract class AbstractCoreConcurrentCache<K, T> implements ICoreCache<K, T>
{
    private final String                  m_named;

    private final ConcurrentHashMap<K, T> m_cache = new ConcurrentHashMap<K, T>();

    protected AbstractCoreConcurrentCache(final String named)
    {
        m_named = CommonOps.requireNonNull(named);
    }

    @Override
    public String getName()
    {
        return m_named;
    }

    @Override
    public int size()
    {
        return m_cache.size();
    }

    @Override
    public void clear()
    {
        m_cache.clear();
    }

    @Override
    public T get(final K name)
    {
        return m_cache.computeIfAbsent(CommonOps.requireNonNull(name), CommonOps.requireNonNull(getMappingFunction()));
    }

    @Override
    public boolean isDefined(final K name)
    {
        return m_cache.containsKey(CommonOps.requireNonNull(name));
    }

    @Override
    public void remove(final K name)
    {
        m_cache.remove(CommonOps.requireNonNull(name));
    }

    @Override
    public List<T> values()
    {
        return CommonOps.toUnmodifiableList(m_cache.values());
    }

    @Override
    public List<K> keys()
    {
        return CommonOps.toUnmodifiableList(m_cache.keySet());
    }
}
