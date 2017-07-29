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

import java.text.Format;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class ThreadLocalFormat<T, F extends Format>
{
    private final ThreadLocal<F> m_local;

    protected ThreadLocalFormat(final Supplier<F> supplier)
    {
        m_local = ThreadLocal.withInitial(Objects.requireNonNull(supplier));
    }

    public F getFormat()
    {
        return getThreadLocal().get();
    }

    public String format(final T object)
    {
        return getFormat().format(Objects.requireNonNull(object));
    }

    public String format(final Supplier<T> supplier)
    {
        return format(supplier.get());
    }

    @SuppressWarnings("unchecked")
    public T parse(final String source) throws ParseException
    {
        return (T) getFormat().parseObject(Objects.requireNonNull(source));
    }

    public T parse(final Supplier<String> supplier) throws ParseException
    {
        return parse(supplier.get());
    }

    public ThreadLocal<F> getThreadLocal()
    {
        return m_local;
    }
}
