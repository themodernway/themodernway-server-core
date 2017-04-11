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

import java.util.Date;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@FunctionalInterface
public interface ITimeSupplier
{
    public long getTime();

    public static long now()
    {
        return System.currentTimeMillis();
    }

    public static ITimeSupplier mills()
    {
        return System::currentTimeMillis;
    }

    public static ITimeSupplier nanos()
    {
        return System::nanoTime;
    }

    public static ITimeSupplier ofLong(final Long time)
    {
        return () -> time;
    }

    public static ITimeSupplier ofLong(final Date date)
    {
        return ofLong(date.getTime());
    }

    public static ITimeSupplier ofLong(final Supplier<Long> time)
    {
        return () -> time.get();
    }

    public static ITimeSupplier ofLong(final LongSupplier time)
    {
        return () -> time.getAsLong();
    }

    public static ITimeSupplier ofDate(final Date date)
    {
        return () -> date.getTime();
    }

    public static ITimeSupplier ofDate(final Supplier<Date> date)
    {
        return () -> date.get().getTime();
    }
}
