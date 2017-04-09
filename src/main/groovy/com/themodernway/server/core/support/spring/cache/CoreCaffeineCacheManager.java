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

package com.themodernway.server.core.support.spring.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import com.themodernway.server.core.ICoreCommon;

public class CoreCaffeineCacheManager extends CaffeineCacheManager implements CacheManager, ICoreCommon
{
    public CoreCaffeineCacheManager()
    {
        setCacheNames(null);
    }
    
    public CoreCaffeineCacheManager(final String caches)
    {
        setCacheNames(toUniqueStringList(caches));
    }


    public CoreCaffeineCacheManager(final Collection<String> caches)
    {
        setCacheNames(caches);
    }

    @Override
    public void setCacheNames(final Collection<String> caches)
    {
        List<String> names = Collections.emptyList();

        if ((null != caches) && (false == caches.isEmpty()))
        {
            names = toUniqueStringList(caches);
        }
        if (names.isEmpty())
        {
            names = getDefautCacheNames();
        }
        super.setCacheNames(names);
    }

    public List<String> getDefautCacheNames()
    {
        final String names = toTrimOrNull(getServerContext().getPropertyByName("core.caffiene.cache.manager.names"));

        if (null == names)
        {
            return null;
        }
        return toUniqueStringList(names);
    }
}
