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

package com.themodernway.server.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.ICursor;
import com.themodernway.common.api.types.IFixedIterable;

public interface ICoreBase
{
    default public <T> T NULL()
    {
        return CommonOps.NULL();
    }

    default public <T> T CAST(final Object value)
    {
        return CommonOps.CAST(value);
    }

    default public boolean isNull(final Object value)
    {
        return CommonOps.isNull(value);
    }

    default public boolean isNonNull(final Object value)
    {
        return CommonOps.isNonNull(value);
    }

    default public <T> T requireNonNullOrElse(final T value, final T otherwise)
    {
        return CommonOps.requireNonNullOrElse(value, otherwise);
    }

    default public <T> T requireNonNullOrElse(final T value, final Supplier<T> otherwise)
    {
        return CommonOps.requireNonNullOrElse(value, otherwise);
    }

    default public <T> T requireNonNull(final T value)
    {
        return CommonOps.requireNonNull(value);
    }

    default public <T> T requireNonNull(final T value, final String reason)
    {
        return CommonOps.requireNonNull(value, reason);
    }

    default public <T> T requireNonNull(final T value, final Supplier<String> reason)
    {
        return CommonOps.requireNonNull(value, reason);
    }

    default public <T> Supplier<T> toSupplier(final T value)
    {
        return () -> value;
    }

    default public IntSupplier toSupplier(final int value)
    {
        return () -> value;
    }

    default public LongSupplier toSupplier(final long value)
    {
        return () -> value;
    }

    default public DoubleSupplier toSupplier(final double value)
    {
        return () -> value;
    }

    default public BooleanSupplier toSupplier(final boolean value)
    {
        return () -> value;
    }

