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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.ICursor;
import com.themodernway.common.api.types.IFixedIterable;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public interface ICoreCommon extends IHasLogging
{
    public static final int    IS_NOT_FOUND = CommonOps.IS_NOT_FOUND;

    public static final String EMPTY_STRING = StringOps.EMPTY_STRING;

    public static <T> T NULL()
    {
        return CommonOps.NULL();
    }

    public static <T> T CAST(final Object object)
    {
        return CommonOps.CAST(object);
    }

    public static List<String> toTaggingValues(final Object target)
    {
        if (null == target)
        {
            return CommonOps.emptyList();
        }
        final TaggingValues tagging = target.getClass().getAnnotation(TaggingValues.class);

        if (null == tagging)
        {
            return CommonOps.emptyList();
        }
        return StringOps.toUnique(tagging.value());
    }

    default public String format(final String format, final Object... args)
    {
        return String.format(format, args);
    }

    default public String uuid()
    {
        return getServerContext().uuid();
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

    @SuppressWarnings("unchecked")
    default public <T> List<T> arrayListOfListSuppliers(final Supplier<List<T>>... lists)
    {
        final List<T> list = arrayList();

        for (final Supplier<List<T>> supp : lists)
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

    @Override
    default public Logger logger()
    {
        return getServerContext().logger();
    }

    default public String getOriginalBeanName(final String name)
    {
        return toTrimOrNull(BeanFactoryUtils.originalBeanName(requireNonNull(name)));
    }

    default public IServerContext getServerContext()
    {
        return ServerContextInstance.getServerContextInstance();
    }

    default public boolean isNull(final Object object)
    {
        return CommonOps.isNull(object);
    }

    default public boolean isNonNull(final Object object)
    {
        return CommonOps.isNonNull(object);
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

    default public <T> Supplier<T> toSupplier(final T valu)
    {
        return () -> valu;
    }

    default public IntSupplier toSupplier(final int valu)
    {
        return () -> valu;
    }

    default public LongSupplier toSupplier(final long valu)
    {
        return () -> valu;
    }

    default public DoubleSupplier toSupplier(final double valu)
    {
        return () -> valu;
    }

    default public BooleanSupplier toSupplier(final boolean valu)
    {
        return () -> valu;
    }

    default public <T> Optional<T> toOptional(final T valu)
    {
        return CommonOps.toOptional(valu);
    }

    @SuppressWarnings("unchecked")
    default public <T> List<T> toList(final T... source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> toList(final Stream<T> stream)
    {
        return CommonOps.toList(stream);
    }

    default public <T> List<T> toList(final Enumeration<? extends T> source)
    {
        return CommonOps.toList(source);
    }

    default public <T> List<T> toList(final Collection<? extends T> collection)
    {
        return CommonOps.toList(collection);
    }

    default public <T> List<T> toList(final ICursor<? extends T> cursor)
    {
        return CommonOps.toList(cursor);
    }

    default public <T> List<T> toList(final IFixedIterable<? extends T> iterable)
    {
        return CommonOps.toList(iterable);
    }

    default public <T> List<T> emptyList()
    {
        return CommonOps.emptyList();
    }

    default public <K, V> Map<K, V> emptyMap()
    {
        return CommonOps.emptyMap();
    }

    default public <T> List<T> toKeys(final Map<? extends T, ?> maps)
    {
        return CommonOps.toKeys(maps);
    }

    default public <K, V> Map<K, V> toUnmodifiableMap(final Map<? extends K, ? extends V> maps)
    {
        return CommonOps.toUnmodifiableMap(maps);
    }

    default public <T> List<T> toUnmodifiableList(final Collection<? extends T> list)
    {
        return CommonOps.toUnmodifiableList(toList(list));
    }

    default public <T> ArrayList<T> arrayList()
    {
        return CommonOps.arrayList();
    }

    default public <T> ArrayList<T> arrayList(final int size)
    {
        return CommonOps.arrayList(size);
    }

    @SuppressWarnings("unchecked")
    default public <T> ArrayList<T> arrayList(final T... source)
    {
        return CommonOps.arrayList(source);
    }

    default public <T> ArrayList<T> arrayList(final Stream<T> stream)
    {
        return CommonOps.arrayList(stream);
    }

    default public <T> ArrayList<T> arrayList(final Collection<? extends T> collection)
    {
        return CommonOps.arrayList(collection);
    }

    default public <T> ArrayList<T> arrayList(final ICursor<? extends T> cursor)
    {
        return CommonOps.arrayList(cursor);
    }

    default public <T> ArrayList<T> arrayList(final IFixedIterable<? extends T> iterable)
    {
        return CommonOps.arrayList(iterable);
    }

    default public <T> ArrayList<T> arrayList(final Enumeration<? extends T> source)
    {
        return CommonOps.arrayList(source);
    }

    default public <T> T[] toArray(final Collection<T> collection, final T[] base)
    {
        return CommonOps.toArray(collection, base);
    }

    default public <T> T[] toArray(final Collection<T> collection, final IntFunction<T[]> generator)
    {
        return CommonOps.toArray(collection, generator);
    }

    default public <T> T[] toArray(final Stream<T> stream, final IntFunction<T[]> generator)
    {
        return CommonOps.toArray(stream, generator);
    }

    @SuppressWarnings("unchecked")
    default public <T> T[] toArray(final T... source)
    {
        return CommonOps.toArray(source);
    }

    default public int[] toArray(final int... source)
    {
        return CommonOps.toArray(source);
    }

    default public long[] toArray(final long... source)
    {
        return CommonOps.toArray(source);
    }

    default public double[] toArray(final double... source)
    {
        return CommonOps.toArray(source);
    }

    default public boolean[] toArray(final boolean... source)
    {
        return CommonOps.toArray(source);
    }

    default public void setConsumerUniqueStringArray(final String list, final Consumer<String[]> prop)
    {
        StringOps.setConsumerUniqueStringArray(list, prop);
    }

    default public void setConsumerUniqueStringArray(final Collection<String> list, final Consumer<String[]> prop)
    {
        StringOps.setConsumerUniqueStringArray(list, prop);
    }

    default public List<String> getSupplierUniqueStringArray(final Supplier<String[]> prop)
    {
        return StringOps.getSupplierUniqueStringArray(prop);
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

    default public List<String> toUnique(final Collection<String> collection)
    {
        return StringOps.toUnique(collection);
    }

    default public List<String> toUnique(final String... collection)
    {
        return StringOps.toUnique(collection);
    }

    default public List<String> toUniqueTokenStringList(final String strings)
    {
        return StringOps.toUniqueTokenStringList(strings);
    }

    default public String toPrintableString(final Collection<String> collection)
    {
        return StringOps.toPrintableString(collection);
    }

    default public String toCommaSeparated(final Collection<String> collection)
    {
        return StringOps.toCommaSeparated(collection);
    }

    default public String toCommaSeparated(final String... collection)
    {
        return StringOps.toCommaSeparated(collection);
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

    default public String failIfNullBytePresent(final String string)
    {
        if (null != string)
        {
            StringOps.failIfNullBytePresent(string);
        }
        return string;
    }
}
