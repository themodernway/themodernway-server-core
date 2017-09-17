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

import java.text.NumberFormat;
import java.util.function.Supplier;

public final class ThreadLocalNumberFormat extends ThreadLocalFormat<Number, NumberFormat>
{
    public final static ThreadLocalNumberFormat withInitial(final Supplier<NumberFormat> supplier)
    {
        return new ThreadLocalNumberFormat(supplier);
    }

    private ThreadLocalNumberFormat(final Supplier<NumberFormat> supplier)
    {
        super(supplier);
    }
}
