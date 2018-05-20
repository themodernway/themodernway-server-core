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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.ICursor;
import com.themodernway.common.api.types.IFixedIterable;

public interface ICoreBase
{
    default <T> T CAST(final Object value)
    {
        return CommonOps.CAST(value);
    }

    default boolean isNull(final Object value)
    {
        return CommonOps.isNull(value);
    }

    default boolean isNonNull(final Object value)
    {
        return CommonOps.isNonNull(value);
    }

    default <T> T requireNonNullOrElse(final T value, final T otherwise)
    {
        return CommonOps.requireNonNullOrElse(value, otherwise);
    }

    default <T> T requireNonNullOrElse(final T value, final Supplier<T> otherwise)
    {
        return CommonOps.requireNonNullOrElse(value, otherwise);
    }

    default <T> T requireNonNull(final T value)
    {
        return CommonOps.requireNonNull(value);
    }

    default <T> T requireNonNull(final T value, final String reason)
    {
        return CommonOps.requireNonNull(value, reason);
    }

    default <T> T requireNonNull(final T value, final Supplier<String> reason)
    {
        return CommonOps.requireNonNull(value, reason);
    }

    default <T> Supplier<T> toSupplier(final T value)
    {
        return () -> value;
    }

    default IntSupplier toSupplier(final int value)
    {
        return () -> value;
    }

    default LongSupplier toSupplier(final long value)
    {
        return () -> value;
    }

    default DoubleSupplier toSupplier(final double value)
    {
        return () -> value;
    }

    default BooleanSupplier toSupplier(final boolean value)
    {
        return () -> value;
    }

    default <T> Optional<T> toOptional(final T value)
    {
        return CommonOps.toOptional(value);
    }

    @SuppressWarnings("unchecked")
    default <T> List<T> toList(final T... source)
    {
        return CommonOps.toList(source);
    }

    default <T> List<T> toList(final Stream<T> source)
    {
        return CommonOps.toList(source);
    }

    default <T> List<T> toList(final Stream<T> source, final Predicate<? super T> predicate)
    {
        return CommonOps.toList(source, predicate);
    }

