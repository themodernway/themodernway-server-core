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

import java.text.DateFormat;
import java.util.Date;
import java.util.function.Supplier;

public final class ThreadLocalDateFormat extends ThreadLocalFormat<Date, DateFormat>
{
    public static final ThreadLocalDateFormat withInitial(final Supplier<DateFormat> supplier)
    {
        return new ThreadLocalDateFormat(supplier);
    }

    private ThreadLocalDateFormat(final Supplier<DateFormat> supplier)
    {
        super(supplier);
    }
}
