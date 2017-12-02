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
 * limitations under the License. ThreadLocal.withInitial(supplier);
 */

package com.themodernway.server.core.support.spring.cache;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.ICoreCommon;

public class CoreCaffeineCacheManager extends CaffeineCacheManager implements CacheManager, ICoreCommon, Closeable
{
    private static final String USE_DYNAMIC_CACHES = "__DYNAMIC__";

    public CoreCaffeineCacheManager()
    {
        setCacheNames(null);
    }

    public CoreCaffeineCacheManager(final String caches)
    {
        if (false == USE_DYNAMIC_CACHES.equals(caches))
        {
            setCacheNames(toUniqueTokenStringList(caches));
        }
        else
        {
            setCacheNames(null);
        }
    }

    public CoreCaffeineCacheManager(final Collection<String> caches)
    {
        setCacheNames(caches);
    }

    @Override
    public void setCacheNames(final Collection<String> caches)
    {
        List<String> names = CommonOps.emptyList();

        if ((null != caches) && (false == caches.isEmpty()))
        {
            names = toUnique(caches);
        }
        if (false == names.isEmpty())
        {
            super.setCacheNames(names);
        }
        else
        {
            super.setCacheNames(null);
        }
    }

    @Override
    public void close() throws IOException
    {
    }
}