    default <T> List<T> toList(final Enumeration<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default <T> List<T> toList(final Collection<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default <T> List<T> toList(final Collection<? extends T> source, final Predicate<? super T> predicate)
    {
        return CommonOps.CAST(CommonOps.toList(source.stream(), predicate));
    }

    default <T> List<T> toList(final ICursor<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default <T> List<T> toList(final IFixedIterable<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default <T> List<T> emptyList()
    {
        return CommonOps.emptyList();
    }

    default <K, V> Map<K, V> emptyMap()
    {
        return CommonOps.emptyMap();
    }

    default <K, V> LinkedHashMap<K, V> linkedMap()
    {
        return CommonOps.linkedMap();
    }

    default <K, V> LinkedHashMap<K, V> linkedMap(final Map<? extends K, ? extends V> source)
    {
        return CommonOps.linkedMap(source);
    }

    default <T> LinkedHashSet<T> linkedSet()
    {
        return CommonOps.linkedSet();
    }

    default <T> LinkedHashSet<T> linkedSet(final Collection<? extends T> source)
    {
        return CommonOps.linkedSet(source);
    }

    @SuppressWarnings("rawtypes")
    default <K, V> Map<K, V> rawmap(final Map source)
    {
        return CommonOps.rawmap(source);
    }

    default Map<String, Object> strmap(final Map<String, ?> source)
    {
        return CommonOps.strmap(source);
    }

    default <T> List<T> toKeys(final Map<? extends T, ?> source)
    {
        return CommonOps.toKeys(source);
    }

    default <K, V> Map<K, V> toUnmodifiableMap(final Map<? extends K, ? extends V> source)
    {
        return CommonOps.toUnmodifiableMap(source);
    }

    default <T> List<T> toUnmodifiableList(final Collection<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default <T> List<T> toUnmodifiableList(final Stream<T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default <T> List<T> toUnmodifiableList(final Stream<T> source, final Predicate<? super T> predicate)
    {
        return CommonOps.toUnmodifiableList(source, predicate);
    }

    @SuppressWarnings("unchecked")
    default <T> List<T> toUnmodifiableList(final T... source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default <T> List<T> toUnmodifiableList(final ICursor<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default <T> List<T> toUnmodifiableList(final IFixedIterable<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default <T> List<T> toUnmodifiableList(final Enumeration<? extends T> source)
    {
        return CommonOps.toUnmodifiableList(source);
    }

    default <T> Set<T> toUnmodifiableSet(final Collection<? extends T> source)
    {
        return CommonOps.toUnmodifiableSet(source);
    }

    default <T> ArrayList<T> arrayListOfSize(final int size)
    {
        return CommonOps.arrayListOfSize(size);
    }

    default <T> ArrayList<T> arrayList()
    {
        return CommonOps.arrayList();
    }

    @SuppressWarnings("unchecked")
    default <T> ArrayList<T> arrayList(final T... source)
    {
        return CommonOps.arrayList(source);
    }

    default <T> ArrayList<T> arrayList(final Stream<T> source)
    {
        return CommonOps.arrayList(source);
    }

    default <T> ArrayList<T> arrayList(final Stream<T> source, final Predicate<? super T> predicate)
    {
        return CommonOps.arrayList(source, predicate);
    }

    default <T> ArrayList<T> arrayList(final Collection<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    default <T> ArrayList<T> arrayList(final ICursor<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    default <T> ArrayList<T> arrayList(final IFixedIterable<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    default <T> ArrayList<T> arrayList(final Enumeration<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    @SuppressWarnings("unchecked")
    default <T> T[] toArray(final T... source)
    {
        return CommonOps.toArray(source);
    }

    @SuppressWarnings("unchecked")
    default <T> List<T> toListOfLists(final List<T>... lists)
    {
        return CommonOps.toListOfLists(lists);
    }

    @SuppressWarnings("unchecked")
    default <T> List<T> toListOfListsUnique(final List<T>... lists)
    {
        return CommonOps.toListOfListsUnique(lists);
    }

    default int box(final int val, final int min, final int max)
    {
        return CommonOps.box(val, min, max);
    }

    default long box(final long val, final long min, final long max)
    {
        return CommonOps.box(val, min, max);
    }

    default double box(final double val, final double min, final double max)
    {
        return CommonOps.box(val, min, max);
    }

    default double project(final double value, final double istart, final double istop, final double ostart, final double ostop)
    {
        return CommonOps.project(value, istart, istop, ostart, ostop);
    }

    default String getEnvironmentProperty(final String name)
    {
        return System.getenv(name);
    }

    default String getEnvironmentProperty(final String name, final String otherwise)
    {
        final String prop = getEnvironmentProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise;
    }

    default String getEnvironmentProperty(final String name, final Supplier<String> otherwise)
    {
        final String prop = getEnvironmentProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise.get();
    }

    default String getSystemProperty(final String name)
    {
        return System.getProperty(name);
    }

    default String getSystemProperty(final String name, final String otherwise)
    {
        final String prop = getSystemProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise;
    }

    default String getSystemProperty(final String name, final Supplier<String> otherwise)
    {
        final String prop = getSystemProperty(name);

        if (null != prop)
        {
            return prop;
        }
        return otherwise.get();
    }

    default String format(final String format, final Object... args)
    {
        return String.format(format, args);
    }

    default String repeat(final String string, final int times)
    {
        return StringOps.repeat(string, times);
    }

    default String[] toArray(final Collection<String> collection)
    {
        return StringOps.toArray(collection);
    }

    default String[] toArray(final Collection<String> collection, final Predicate<String> predicate)
    {
        return StringOps.toArray(collection, predicate);
    }

    default String[] toArray(final String... collection)
    {
        return StringOps.toArray(collection);
    }

    default String[] toArray(final Stream<String> stream)
    {
        return StringOps.toArray(stream);
    }

    default String[] toArray(final Stream<String> stream, final Predicate<String> predicate)
    {
        return StringOps.toArray(stream, predicate);
    }

    default String[] toUniqueArray(final Collection<String> collection)
    {
        return StringOps.toUniqueArray(collection);
    }

    default String[] toUniqueArray(final Collection<String> collection, final Predicate<String> predicate)
    {
        return StringOps.toUniqueArray(collection, predicate);
    }

    default String[] toUniqueArray(final String... collection)
    {
        return StringOps.toUniqueArray(collection);
    }

    default Stream<String> toUnique(final Stream<String> stream)
    {
        return StringOps.toUnique(stream);
    }

    default Stream<String> toUnique(final Stream<String> stream, final Predicate<String> predicate)
    {
        return StringOps.toUnique(stream, predicate);
    }

    default List<String> toUnique(final String... collection)
    {
        return StringOps.toUnique(collection);
    }

    default List<String> toUnique(final Collection<String> collection)
    {
        return StringOps.toUnique(collection);
    }

    default List<String> toUnique(final Collection<String> collection, final Predicate<String> predicate)
    {
        return StringOps.toUnique(collection, predicate);
    }

    default List<String> toUniqueTokenStringList(final String strings)
    {
        return StringOps.toUniqueTokenStringList(strings);
    }

    default String toCommaSeparated(final Collection<String> collection)
    {
        return StringOps.toCommaSeparated(collection);
    }

    default String toCommaSeparated(final String... collection)
    {
        return StringOps.toCommaSeparated(collection);
    }

    default String toCommaSeparated(final Stream<String> stream)
    {
        return StringOps.toCommaSeparated(stream);
    }

    default Collection<String> tokenizeToStringCollection(final String string)
    {
        return StringOps.tokenizeToStringCollection(string);
    }

    default Collection<String> tokenizeToStringCollection(final String string, final String delimiters)
    {
        return StringOps.tokenizeToStringCollection(string, delimiters);
    }

    default Collection<String> tokenizeToStringCollection(final String string, final boolean trim, final boolean ignore)
    {
        return StringOps.tokenizeToStringCollection(string, trim, ignore);
    }

    default Collection<String> tokenizeToStringCollection(final String string, final String delimiters, final boolean trim, final boolean ignore)
    {
        return StringOps.tokenizeToStringCollection(string, delimiters, trim, ignore);
    }

    default String toPrintableString(final Collection<String> collection)
    {
        return StringOps.toPrintableString(collection);
    }

    default String toPrintableString(final String... list)
    {
        return StringOps.toPrintableString(list);
    }

    default String toTrimOrNull(final String string)
    {
        return StringOps.toTrimOrNull(string);
    }

    default String toTrimOrElse(final String string, final String otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    default String toTrimOrElse(final String string, final Supplier<String> otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    default String requireTrimOrNull(final String string)
    {
        return StringOps.requireTrimOrNull(string);
    }

    default String requireTrimOrNull(final String string, final String reason)
    {
        return StringOps.requireTrimOrNull(string, reason);
    }

    default String requireTrimOrNull(final String string, final Supplier<String> reason)
    {
        return StringOps.requireTrimOrNull(string, reason);
    }

    default String reverse(final String string)
    {
        return StringOps.reverse(string);
    }

    default String failIfNullBytePresent(final String string)
    {
        return StringOps.failIfNullBytePresent(string);
    }

    default <T> Stream<T> emptyStream()
    {
        return CommonOps.emptyStream();
    }

    default <T> Stream<T> toStream(final T source)
    {
        return CommonOps.toStream(source);
    }

    @SuppressWarnings("unchecked")
    default <T> Stream<T> toStream(final T... source)
    {
        return CommonOps.toStream(source);
    }

    default IntStream toStream(final int source)
    {
        return CommonOps.toStream(source);
    }

    default IntStream toStream(final int... source)
    {
        return CommonOps.toStream(source);
    }

    default LongStream toStream(final long source)
    {
        return CommonOps.toStream(source);
    }

    default LongStream toStream(final long... source)
    {
        return CommonOps.toStream(source);
    }

    default DoubleStream toStream(final double source)
    {
        return CommonOps.toStream(source);
    }

    default DoubleStream toStream(final double... source)
    {
        return CommonOps.toStream(source);
    }

    default List<String> toUniqueStringListOf(final List<?> list)
    {
        if ((null != list) && (false == list.isEmpty()))
        {
            return toList(toUnique(list.stream().filter(o -> o instanceof CharSequence).map(o -> toTrimOrNull(o.toString()))));
        }
        return emptyList();
    }
}
