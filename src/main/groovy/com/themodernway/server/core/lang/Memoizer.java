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

package com.themodernway.server.core.lang;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memoizer<T, R>
{
    private final ConcurrentHashMap<T, R> m_cache = new ConcurrentHashMap<T, R>();

    private Function<T, R> doMemoize(final Function<T, R> function)
    {
        return input -> m_cache.computeIfAbsent(input, function);
    }

    public static <T, R> Function<T, R> memoize(final Function<T, R> function)
    {
        return new Memoizer<T, R>().doMemoize(function);
    }
}
