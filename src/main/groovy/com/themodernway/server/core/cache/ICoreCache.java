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

import java.io.Closeable;
import java.util.List;
import java.util.function.Function;

import com.themodernway.common.api.types.INamed;

public interface ICoreCache<K, T> extends INamed, Closeable
{
    public int size();

    public void clear();

    public T get(K name);

    public boolean isDefined(K name);

    public void remove(K name);

    public List<K> keys();

    public List<T> values();

    public Function<K, T> getMappingFunction();
}
