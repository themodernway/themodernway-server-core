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
 * limitations under the License.
 */

package com.themodernway.server.core.support

import java.util.concurrent.Future
import java.util.function.Supplier
import java.util.stream.Stream

import org.apache.log4j.Logger
import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.PollableChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.web.context.WebApplicationContext

import com.themodernway.server.core.file.vfs.IFileItemStorage
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider
import com.themodernway.server.core.jmx.management.ICoreServerManager
import com.themodernway.server.core.json.JSONArray
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.binder.BinderType
import com.themodernway.server.core.json.binder.IBinder
import com.themodernway.server.core.pubsub.JSONMessageBuilder
import com.themodernway.server.core.scripting.IScriptingProvider
import com.themodernway.server.core.security.AuthorizationResult
import com.themodernway.server.core.security.IAuthorizationProvider
import com.themodernway.server.core.security.ICryptoProvider
import com.themodernway.server.core.security.ISignatoryProvider
import com.themodernway.server.core.security.session.IServerSessionRepository
import com.themodernway.server.core.security.session.IServerSessionRepositoryProvider
import com.themodernway.server.core.support.spring.IBuildDescriptorProvider
import com.themodernway.server.core.support.spring.IPropertiesResolver
import com.themodernway.server.core.support.spring.IServerContext
import com.themodernway.server.core.support.spring.IServletContextCustomizerProvider
import com.themodernway.server.core.support.spring.ServerContextInstance
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketService
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketServiceProvider

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public class CoreGroovySupport implements IServerContext, Closeable
{
    private static final CoreGroovySupport  INSTANCE = new CoreGroovySupport()

    private final Logger                    m_logger = Logger.getLogger(getClass())

    @Memoized
    public static final CoreGroovySupport getCoreGroovySupport()
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
    public IServerContext getServerContext()
    {
        ServerContextInstance.getServerContextInstance()
    }

    @Memoized
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
    public List<String> getPrincipalsKeys()
    {
        getServerContext().getPrincipalsKeys()
    }

    @Memoized
    public ICoreServerManager getCoreServerManager()
    {
        getServerContext().getCoreServerManager()
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
        getFileItemStorageProvider().getItem(Objects.requireNonNull(name))
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
    public String getPropertyByName(String name)
    {
        getServerContext().getPropertyByName(Objects.requireNonNull(name))
    }

    @Memoized
    public String getPropertyByName(String name, String otherwise)
    {
        getServerContext().getPropertyByName(Objects.requireNonNull(name), otherwise)
    }

    @Override
    public String getPropertyByName(String name, Supplier<String> otherwise)
    {
        getServerContext().getPropertyByName(Objects.requireNonNull(name), otherwise)
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
        getServerSessionRepositoryProvider().getServerSessionRepository(Objects.requireNonNull(domain))
    }

    @Override
    public AuthorizationResult isAuthorized(Object target, List<String> roles)
    {
        getServerContext().isAuthorized(Objects.requireNonNull(target), Objects.requireNonNull(roles))
    }

    @Memoized
    public ICryptoProvider getCryptoProvider()
    {
        getServerContext().getCryptoProvider()
    }

    @Memoized
    public ISignatoryProvider getSignatoryProvider()
    {
        getServerContext().getSignatoryProvider()
    }

    @Memoized
    public ICoreNetworkProvider network()
    {
        getServerContext().network()
    }

    @Memoized
    public MessageChannel getMessageChannel(String name)
    {
        getServerContext().getMessageChannel(Objects.requireNonNull(name))
    }

    @Memoized
    public PublishSubscribeChannel getPublishSubscribeChannel(String name)
    {
        getServerContext().getPublishSubscribeChannel(Objects.requireNonNull(name))
    }

    @Memoized
    public SubscribableChannel getSubscribableChannel(String name)
    {
        getServerContext().getSubscribableChannel(Objects.requireNonNull(name))
    }

    @Memoized
    public PollableChannel getPollableChannel(String name)
    {
        getServerContext().getPollableChannel(Objects.requireNonNull(name))
    }

    @Override
    public boolean publish(String name, JSONObject message)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message)))
    }

    @Override
    public boolean publish(String name, JSONObject message, long timeout)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message)), timeout)
    }

    @Override
    public boolean publish(String name, JSONObject message, Map<String, ?> headers)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message), Objects.requireNonNull(headers)))
    }

    @Override
    public boolean publish(String name, JSONObject message, Map<String, ?> headers, long timeout)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message), Objects.requireNonNull(headers)), timeout)
    }

    @Override
    public <T> boolean publish(String name, Message<T> message)
    {
        def channel = getMessageChannel(Objects.requireNonNull(name))

        if (channel)
        {
            return channel.send(Objects.requireNonNull(message))
        }
        throw new IllegalArgumentException("MessageChannel ${name} does not exist.")
    }

    @Override
    public <T> boolean publish(String name, Message<T> message, long timeout)
    {
        def channel = getMessageChannel(Objects.requireNonNull(name))

        if (channel)
        {
            return channel.send(Objects.requireNonNull(message), timeout)
        }
        throw new IllegalArgumentException("MessageChannel ${name} does not exist.")
    }

    @Override
    public boolean containsBean(String name)
    {
        getServerContext().containsBean(Objects.requireNonNull(name))
    }

    @Override
    public <B> B getBean(String name, Class<B> type) throws Exception
    {
        getServerContext().getBean(Objects.requireNonNull(name), Objects.requireNonNull(type))
    }

    @Override
    public <B> B getBeanSafely(String name, Class<B> type)
    {
        getServerContext().getBeanSafely(Objects.requireNonNull(name), Objects.requireNonNull(type))
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

    @Override
    public String toTrimOrNull(String string)
    {
        getServerContext().toTrimOrNull(string)
    }

    @Override
    public String toTrimOrElse(String string, String otherwise)
    {
        getServerContext().toTrimOrElse(string, otherwise)
    }

    @Override
    public String toTrimOrElse(String string, Supplier<String> otherwise)
    {
        getServerContext().toTrimOrElse(string, otherwise)
    }

    @Override
    public <T> T requireNonNull(T object)
    {
        Objects.requireNonNull(object)
    }

    @Override
    public <T> T requireNonNull(T object, String message)
    {
        Objects.requireNonNull(object, message)
    }

    @Override
    public <T> T requireNonNull(T object, Supplier<String> message)
    {
        Objects.requireNonNull(object, message)
    }

    @Override
    public <T> T requireNonNullOrElse(T object, T otherwise)
    {
        Objects.nonNull(object) ? object : otherwise
    }

    @Override
    public <T> T requireNonNullOrElse(T object, Supplier<T> otherwise)
    {
        Objects.nonNull(object) ? object : otherwise.get()
    }

    @Memoized
    public IScriptingProvider scripting()
    {
        getServerContext().scripting()
    }

    @Override
    public Resource resource(String location)
    {
        getServerContext().resource(Objects.requireNonNull(location))
    }

    @Override
    public Reader reader(String location) throws IOException
    {
        getServerContext().reader(Objects.requireNonNull(location))
    }

    @Memoized
    public IWebSocketServiceProvider getWebSocketServiceProvider()
    {
        getServerContext().getWebSocketServiceProvider()
    }

    @Memoized
    public IWebSocketService getWebSocketService(String name)
    {
        getWebSocketServiceProvider().getWebSocketService(name)
    }

    @Memoized
    public CacheManager getCacheManager(String name)
    {
        getServerContext().getCacheManager(name)
    }

    @Memoized
    public CoreGroovyParallel parallel()
    {
        CoreGroovyParallel.getCoreGroovyParallel()
    }

    @Override
    public IBinder binder()
    {
        getServerContext().binder()
    }

    @Override
    public IBinder binder(BinderType type)
    {
        getServerContext().binder(type)
    }

    @Override
    public JSONArray jarr()
    {
        new JSONArray()
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
        new JSONObject(Objects.requireNonNull(map))
    }

    @Override
    public JSONObject json(Object object)
    {
        getServerContext().json(object)
    }

    @Override
    public JSONObject json(Optional<?> optional)
    {
        getServerContext().json(optional)
    }

    @Override
    public JSONObject json(String name, Object value)
    {
        new JSONObject(Objects.requireNonNull(name), value)
    }
}
