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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public interface ICoreCommon
{
    public static String EMPTY_STRING = StringOps.EMPTY_STRING;

    default public String format(String format, Object... args)
    {
        return String.format(format, args);
    }

    default public String uuid()
    {
        return UUID.randomUUID().toString().toUpperCase();
    }

    default public String toTrimOrNull(String string)
    {
        return StringOps.toTrimOrNull(string);
    }

    default public String toTrimOrElse(String string, String otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    default public String toTrimOrElse(String string, Supplier<String> otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    default public String requireTrimOrNull(String string)
    {
        return StringOps.requireTrimOrNull(string);
    }

    default public String requireTrimOrNull(String string, String reason)
    {
        return StringOps.requireTrimOrNull(string, reason);
    }

    default public String requireTrimOrNull(String string, Supplier<String> reason)
    {
        return requireNonNull(toTrimOrNull(string), reason);
    }

    default public <T> T requireNonNull(T object)
    {
        return Objects.requireNonNull(object);
    }

    default public <T> T requireNonNull(T object, String reason)
    {
        return Objects.requireNonNull(object, reason);
    }

    default public <T> T requireNonNull(T object, Supplier<String> reason)
    {
        return Objects.requireNonNull(object, reason);
    }

    default public String getEnvironmentProperty(final String name)
    {
        return System.getenv(requireNonNull(name));
    }

    default public String getEnvironmentProperty(final String name, final String otherwise)
    {
        final String prop = getEnvironmentProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise;
    }

    default public String getEnvironmentProperty(final String name, final Supplier<String> otherwise)
    {
        final String prop = getEnvironmentProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise.get();
    }

    default public String getSystemProperty(final String name)
    {
        return System.getProperty(requireNonNull(name));
    }

    default public String getSystemProperty(final String name, final String otherwise)
    {
        final String prop = getSystemProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise;
    }

    default public String getSystemProperty(final String name, final Supplier<String> otherwise)
    {
        final String prop = getSystemProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise.get();
    }

    default public String toPrintableString(final Collection<String> collection)
    {
        return StringOps.toPrintableString(collection);
    }

    default public List<String> toUniqueStringList(final Stream<String> strings)
    {
        return toUniqueStringList(strings.collect(Collectors.toList()));
    }

    default public List<String> toUniqueStringList(final Supplier<String> strings)
    {
        return toUniqueStringList(strings.get());
    }

    default public List<String> toUniqueStringList(final String strings)
    {
        return StringOps.toUniqueStringList(strings);
    }

    default public <T> List<T> emptyList()
    {
        return Collections.emptyList();
    }

    default public <T> List<T> arrayList()
    {
        return new ArrayList<T>();
    }

    default public <T> List<T> toList(final Enumeration<T> source)
    {
        return Collections.list(source);
    }

    default public <T> List<T> toUnmodifiableList(final List<T> list)
    {
        return Collections.unmodifiableList(list);
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> arrayListOfListSuppliers(Supplier<List<T>>... lists)
    {
        final List<T> list = arrayList();

        for (Supplier<List<T>> supp : lists)
        {
            final List<T> adds = supp.get();

            if ((null != adds) && (false == adds.isEmpty()))
            {
                list.addAll(adds);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> arrayListOfLists(List<T>... lists)
    {
        final List<T> list = arrayList();

        for (List<T> adds : lists)
        {
            if ((null != adds) && (false == adds.isEmpty()))
            {
                list.addAll(adds);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> arrayListOfListsUnique(List<T>... lists)
    {
        final List<T> list = arrayList();

        for (List<T> adds : lists)
        {
            if ((null != adds) && (false == adds.isEmpty()))
            {
                for (T item : adds)
                {
                    if (false == list.contains(item))
                    {
                        list.add(item);
                    }
                }
            }
        }
        return list;
    }

    default public List<String> toUniqueStringList(final String[] strings)
    {
        return StringOps.toUniqueStringList(strings);
    }

    default public List<String> toUniqueStringList(final Collection<String> strings)
    {
        return StringOps.toUniqueStringList(strings);
    }

    default public IServerContext getServerContext()
    {
        return ServerContextInstance.getServerContextInstance();
    }
}
