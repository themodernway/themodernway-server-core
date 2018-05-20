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

package com.themodernway.server.core;

import java.text.Format;
import java.text.ParseException;
import java.util.function.Supplier;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.ParserException;

public abstract class ThreadLocalFormat<T, F extends Format> extends ThreadLocalValued<F> implements IFormattingParser<T>
{
    protected ThreadLocalFormat(final Supplier<F> supplier)
    {
        super(supplier);
    }

    @Override
    public String format(final T object)
    {
        return getValue().format(CommonOps.requireNonNull(object));
    }

    @Override
    public String format(final Supplier<T> supplier)
    {
        return format(supplier.get());
    }

    @Override
    public T parse(final String source) throws ParserException
    {
        try
        {
            return CommonOps.CAST(getValue().parseObject(CommonOps.requireNonNull(source)));
        }
        catch (final ParseException e)
        {
           throw new ParserException(e);
        }
    }

    @Override
    public T parse(final Supplier<String> supplier) throws ParserException
    {
        return parse(supplier.get());
    }
}
