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

import java.util.concurrent.Future
import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Supplier
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream

import org.slf4j.Logger
import org.springframework.beans.BeansException
import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.web.context.WebApplicationContext

import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.common.api.java.util.StringOps
import com.themodernway.common.api.types.ICursor
import com.themodernway.common.api.types.IFixedIterable
import com.themodernway.server.core.NanoTimer
import com.themodernway.server.core.content.ICoreContentTypeMapper
import com.themodernway.server.core.file.vfs.IFileItemStorage
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider
import com.themodernway.server.core.json.JSONArray
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.JSONObjectSuppliersBuilder
import com.themodernway.server.core.json.binder.IBinder
import com.themodernway.server.core.json.path.IJSONPathOperations
import com.themodernway.server.core.json.path.JSONPath
import com.themodernway.server.core.logging.LoggingOps
import com.themodernway.server.core.mail.IMailSender
import com.themodernway.server.core.mail.IMailSenderProvider
import com.themodernway.server.core.runtime.IManagementOperations
import com.themodernway.server.core.runtime.IMemoryStatistics
import com.themodernway.server.core.runtime.IOperatingSystemStatistics
import com.themodernway.server.core.runtime.IRuntimeStatistics
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
import com.themodernway.server.core.support.spring.network.PathParameters

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public class CoreGroovySupport implements IServerContext, Closeable
{
    private final static CoreGroovySupport  INSTANCE = new CoreGroovySupport()

    private final Logger                    m_logger = LoggingOps.getLogger(getClass())

    @Memoized
    public static CoreGroovySupport getCoreGroovySupport()
    {
        INSTANCE
    }

    public CoreGroovySupport()
    {
    }

    @Override
    public Logger logger()
    {
        m_logger
    }

    @Memoized
    public IJSONPathOperations jsonpath()
    {
         JSONPath.operations()
    }

    @Memoized
    public IServerContext getServerContext()
    {
        ServerContextInstance.getServerContextInstance()
    }

    @Override
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
    public IManagementOperations getManagementOperations()
    {
        getServerContext().getManagementOperations()
    }

    @Memoized
    public IMemoryStatistics getMemoryStatistics()
    {
        getManagementOperations().getMemoryStatistics()
    }

    @Memoized
    public IRuntimeStatistics getRuntimeStatistics()
    {
        getManagementOperations().getRuntimeStatistics()
    }

    @Memoized
    public IOperatingSystemStatistics getOperatingSystemStatistics()
    {
        getManagementOperations().getOperatingSystemStatistics()
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
    public JSONObject getBuildDescriptors()
    {
        getBuildDescriptorProvider().toJSONObject()
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

    @Override
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

    public NanoTimer nstimer()
    {
        new NanoTimer()
    }

    @Memoized
    public ICoreContentTypeMapper getContentTypeMapper()
    {
        getServerContext().getContentTypeMapper()
    }

    @Override
    public boolean containsBean(String name)
    {
        getServerContext().containsBean(name)
    }

    @Override
    public <B> B getBean(String name, Class<B> type) throws BeansException
    {
        getServerContext().getBean(name, type)
    }

    @Override
    public <B> B getBeanSafely(String name, Class<B> type)
    {
        getServerContext().getBeanSafely(name, type)
    }

    @Override
    public <B> Map<String, B> getBeansOfType(Class<B> type) throws BeansException
    {
        getServerContext().getBeansOfType(type)
    }

    @Override
    public String getOriginalBeanName(String name)
    {
        getServerContext().getOriginalBeanName(name)
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public String uuid()
    {
        getServerContext().uuid()
    }

    @Memoized
    public IScriptingProvider scripting()
    {
        getServerContext().scripting()
    }

    @Override
    public Resource resource(String location)
    {
        getServerContext().resource(location)
    }

    @Override
    public Reader reader(String location) throws IOException
    {
        getServerContext().reader(location)
    }

    @Memoized
    public CacheManager getCacheManager(String name)
    {
        getServerContext().getCacheManager(name)
    }

    public <T> T parallel(T collection)
    {
        CoreGroovyOps.parallel(collection)
    }

    public void pause(long time)
    {
        CoreGroovyOps.pause(time)
    }

    public void pause(long time, Closure closure)
    {
        CoreGroovyOps.pause(time, closure)
    }

    @Override
    public IBinder binder()
    {
        getServerContext().binder()
    }

    @Override
    public JSONArray jarr()
    {
        new JSONArray()
    }

    @Override
    public JSONArray jarr(Object... objects)
    {
        jarr(CommonOps.toList(objects))
    }

    @Override
    public JSONArray jarr(Collection<?> collection)
    {
        getServerContext().jarr(collection)
    }

    @Override
    public JSONArray jarr(Future<?> future)
    {
        getServerContext().jarr(future)
    }

    @Override
    public JSONArray jarr(JSONObject object)
    {
        getServerContext().jarr(object)
    }

    @Override
    public JSONArray jarr(List<?> list)
    {
        getServerContext().jarr(list)
    }

    @Override
    public JSONArray jarr(Map<String, ?> map)
    {
        getServerContext().jarr(map)
    }

    @Override
    public JSONArray jarr(Object object)
    {
        getServerContext().jarr(object)
    }

    @Override
    public JSONArray jarr(Optional<?> optional)
    {
        getServerContext().jarr(optional)
    }

    @Override
    public JSONArray jarr(Stream<?> stream)
    {
        getServerContext().jarr(stream)
    }

    @Override
    public JSONArray jarr(String name, Object value)
    {
        getServerContext().jarr(name, value)
    }

    @Override
    public JSONObject json()
    {
        new JSONObject()
    }

    @Override
    public JSONObject json(JSONObject object)
    {
        getServerContext().json(object)
    }

    @Override
    public JSONObject json(Collection<?> collection)
    {
        getServerContext().json(collection)
    }

    @Override
    public JSONObject json(Future<?> future)
    {
        getServerContext().json(future)
    }

    @Override
    public JSONObject json(List<?> list)
    {
        getServerContext().json(list)
    }

    @Override
    public JSONObject json(Map<String, ?> map)
    {
        new JSONObject(map)
    }

    @Override
    public JSONObject json(Object object)
    {
        getServerContext().json(object)
    }

    @Override
    public JSONObject json(Stream<?> stream)
    {
        getServerContext().json(stream)
    }

    @Override
    public JSONObject json(Optional<?> optional)
    {
        getServerContext().json(optional)
    }

    @Override
    public JSONObject json(String name, Object value)
    {
        new JSONObject(name, value)
    }

    @Override
    public JSONObject json(Object... args)
    {
        json(jarr(args))
    }

    @Override
    public JSONArray jarr(JSONObjectSuppliersBuilder builder)
    {
        new JSONArray(CommonOps.requireNonNull(builder))
    }

    @Override
    public JSONObject json(JSONObjectSuppliersBuilder builder)
    {
        new JSONObject(CommonOps.requireNonNull(builder))
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

    @Override
    public String getPropertyByName(String name, Supplier<String> otherwise)
    {
        getServerContext().getPropertyByName(name, otherwise)
    }

    @Override
    public String getResolvedExpression(String expr)
    {
        getServerContext().getResolvedExpression(expr)
    }

    @Override
    public String getResolvedExpression(String expr, String otherwise)
    {
        getServerContext().getResolvedExpression(expr, otherwise)
    }

    @Override
    public String getResolvedExpression(String expr, Supplier<String> otherwise)
    {
        getServerContext().getResolvedExpression(expr, otherwise)
    }

    @Override
    public <T> T CAST(Object value)
    {
        CommonOps.CAST(value)
    }

    @Override
    public boolean isNull(Object value)
    {
        CommonOps.isNull(value)
    }

    @Override
    public boolean isNonNull(Object value)
    {
        CommonOps.isNonNull(value)
    }

    @Override
    public <T> T requireNonNullOrElse(T value, T otherwise)
    {
        CommonOps.requireNonNullOrElse(value, otherwise)
    }

    @Override
    public <T> T requireNonNullOrElse(T value, Supplier<T> otherwise)
    {
        CommonOps.requireNonNullOrElse(value, otherwise)
    }

    @Override
    public <T> T requireNonNull(T value)
    {
        CommonOps.requireNonNull(value)
    }

    @Override
    public <T> T requireNonNull(T value, String reason)
    {
        CommonOps.requireNonNull(value, reason)
    }

    @Override
    public <T> T requireNonNull(T value, Supplier<String> reason)
    {
        CommonOps.requireNonNull(value, reason)
    }

    @Override
    public <T> Supplier<T> toSupplier(T value)
    {
        CommonOps.toSupplier(value)
    }

    @Override
    public IntSupplier toSupplier(int value)
    {
        CommonOps.toSupplier(value)
    }

    @Override
    public LongSupplier toSupplier(long value)
    {
        CommonOps.toSupplier(value)
    }

    @Override
    public DoubleSupplier toSupplier(double value)
    {
        CommonOps.toSupplier(value)
    }

    @Override
    public BooleanSupplier toSupplier(boolean value)
    {
        CommonOps.toSupplier(value)
    }

    @Override
    public <T> Optional<T> toOptional(T value)
    {
        CommonOps.toOptional(value)
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> toList(T... source)
    {
        CommonOps.toList(source)
    }

    @Override
    public <T> List<T> toList(final Stream<T> source)
    {
        CommonOps.toList(source)
    }

    @Override
    public <T> List<T> toList(Enumeration<? extends T> source)
    {
        CommonOps.toList(source)
    }

    @Override
    public <T> List<T> toList(Collection<? extends T> source)
    {
        CommonOps.toList(source)
    }

    @Override
    public <T> List<T> toList(ICursor<? extends T> source)
    {
        CommonOps.toList(source)
    }

    @Override
    public <T> List<T> toList(IFixedIterable<? extends T> source)
    {
       CommonOps.toList(source)
    }

    @Override
    public <T> List<T> emptyList()
    {
        CommonOps.emptyList()
    }

    @Override
    public <K, V> Map<K, V> emptyMap()
    {
        CommonOps.emptyMap()
    }

    @Override
    public <K, V> LinkedHashMap<K, V> linkedMap()
    {
        CommonOps.linkedMap()
    }

    @Override
    public <K, V> LinkedHashMap<K, V> linkedMap(Map<? extends K, ? extends V> source)
    {
        CommonOps.linkedMap(source)
    }

    @Override
    public <K, V> Map<K, V> rawmap(Map source)
    {
       CommonOps.rawmap(source)
    }

    @Override
    public Map<String, Object> strmap(Map<String, ?> source)
    {
        CommonOps.strmap(source)
    }

    @Override
    public <T> List<T> toKeys(Map<? extends T, ?> source)
    {
        CommonOps.toKeys(source)
    }

    @Override
    public <K, V> Map<K, V> toUnmodifiableMap(Map<? extends K, ? extends V> source)
    {
        CommonOps.toUnmodifiableMap(source)
    }

    @Override
    public <T> List<T> toUnmodifiableList(Collection<? extends T> source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    @Override
    public <T> List<T> toUnmodifiableList(Stream<T> source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> toUnmodifiableList(T... source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    @Override
    public <T> List<T> toUnmodifiableList(ICursor<? extends T> source)
    {
        CommonOps.toUnmodifiableList(source)
    }

    @Override
    public <T> List<T> toUnmodifiableList(IFixedIterable<? extends T> source)
    {
       CommonOps.toUnmodifiableList(source)
    }

    @Override
    public <T> List<T> toUnmodifiableList(Enumeration<? extends T> source)
    {
       CommonOps.toUnmodifiableList(source)
    }

    @Override
    public <T> ArrayList<T> arrayListOfSize(int size)
    {
        CommonOps.arrayListOfSize(size)
    }

    @Override
    public <T> ArrayList<T> arrayList()
    {
        CommonOps.arrayList()
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> arrayList(T... source)
    {
        CommonOps.arrayList(source)
    }

    @Override
    public <T> ArrayList<T> arrayList(Stream<T> source)
    {
        CommonOps.arrayList(source)
    }

    @Override
    public <T> ArrayList<T> arrayList(Collection<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    @Override
    public <T> ArrayList<T> arrayList(ICursor<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    @Override
    public <T> ArrayList<T> arrayList(IFixedIterable<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    @Override
    public <T> ArrayList<T> arrayList(Enumeration<? extends T> source)
    {
        CommonOps.arrayList(source)
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T... source)
    {
        CommonOps.toArray(source)
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> toListOfLists(List<T>... lists)
    {
        CommonOps.toListOfLists(lists)
    }

    @Override
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

    @Override
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

    @Override
    public String getSystemProperty(String name, Supplier<String> otherwise)
    {
        getServerContext().getSystemProperty(name, otherwise)
    }

    @Override
    public PathParameters parameters(Map<String, ?> vars)
    {
        new PathParameters(requireNonNull(vars))
    }

    @Override
    public String format(String format, Object... args)
    {
        String.format(requireNonNull(format), args)
    }

    @Memoized
    public String repeat(String string, int times)
    {
        StringOps.repeat(string, times)
    }

    @Override
    public String[] toArray(Collection<String> collection)
    {
        StringOps.toArray(collection)
    }

    @Override
    public String[] toArray(String... collection)
    {
        StringOps.toArray(collection)
    }

    @Override
    public String[] toArray(Stream<String> stream)
    {
        StringOps.toArray(stream)
    }

    @Override
    public String[] toUniqueArray(Collection<String> collection)
    {
        StringOps.toUniqueArray(collection)
    }

    @Override
    public String[] toUniqueArray(String... collection)
    {
       StringOps.toUniqueArray(collection)
    }

    @Override
    public Stream<String> toUnique(Stream<String> stream)
    {
        StringOps.toUnique(stream)
    }

    @Override
    public List<String> toUnique(String... collection)
    {
        StringOps.toUnique(collection)
    }

    @Override
    public List<String> toUnique(Collection<String> collection)
    {
        StringOps.toUnique(collection)
    }

    @Override
    public List<String> toUniqueTokenStringList(String strings)
    {
        StringOps.toUniqueTokenStringList(strings)
    }

    @Override
    public String toCommaSeparated(Collection<String> collection)
    {
        StringOps.toCommaSeparated(collection)
    }

    @Override
    public String toCommaSeparated(String... collection)
    {
        StringOps.toCommaSeparated(collection)
    }

    @Override
    public String toCommaSeparated(Stream<String> stream)
    {
        StringOps.toCommaSeparated(stream)
    }

    @Override
    public Collection<String> tokenizeToStringCollection(String string)
    {
        StringOps.tokenizeToStringCollection(string)
    }

    @Override
    public Collection<String> tokenizeToStringCollection(String string, String delimiters)
    {
        StringOps.tokenizeToStringCollection(string, delimiters)
    }

    @Override
    public Collection<String> tokenizeToStringCollection(String string, boolean trim, boolean ignore)
    {
        StringOps.tokenizeToStringCollection(string, trim, ignore)
    }

    @Override
    public Collection<String> tokenizeToStringCollection(String string, String delimiters, boolean trim, boolean ignore)
    {
        StringOps.tokenizeToStringCollection(string, delimiters, trim, ignore)
    }

    @Override
    public String toPrintableString(Collection<String> collection)
    {
        StringOps.toPrintableString(collection)
    }

    @Override
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

    @Override
    public String toTrimOrElse(String string, Supplier<String> otherwise)
    {
        StringOps.toTrimOrElse(string, otherwise)
    }

    @Override
    public String requireTrimOrNull(String string)
    {
        StringOps.requireTrimOrNull(string)
    }

    @Override
    public String requireTrimOrNull(String string, String reason)
    {
        StringOps.requireTrimOrNull(string, reason)
    }

    @Override
    public String requireTrimOrNull(String string, Supplier<String> reason)
    {
        StringOps.requireTrimOrNull(string, reason)
    }

    @Memoized
    public String reverse(String string)
    {
        StringOps.reverse(string)
    }

    @Override
    public String failIfNullBytePresent(String string)
    {
        StringOps.failIfNullBytePresent(string)
    }

    @Override
    public <T> Stream<T> emptyStream()
    {
        CommonOps.emptyStream()
    }

    @Override
    public <T> Stream<T> toStream(T source)
    {
        CommonOps.toStream(source)
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> toStream(T... source)
    {
        CommonOps.toStream(source)
    }

    @Override
    public IntStream toStream(int source)
    {
        CommonOps.toStream(source)
    }

    @Override
    public IntStream toStream(int... source)
    {
        CommonOps.toStream(source)
    }

    @Override
    public LongStream toStream(long source)
    {
        CommonOps.toStream(source)
    }

    @Override
    public LongStream toStream(long... source)
    {
        CommonOps.toStream(source)
    }

    @Override
    public DoubleStream toStream(double source)
    {
        CommonOps.toStream(source)
    }

    @Override
    public DoubleStream toStream(double... source)
    {
        CommonOps.toStream(source)
    }
}
