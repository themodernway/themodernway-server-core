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

import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.PollableChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.web.context.WebApplicationContext

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.file.storage.IFileItemStorageProvider
import com.themodernway.server.core.jmx.management.ICoreServerManager
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.support.JSONTrait
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
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketService
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketServiceProvider

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public trait CoreGroovyTrait implements JSONTrait
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

    public boolean publish(String name, JSONObject message)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message)))
    }

    public boolean publish(String name, JSONObject message, long timeout)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message)), timeout)
    }

    public boolean publish(String name, JSONObject message, Map<String, ?> headers)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message), Objects.requireNonNull(headers)))
    }

    public boolean publish(String name, JSONObject message, Map<String, ?> headers, long timeout)
    {
        publish(Objects.requireNonNull(name), JSONMessageBuilder.createMessage(Objects.requireNonNull(message), Objects.requireNonNull(headers)), timeout)
    }

    public <T> boolean publish(String name, Message<T> message)
    {
        getServerContext().publish(Objects.requireNonNull(name), Objects.requireNonNull(message))
    }

    public <T> boolean publish(String name, Message<T> message, long timeout)
    {
        getServerContext().publish(Objects.requireNonNull(name), Objects.requireNonNull(message), timeout)
    }

    public boolean containsBean(String name)
    {
        getServerContext().containsBean(Objects.requireNonNull(name))
    }

    public <B> B getBean(String name, Class<B> type) throws Exception
    {
        getServerContext().getBean(Objects.requireNonNull(name), Objects.requireNonNull(type))
    }

    public <B> B getBeanSafely(String name, Class<B> type)
    {
        getServerContext().getBeanSafely(Objects.requireNonNull(name), Objects.requireNonNull(type))
    }

    public String uuid()
    {
        getServerContext().uuid()
    }

    public String toTrimOrNull(String string)
    {
        StringOps.toTrimOrNull(string)
    }

    public String toTrimOrElse(String string, String otherwise)
    {
        StringOps.toTrimOrElse(string, otherwise)
    }

    public <T> T requireNonNull(T object)
    {
        Objects.requireNonNull(object)
    }

    public <T> T requireNonNull(T object, String message)
    {
        Objects.requireNonNull(object, message)
    }

    @Memoized
    public IScriptingProvider scripting()
    {
        getServerContext().scripting()
    }

    public Resource resource(String location)
    {
        getServerContext().resource(Objects.requireNonNull(location))
    }

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
}