    default public <T> Optional<T> toOptional(final T value)
    {
        return CommonOps.toOptional(value);
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> toList(final T... source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> toList(final Stream<T> source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> toList(final Enumeration<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> toList(final Collection<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> toList(final ICursor<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> toList(final IFixedIterable<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> emptyList()
    {
        return CommonOps.emptyList();
    }

    default public <K, V> Map<K, V> emptyMap()
    {
        return CommonOps.emptyMap();
    }

    default public <K, V> LinkedHashMap<K, V> linkedMap()
    {
        return CommonOps.linkedMap();
    }

    default public <K, V> LinkedHashMap<K, V> linkedMap(final Map<? extends K, ? extends V> source)
    {
        return CommonOps.linkedMap(source);
    }

    @SuppressWarnings("rawtypes")
    default public <K, V> Map<K, V> RAWMAP(final Map source)
    {
        return CommonOps.RAWMAP(source);
    }

    default public Map<String, Object> STRMAP(final Map<String, ?> source)
    {
        return CommonOps.STRMAP(source);
    }

    default public <T> List<T> toKeys(final Map<? extends T, ?> source)
    {
        return CommonOps.toKeys(source);
    }

    default public <K, V> Map<K, V> toUnmodifiableMap(final Map<? extends K, ? extends V> source)
    {
        return CommonOps.toUnmodifiableMap(source);
    }

    default public <T> List<T> toUnmodifiableList(final Collection<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default public <T> List<T> toUnmodifiableList(final Stream<T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> toUnmodifiableList(final T... source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default public <T> List<T> toUnmodifiableList(final ICursor<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default public <T> List<T> toUnmodifiableList(final IFixedIterable<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default public <T> List<T> toUnmodifiableList(final Enumeration<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default public <T> ArrayList<T> arrayListOfSize(final int size)
    {
        return CommonOps.arrayListOfSize(size);
    }

    default public <T> ArrayList<T> arrayList()
    {
        return CommonOps.arrayList();
    }

    @SuppressWarnings("unchecked")
    default public <T> ArrayList<T> arrayList(final T... source)
    {
        return CommonOps.arrayList(source);
    }

    default public <T> ArrayList<T> arrayList(final Stream<T> source)
    {
        return CommonOps.arrayList(source);
    }

    default public <T> ArrayList<T> arrayList(final Collection<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    default public <T> ArrayList<T> arrayList(final ICursor<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    default public <T> ArrayList<T> arrayList(final IFixedIterable<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    default public <T> ArrayList<T> arrayList(final Enumeration<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    @SuppressWarnings("unchecked")
    default public <T> Stream<T> toStream(final T... source)
    {
        return CommonOps.toStream(source);
    }

    @SuppressWarnings("unchecked")
    default public <T> T[] toArray(final T... source)
    {
        return CommonOps.toArray(source);
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> arrayListOfLists(final List<T>... lists)
    {
        final List<T> list = arrayList();

        for (final List<T> adds : lists)
        {
            if ((null != adds) && (false == adds.isEmpty()))
            {
                list.addAll(adds);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> arrayListOfListsUnique(final List<T>... lists)
    {
        final List<T> list = arrayList();

        for (final List<T> adds : lists)
        {
            if ((null != adds) && (false == adds.isEmpty()))
            {
                for (final T item : adds)
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

    default public String format(final String format, final Object... args)
    {
        return String.format(requireNonNull(format), args);
    }

    default public String repeat(final String string, final int times)
    {
        return StringOps.repeat(string, times);
    }

    default public String[] toArray(final Collection<String> collection)
    {
        return StringOps.toArray(collection);
    }

    default public String[] toArray(final String... collection)
    {
        return StringOps.toArray(collection);
    }

    default public String[] toArray(final Stream<String> stream)
    {
        return StringOps.toArray(stream);
    }

    default public String[] toUniqueArray(final Collection<String> collection)
    {
        return StringOps.toUniqueArray(collection);
    }

    default public String[] toUniqueArray(final String... collection)
    {
        return StringOps.toUniqueArray(collection);
    }

    default public Stream<String> toUnique(final Stream<String> stream)
    {
        return StringOps.toUnique(stream);
    }

    default public List<String> toUnique(final String... collection)
    {
        return StringOps.toUnique(collection);
    }

    default public List<String> toUnique(final Collection<String> collection)
    {
        return StringOps.toUnique(collection);
    }

    default public List<String> toUniqueTokenStringList(final String strings)
    {
        return StringOps.toUniqueTokenStringList(strings);
    }

    default public String toCommaSeparated(final Collection<String> collection)
    {
        return StringOps.toCommaSeparated(collection);
    }

    default public String toCommaSeparated(final String... collection)
    {
        return StringOps.toCommaSeparated(collection);
    }

    default public String toCommaSeparated(final Stream<String> stream)
    {
        return StringOps.toCommaSeparated(stream);
    }

    default public Collection<String> tokenizeToStringCollection(final String string)
    {
        return StringOps.tokenizeToStringCollection(string);
    }

    default public Collection<String> tokenizeToStringCollection(final String string, final String delimiters)
    {
        return StringOps.tokenizeToStringCollection(string, delimiters);
    }

    default public Collection<String> tokenizeToStringCollection(final String string, final boolean trim, final boolean ignore)
    {
        return StringOps.tokenizeToStringCollection(string, trim, ignore);
    }

    default public Collection<String> tokenizeToStringCollection(final String string, final String delimiters, final boolean trim, final boolean ignore)
    {
        return StringOps.tokenizeToStringCollection(string, delimiters, trim, ignore);
    }

    default public String toPrintableString(final Collection<String> collection)
    {
        return StringOps.toPrintableString(collection);
    }

    default public String toPrintableString(final String... list)
    {
        return StringOps.toPrintableString(list);
    }

    default public String toTrimOrNull(final String string)
    {
        return StringOps.toTrimOrNull(string);
    }

    default public String toTrimOrElse(final String string, final String otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    default public String toTrimOrElse(final String string, final Supplier<String> otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    default public String requireTrimOrNull(final String string)
    {
        return StringOps.requireTrimOrNull(string);
    }

    default public String requireTrimOrNull(final String string, final String reason)
    {
        return StringOps.requireTrimOrNull(string, reason);
    }

    default public String requireTrimOrNull(final String string, final Supplier<String> reason)
    {
        return StringOps.requireTrimOrNull(string, reason);
    }

    default public String reverse(final String string)
    {
        return StringOps.reverse(string);
    }

    default public String failIfNullBytePresent(final String string)
    {
        return StringOps.failIfNullBytePresent(string);
    }
}
