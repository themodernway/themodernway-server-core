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
import java.util.function.Supplier;

import com.themodernway.common.api.java.util.CommonOps;

public abstract class ThreadLocalFormat<T, F extends Format> extends ThreadLocalValued<F> implements IFormattingParser<T>
{
    protected ThreadLocalFormat(final Supplier<F> supplier)
    {
        super(supplier);
    }

    @Override
    public String format(final T object) throws Exception
    {
        return getValue().format(CommonOps.requireNonNull(object));
    }

    @Override
    public String format(final Supplier<T> supplier) throws Exception
    {
        return format(supplier.get());
    }

    @Override
    public T parse(final String source) throws Exception
    {
        return CommonOps.CAST(getValue().parseObject(CommonOps.requireNonNull(source)));
    }

    @Override
    public T parse(final Supplier<String> supplier) throws Exception
    {
        return parse(supplier.get());
    }
}
