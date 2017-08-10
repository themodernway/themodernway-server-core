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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.themodernway.common.api.java.util.StringOps;

public final class CoreUtils
{
    protected CoreUtils()
    {
    }

    public static final <T> T NULL()
    {
        return null;
    }

    public static final void setConsumerUniqueStringArray(final String list, final Consumer<String[]> prop)
    {
        final String toks = StringOps.toTrimOrNull(list);

        if (null != toks)
        {
            final String[] uniq = StringOps.toArray(StringOps.toUniqueTokenStringList(toks));

            if ((null != uniq) && (uniq.length > 0))
            {
                prop.accept(uniq);

                return;
            }
        }
        prop.accept(NULL());
    }

    public static final void setConsumerUniqueStringArray(final Collection<String> list, final Consumer<String[]> prop)
    {
        if ((null != list) && (false == list.isEmpty()))
        {
            final String[] uniq = StringOps.toUniqueArray(list);

            if ((null != uniq) && (uniq.length > 0))
            {
                prop.accept(uniq);

                return;
            }
        }
        prop.accept(NULL());
    }

    public static final List<String> getSupplierUniqueStringArray(final Supplier<String[]> prop)
    {
        final String[] uniq = prop.get();

        if ((null != uniq) && (uniq.length > 0))
        {
            return StringOps.toUnique(uniq);
        }
        return arrayList(0);
    }

    public static final <T> List<T> arrayList()
    {
        return new ArrayList<T>();
    }

    public static final <T> List<T> arrayList(final int size)
    {
        return new ArrayList<T>(size);
    }
}
