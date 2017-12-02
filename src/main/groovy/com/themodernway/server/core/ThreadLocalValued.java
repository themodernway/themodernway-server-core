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

package com.themodernway.server.core;

import java.util.function.Supplier;

import com.themodernway.common.api.java.util.CommonOps;

public class ThreadLocalValued<T> implements IThreadLocalValued<T>
{
    private final ThreadLocal<T> m_local;

    public ThreadLocalValued(final Supplier<T> supplier)
    {
        this(ThreadLocal.withInitial(CommonOps.requireNonNull(supplier)));
    }

    public ThreadLocalValued(final ThreadLocal<T> local)
    {
        m_local = CommonOps.requireNonNull(local);
    }

    @Override
    public T getValue()
    {
        return getThreadLocal().get();
    }

    @Override
    public ThreadLocal<T> getThreadLocal()
    {
        return m_local;
    }

    @Override
    public Supplier<T> toSupplier()
    {
        return () -> getThreadLocal().get();
    }
}
