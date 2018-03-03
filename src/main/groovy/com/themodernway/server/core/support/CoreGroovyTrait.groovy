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

package com.themodernway.server.core.support

import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Supplier
import java.util.stream.Stream

import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.web.context.WebApplicationContext

import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.common.api.java.util.StringOps
import com.themodernway.common.api.types.ICursor
import com.themodernway.common.api.types.IFixedIterable
import com.themodernway.server.core.file.vfs.IFileItemStorage
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider
import com.themodernway.server.core.json.support.JSONTrait
import com.themodernway.server.core.mail.IMailSender
import com.themodernway.server.core.mail.IMailSenderProvider
import com.themodernway.server.core.scripting.IScriptingProvider
import com.themodernway.server.core.security.IAuthorizationProvider
import com.themodernway.server.core.security.IAuthorizationResult
import com.themodernway.server.core.security.ICryptoProvider
import com.themodernway.server.core.security.session.IServerSessionRepository
import com.themodernway.server.core.security.session.IServerSessionRepositoryProvider
import com.themodernway.server.core.support.spring.IBuildDescriptorProvider
import com.themodernway.server.core.support.spring.IPropertiesResolver
import com.themodernway.server.core.support.spring.IServerContext
import com.themodernway.server.core.support.spring.IServletContextCustomizerProvider
import com.themodernway.server.core.support.spring.ServerContextInstance
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public trait CoreGroovyTrait implements CoreGroovyParallelTrait, JSONTrait
{
    @Memoized
    public IServerContext getServerContext()
    {
        ServerContextInstance.getServerContextInstance()
    }

    public boolean isApplicationContextInitialized()
    {
        getServerContext().isApplicationContextInitialized()
    }

    @Memoized
    public ApplicationContext getApplicationContext()
    {
        getServerContext().getApplicationContext()
    }

    @Memoized
    public WebApplicationContext getWebApplicationContext()
    {
        getServerContext().getWebApplicationContext()
    }

    @Memoized
    public Environment getEnvironment()
    {
        getServerContext().getEnvironment()
    }

    @Memoized
    public IMailSenderProvider getMailSenderProvider()
    {
        getServerContext().getMailSenderProvider()
    }

    @Memoized
    public IMailSender getMailSender(String name)
    {
        getMailSenderProvider().getItem(name)
    }

    @Memoized
    public IBuildDescriptorProvider getBuildDescriptorProvider()
    {
        getServerContext().getBuildDescriptorProvider()
    }

    @Memoized
    public IFileItemStorageProvider getFileItemStorageProvider()
    {
        getServerContext().getFileItemStorageProvider()
    }

    @Memoized
    public IFileItemStorage getFileItemStorage(String name)
    {
        getFileItemStorageProvider().getItem(name)
    }

    @Memoized
    public IServletContextCustomizerProvider getServletContextCustomizerProvider()
    {
        getServerContext().getServletContextCustomizerProvider()
    }

    @Memoized
    public IPropertiesResolver getPropertiesResolver()
    {
        getServerContext().getPropertiesResolver()
    }

    @Memoized
    public IAuthorizationProvider getAuthorizationProvider()
    {
        getServerContext().getAuthorizationProvider()
    }

    @Memoized
    public IServerSessionRepositoryProvider getServerSessionRepositoryProvider()
    {
        getServerContext().getServerSessionRepositoryProvider()
    }

    @Memoized
    public IServerSessionRepository getServerSessionRepository(String domain)
    {
        getServerSessionRepositoryProvider().getServerSessionRepository(domain)
    }

    public IAuthorizationResult isAuthorized(Object target, List<String> roles)
    {
        getServerContext().isAuthorized(target, roles)
    }

    @Memoized
    public ICryptoProvider getCryptoProvider()
    {
        getServerContext().getCryptoProvider()
    }

    @Memoized
    public ICoreNetworkProvider network()
    {
        getServerContext().network()
    }

    public boolean containsBean(String name)
    {
        getServerContext().containsBean(name)
    }

    public <B> B getBean(String name, Class<B> type) throws Exception
    {
        getServerContext().getBean(name, type)
    }

    public <B> B getBeanSafely(String name, Class<B> type)
    {
        getServerContext().getBeanSafely(name, type)
    }

    public <B> Map<String, B> getBeansOfType(Class<B> type) throws Exception
    {
        getServerContext().getBeansOfType(type)
    }

    public String getOriginalBeanName(String name)
    {
        getServerContext().getOriginalBeanName(name)
    }

    public String uuid()
    {
        getServerContext().uuid()
    }

    @Memoized
    public IScriptingProvider scripting()
    {
        getServerContext().scripting()
    }

    public Resource resource(String location)
    {
        getServerContext().resource(location)
    }

    public Reader reader(String location) throws IOException
    {
        getServerContext().reader(location)
    }

    @Memoized
    public CacheManager getCacheManager(String name)
    {
        getServerContext().getCacheManager(name)
    }

    @Memoized
    public String getPropertyByName(String name)
    {
        getServerContext().getPropertyByName(name)
    }

    @Memoized
    public String getPropertyByName(String name, String otherwise)
    {
        getServerContext().getPropertyByName(name, otherwise)
    }

    public String getPropertyByName(String name, Supplier<String> otherwise)
    {
        getServerContext().getPropertyByName(name, otherwise)
    }

    public String getResolvedExpression(String expr)
    {
        getServerContext().getResolvedExpression(expr)
    }

    public String getResolvedExpression(String expr, String otherwise)
    {
        getServerContext().getResolvedExpression(expr, otherwise)
    }

    public String getResolvedExpression(String expr, Supplier<String> otherwise)
    {
        getServerContext().getResolvedExpression(expr, otherwise)
    }

    public <T> T nulled()
    {
        CommonOps.nulled()
    }

    public <T> T cast(Object value)
    {
        CommonOps.cast(value)
    }

    public boolean isNull(Object value)
    {
        CommonOps.isNull(value)
    }

    public boolean isNonNull(Object value)
    {
        CommonOps.isNonNull(value)
    }

    public <T> T requireNonNullOrElse(T value, T otherwise)
    {
        CommonOps.requireNonNullOrElse(value, otherwise)
    }

    public <T> T requireNonNullOrElse(T value, Supplier<T> otherwise)
    {
        CommonOps.requireNonNullOrElse(value, otherwise)
    }

    public <T> T requireNonNull(T value)
    {
        CommonOps.requireNonNull(value)
    }

    public <T> T requireNonNull(T value, String reason)
    {
        CommonOps.requireNonNull(value, reason)
    }

    public <T> T requireNonNull(T value, Supplier<String> reason)
    {
        CommonOps.requireNonNull(value, reason)
    }

    public <T> Supplier<T> toSupplier(T value)
    {
        CommonOps.toSupplier(value)
    }

    public IntSupplier toSupplier(int value)
    {
        CommonOps.toSupplier(value)
    }

    public LongSupplier toSupplier(long value)
    {
        CommonOps.toSupplier(value)
    }

    public DoubleSupplier toSupplier(double value)
    {
        CommonOps.toSupplier(value)
    }

    public BooleanSupplier toSupplier(boolean value)
    {
        CommonOps.toSupplier(value)
    }

    public <T> Optional<T> toOptional(T value)
    {
        CommonOps.toOptional(value)
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> toList(T... source)
    {
        CommonOps.toList(source)
    }

    public <T> List<T> toList(Stream<T> source)
    {
        CommonOps.toList(source)
    }

    public <T> List<T> toList(Enumeration<? extends T> source)
    {
        CommonOps.toList(source)
    }

    public <T> List<T> toList(Collection<? extends T> source)
    {
        CommonOps.toList(source)
    }

    public <T> List<T> toList(ICursor<? extends T> source)
    {
        CommonOps.toList(source)
    }

    public <T> List<T> toList(IFixedIterable<? extends T> source)
    {
       CommonOps.toList(source)
    }

    public <T> List<T> emptyList()
    {
        CommonOps.emptyList()
    }

    public <K, V> Map<K, V> emptyMap()
    {
        CommonOps.emptyMap()
    }

    public <K, V> LinkedHashMap<K, V> linkedMap()
    {
        CommonOps.linkedMap()
    }

    public <K, V> LinkedHashMap<K, V> linkedMap(Map<? extends K, ? extends V> source)
    {
        CommonOps.linkedMap(source)
    }

    public <K, V> Map<K, V> rawmap(Map source)
    {
       CommonOps.rawmap(source)
    }

    public Map<String, Object> strmap(Map<String, ?> source)
    {
        CommonOps.strmap(source)
    }

    public <T> List<T> toKeys(Map<? extends T, ?> source)
    {
        CommonOps.toKeys(source)
    }

    public <K, V> Map<K, V> toUnmodifiableMap(Map<? extends K, ? extends V> source)
    {
        CommonOps.toUnmodifiableMap(source)
    }

    public <T> List<T> toUnmodifiableList(Collection<? extends T> source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    public <T> List<T> toUnmodifiableList(Stream<T> source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> toUnmodifiableList(T... source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    public <T> List<T> toUnmodifiableList(ICursor<? extends T> source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    public <T> List<T> toUnmodifiableList(IFixedIterable<? extends T> source)
    {
       CommonOps.toUnmodifiableList(source)
    }

    public <T> List<T> toUnmodifiableList(Enumeration<? extends T> source)
    {
       CommonOps.toUnmodifiableList(source)
    }

    public <T> ArrayList<T> arrayListOfSize(int size)
    {
        CommonOps.arrayListOfSize(size)
    }

    public <T> ArrayList<T> arrayList()
    {
        CommonOps.arrayList()
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> arrayList(T... source)
    {
        CommonOps.arrayList(source)
    }

    public <T> ArrayList<T> arrayList(Stream<T> source)
    {
        CommonOps.arrayList(source)
    }

    public <T> ArrayList<T> arrayList(Collection<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    public <T> ArrayList<T> arrayList(ICursor<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    public <T> ArrayList<T> arrayList(IFixedIterable<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    public <T> ArrayList<T> arrayList(Enumeration<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    @SuppressWarnings("unchecked")
    public <T> Stream<T> toStream(T... source)
    {
         CommonOps.toStream(source)
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T... source)
    {
        CommonOps.toArray(source)
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> toListOfLists(List<T>... lists)
    {
        CommonOps.toListOfLists(lists)
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> toListOfListsUnique(List<T>... lists)
    {
        CommonOps.toListOfListsUnique(lists)
    }

    @Memoized
    public String getEnvironmentProperty(String name)
    {
        getServerContext().getEnvironmentProperty(name)
    }

    @Memoized
    public String getEnvironmentProperty(String name, String otherwise)
    {
        getServerContext().getEnvironmentProperty(name, otherwise)
    }

    public String getEnvironmentProperty(String name, Supplier<String> otherwise)
    {
        getServerContext().getEnvironmentProperty(name, otherwise)
    }

    @Memoized
    public String getSystemProperty(String name)
    {
        getServerContext().getSystemProperty(name)
    }

    @Memoized
    public String getSystemProperty(String name, String otherwise)
    {
        getServerContext().getSystemProperty(name, otherwise)
    }

    public String getSystemProperty(String name, Supplier<String> otherwise)
    {
        getServerContext().getSystemProperty(name, otherwise)
    }

    public String format(String format, Object... args)
    {
        String.format(requireNonNull(format), args)
    }

    @Memoized
    public String repeat(String string, int times)
    {
        StringOps.repeat(string, times)
    }

    public String[] toArray(Collection<String> collection)
    {
        StringOps.toArray(collection)
    }

    public String[] toArray(String... collection)
    {
        StringOps.toArray(collection)
    }

    public String[] toArray(Stream<String> stream)
    {
        StringOps.toArray(stream)
    }

    public String[] toUniqueArray(Collection<String> collection)
    {
        StringOps.toUniqueArray(collection)
    }

    public String[] toUniqueArray(String... collection)
    {
       StringOps.toUniqueArray(collection)
    }

    public Stream<String> toUnique(Stream<String> stream)
    {
        StringOps.toUnique(stream)
    }

    public List<String> toUnique(String... collection)
    {
        StringOps.toUnique(collection)
    }

    public List<String> toUnique(Collection<String> collection)
    {
        StringOps.toUnique(collection)
    }

    public List<String> toUniqueTokenStringList(String strings)
    {
        StringOps.toUniqueTokenStringList(strings)
    }

    public String toCommaSeparated(Collection<String> collection)
    {
        StringOps.toCommaSeparated(collection)
    }

    public String toCommaSeparated(String... collection)
    {
        StringOps.toCommaSeparated(collection)
    }

    public String toCommaSeparated(Stream<String> stream)
    {
        StringOps.toCommaSeparated(stream)
    }

    public Collection<String> tokenizeToStringCollection(String string)
    {
        StringOps.tokenizeToStringCollection(string)
    }

    public Collection<String> tokenizeToStringCollection(String string, String delimiters)
    {
        StringOps.tokenizeToStringCollection(string, delimiters)
    }

    public Collection<String> tokenizeToStringCollection(String string, boolean trim, boolean ignore)
    {
        StringOps.tokenizeToStringCollection(string, trim, ignore)
    }

    public Collection<String> tokenizeToStringCollection(String string, String delimiters, boolean trim, boolean ignore)
    {
        StringOps.tokenizeToStringCollection(string, delimiters, trim, ignore)
    }

    public String toPrintableString(Collection<String> collection)
    {
        StringOps.toPrintableString(collection)
    }

    public String toPrintableString(String... list)
    {
        StringOps.toPrintableString(list)
    }

    @Memoized
    public String toTrimOrNull(String string)
    {
        StringOps.toTrimOrNull(string)
    }

    @Memoized
    public String toTrimOrElse(String string, String otherwise)
    {
        StringOps.toTrimOrElse(string, otherwise)
    }

    public String toTrimOrElse(String string, Supplier<String> otherwise)
    {
        StringOps.toTrimOrElse(string, otherwise)
    }

    public String requireTrimOrNull(String string)
    {
        StringOps.requireTrimOrNull(string)
    }

    public String requireTrimOrNull(String string, String reason)
    {
        StringOps.requireTrimOrNull(string, reason)
    }

    public String requireTrimOrNull(String string, Supplier<String> reason)
    {
        StringOps.requireTrimOrNull(string, reason)
    }

    @Memoized
    public String reverse(String string)
    {
        StringOps.reverse(string)
    }
}
