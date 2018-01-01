/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import java.util.function.Supplier

import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.PollableChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.web.context.WebApplicationContext

import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.server.core.file.vfs.IFileItemStorage
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.support.JSONTrait
import com.themodernway.server.core.mail.IMailSender
import com.themodernway.server.core.mail.IMailSenderProvider
import com.themodernway.server.core.pubsub.JSONMessageBuilder
import com.themodernway.server.core.scripting.IScriptingProvider
import com.themodernway.server.core.security.IAuthorizationProvider
import com.themodernway.server.core.security.IAuthorizationResult
import com.themodernway.server.core.security.ICryptoProvider
import com.themodernway.server.core.security.ISignatoryProvider
import com.themodernway.server.core.security.session.IServerSessionRepository
import com.themodernway.server.core.security.session.IServerSessionRepositoryProvider
import com.themodernway.server.core.support.spring.IBuildDescriptorProvider
import com.themodernway.server.core.support.spring.IPropertiesResolver
import com.themodernway.server.core.support.spring.IServerContext
import com.themodernway.server.core.support.spring.IServletContextCustomizerProvider
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketService
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketServiceProvider

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public trait CoreGroovyTrait implements CoreGroovyParallelTrait, JSONTrait
{
    @Memoized
    public IServerContext getServerContext()
    {
        CoreGroovySupport.getCoreGroovySupport()
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
        getFileItemStorageProvider().getItem(requireNonNull(name))
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
        getServerContext().getPropertyByName(requireNonNull(name))
    }

    @Memoized
    public String getPropertyByName(String name, String otherwise)
    {
        getServerContext().getPropertyByName(requireNonNull(name), otherwise)
    }

    public String getPropertyByName(String name, Supplier<String> otherwise)
    {
        getServerContext().getPropertyByName(requireNonNull(name), otherwise)
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
        getServerSessionRepositoryProvider().getServerSessionRepository(requireNonNull(domain))
    }

    public IAuthorizationResult isAuthorized(Object target, List<String> roles)
    {
        getServerContext().isAuthorized(requireNonNull(target), requireNonNull(roles))
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
        getServerContext().getMessageChannel(requireNonNull(name))
    }

    @Memoized
    public SubscribableChannel getSubscribableChannel(String name)
    {
        getServerContext().getSubscribableChannel(requireNonNull(name))
    }

    @Memoized
    public PollableChannel getPollableChannel(String name)
    {
        getServerContext().getPollableChannel(requireNonNull(name))
    }

    public boolean publish(String name, JSONObject message)
    {
        publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message)))
    }

    public boolean publish(String name, JSONObject message, long timeout)
    {
        publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message)), timeout)
    }

    public boolean publish(String name, JSONObject message, Map<String, ?> headers)
    {
        publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message), requireNonNull(headers)))
    }

    public boolean publish(String name, JSONObject message, Map<String, ?> headers, long timeout)
    {
        publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message), requireNonNull(headers)), timeout)
    }

    public <T> boolean publish(String name, Message<T> message)
    {
        getServerContext().publish(requireNonNull(name), requireNonNull(message))
    }

    public <T> boolean publish(String name, Message<T> message, long timeout)
    {
        getServerContext().publish(requireNonNull(name), requireNonNull(message), timeout)
    }

    public boolean containsBean(String name)
    {
        getServerContext().containsBean(requireNonNull(name))
    }

    public <B> B getBean(String name, Class<B> type) throws Exception
    {
        getServerContext().getBean(requireNonNull(name), requireNonNull(type))
    }

    public <B> B getBeanSafely(String name, Class<B> type)
    {
        getServerContext().getBeanSafely(requireNonNull(name), requireNonNull(type))
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

    public String toTrimOrNull(String string)
    {
        getServerContext().toTrimOrNull(string)
    }

    public String toTrimOrElse(String string, String otherwise)
    {
        getServerContext().toTrimOrElse(string, otherwise)
    }

    public String toTrimOrElse(String string, Supplier<String> otherwise)
    {
        getServerContext().toTrimOrElse(string, otherwise)
    }

    public <T> T requireNonNull(T object)
    {
        CommonOps.requireNonNull(object)
    }

    public <T> T requireNonNull(T object, String message)
    {
        CommonOps.requireNonNull(object, message)
    }

    public <T> T requireNonNull(T object, Supplier<String> message)
    {
        CommonOps.requireNonNull(object, message)
    }

    public <T> T requireNonNullOrElse(T object, T otherwise)
    {
        CommonOps.requireNonNullOrElse(object, otherwise)
    }

    public <T> T requireNonNullOrElse(T object, Supplier<T> otherwise)
    {
        CommonOps.requireNonNullOrElse(object, otherwise)
    }

    @Memoized
    public IScriptingProvider scripting()
    {
        getServerContext().scripting()
    }

    public Resource resource(String location)
    {
        getServerContext().resource(requireNonNull(location))
    }

    public Reader reader(String location) throws IOException
    {
        getServerContext().reader(requireNonNull(location))
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
    public IMailSenderProvider getMailSenderProvider()
    {
        getServerContext().getMailSenderProvider()
    }

    @Memoized
    public IMailSender getMailSender(String name)
    {
        getMailSenderProvider().getItem(requireNonNull(name))
    }
}
