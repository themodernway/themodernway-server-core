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

package com.themodernway.server.core.support.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.file.vfs.IFileItemStorage;
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.support.CoreJSONOperations;
import com.themodernway.server.core.mail.IMailSender;
import com.themodernway.server.core.mail.IMailSenderProvider;
import com.themodernway.server.core.pubsub.JSONMessageBuilder;
import com.themodernway.server.core.scripting.IScriptingProvider;
import com.themodernway.server.core.security.DefaultAuthorizationProvider;
import com.themodernway.server.core.security.IAuthorizationProvider;
import com.themodernway.server.core.security.IAuthorizationResult;
import com.themodernway.server.core.security.ICryptoProvider;
import com.themodernway.server.core.security.ISignatoryProvider;
import com.themodernway.server.core.security.session.IServerSessionRepository;
import com.themodernway.server.core.security.session.IServerSessionRepositoryProvider;
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider;
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketService;
import com.themodernway.server.core.support.spring.network.websocket.IWebSocketServiceProvider;

public class ServerContextInstance extends CoreJSONOperations implements IServerContext
{
    private static ApplicationContext                  APPCONTEXT   = null;

    private final static DefaultAuthorizationProvider  DEFAULT_AUTH = new DefaultAuthorizationProvider();

    private final static DefaultPrincipalsKeysProvider DEFAULT_KEYS = new DefaultPrincipalsKeysProvider();

    private final static ServerContextInstance         INSTANCE     = new ServerContextInstance();

    private final Logger                               m_logger     = Logger.getLogger(getClass());

    public static final ServerContextInstance getServerContextInstance()
    {
        return INSTANCE;
    }

    protected ServerContextInstance()
    {
    }

    public static final void setApplicationContext(final ApplicationContext context)
    {
        APPCONTEXT = context;
    }

    @Override
    public final boolean isApplicationContextInitialized()
    {
        return (null != APPCONTEXT);
    }

    @Override
    public final ApplicationContext getApplicationContext()
    {
        return requireNonNull(APPCONTEXT, "ApplicationContext is null, initialization error.");
    }

    @Override
    public final WebApplicationContext getWebApplicationContext()
    {
        final ApplicationContext context = getApplicationContext();

        if (context instanceof WebApplicationContext)
        {
            return (WebApplicationContext) context;
        }
        return null;
    }

    @Override
    public final Environment getEnvironment()
    {
        return requireNonNull(getApplicationContext().getEnvironment(), "Environment is null, initialization error.");
    }

    @Override
    public final boolean containsBean(final String name)
    {
        return getApplicationContext().containsBean(requireNonNull(name));
    }

    @Override
    public final <B> B getBean(final String name, final Class<B> type) throws Exception
    {
        return getApplicationContext().getBean(requireNonNull(name), requireNonNull(type));
    }

    @Override
    public final <B> B getBeanSafely(final String name, final Class<B> type)
    {
        requireNonNull(name);

        requireNonNull(type);

        B bean = null;

        try
        {
            final ApplicationContext ctxt = getApplicationContext();

            if (ctxt.containsBean(name))
            {
                try
                {
                    bean = ctxt.getBean(name, type);
                }
                catch (final Exception e)
                {
                    logger().error("getBeanSafely(" + name + "," + type.getName() + ") error.", e);
                }
                if (null == bean)
                {
                    try
                    {
                        final Object look = ctxt.getBean(name);

                        if ((null != look) && (type.isAssignableFrom(look.getClass())))
                        {
                            bean = type.cast(look);
                        }
                    }
                    catch (final Exception e)
                    {
                        logger().error("getBeanSafely(" + name + "," + type.getName() + ") error.", e);
                    }
                }
            }
        }
        catch (final Exception e)
        {
            logger().error("getBeanSafely(" + name + "," + type.getName() + ") error.", e);
        }
        return bean;
    }

    @Override
    public <B> Map<String, B> getBeansOfType(final Class<B> type) throws Exception
    {
        return CommonOps.toUnmodifiableMap(getApplicationContext().getBeansOfType(requireNonNull(type)));
    }

    @Override
    public String getOriginalBeanName(final String name)
    {
        return toTrimOrNull(BeanFactoryUtils.originalBeanName(requireNonNull(name)));
    }

    @Override
    public final IPropertiesResolver getPropertiesResolver()
    {
        return this;
    }

    @Override
    public final String getPropertyByName(final String name)
    {
        final String valu = getEnvironment().getProperty(requireNonNull(name));

        if (null != valu)
        {
            return valu;
        }
        return getCorePropertiesResolver().getPropertyByName(name);
    }

    @Override
    public final String getPropertyByName(final String name, final String otherwise)
    {
        final String valu = getEnvironment().getProperty(requireNonNull(name));

        if (null != valu)
        {
            return valu;
        }
        return getCorePropertiesResolver().getPropertyByName(name, otherwise);
    }

    @Override
    public String getPropertyByName(final String name, final Supplier<String> otherwise)
    {
        final String valu = getEnvironment().getProperty(requireNonNull(name));

        if (null != valu)
        {
            return valu;
        }
        return getCorePropertiesResolver().getPropertyByName(name, otherwise);
    }

    @Override
    public final IAuthorizationProvider getAuthorizationProvider()
    {
        final IAuthorizationProvider auth = getBeanSafely("AuthorizationProvider", IAuthorizationProvider.class);

        if (null != auth)
        {
            return auth;
        }
        logger().trace("Using AuthorizationProvider default " + DEFAULT_AUTH.getClass().getName());

        return DEFAULT_AUTH;
    }

    @Override
    public final List<String> getPrincipalsKeys()
    {
        final IPrincipalsKeysProvider keys = getBeanSafely("PrincipalsKeysProvider", IPrincipalsKeysProvider.class);

        if (null != keys)
        {
            return keys.getPrincipalsKeys();
        }
        logger().trace("Using PrincipalsKeysProvider default " + DEFAULT_KEYS.getClass().getName());

        return DEFAULT_KEYS.getPrincipalsKeys();
    }

    @Override
    public final IMailSenderProvider getMailSenderProvider()
    {
        return requireNonNull(getBeanSafely("MailSenderProvider", IMailSenderProvider.class), "MailSenderProvider is null, initialization error.");
    }

    @Override
    public final IMailSender getMailSender(final String name)
    {
        return getMailSenderProvider().getItem(requireNonNull(name));
    }

    @Override
    public final IFileItemStorageProvider getFileItemStorageProvider()
    {
        return requireNonNull(getBeanSafely("FileItemStorageProvider", IFileItemStorageProvider.class), "FileItemStorageProvider is null, initialization error.");
    }

    @Override
    public IFileItemStorage getFileItemStorage(final String name)
    {
        return getFileItemStorageProvider().getItem(requireNonNull(name));
    }

    @Override
    public final IServerSessionRepositoryProvider getServerSessionRepositoryProvider()
    {
        return requireNonNull(getBeanSafely("ServerSessionRepositoryProvider", IServerSessionRepositoryProvider.class), "ServerSessionRepositoryProvider is null, initialization error.");
    }

    @Override
    public final IServerSessionRepository getServerSessionRepository(final String domain)
    {
        return getServerSessionRepositoryProvider().getServerSessionRepository(requireNonNull(domain));
    }

    @Override
    public final IBuildDescriptorProvider getBuildDescriptorProvider()
    {
        return requireNonNull(getBeanSafely("BuildDescriptorProvider", IBuildDescriptorProvider.class), "BuildDescriptorProvider is null, initialization error.");
    }

    @Override
    public IServletContextCustomizerProvider getServletContextCustomizerProvider()
    {
        return requireNonNull(getBeanSafely("ServletContextCustomizerProvider", IServletContextCustomizerProvider.class), "ServletContextCustomizerProvider is null, initialization error.");
    }

    @Override
    public final ICryptoProvider getCryptoProvider()
    {
        return requireNonNull(getBeanSafely("CryptoProvider", ICryptoProvider.class), "CryptoProvider is null, initialization error.");
    }

    @Override
    public final ISignatoryProvider getSignatoryProvider()
    {
        return requireNonNull(getBeanSafely("SignatoryProvider", ISignatoryProvider.class), "SignatoryProvider is null, initialization error.");
    }

    @Override
    public final ICoreNetworkProvider network()
    {
        return requireNonNull(getBeanSafely("NetworkProvider", ICoreNetworkProvider.class), "NetworkProvider is null, initialization error.");
    }

    private final CorePropertiesResolver getCorePropertiesResolver()
    {
        return requireNonNull(getBeanSafely("CorePropertiesResolver", CorePropertiesResolver.class), "CorePropertiesResolver is null, initialization error.");
    }

    @Override
    public final IAuthorizationResult isAuthorized(final Object target, final List<String> roles)
    {
        return getAuthorizationProvider().isAuthorized(target, roles);
    }

    @Override
    public final MessageChannel getMessageChannel(final String name)
    {
        MessageChannel channel = getBeanSafely(requireNonNull(name), MessageChannel.class);

        if (null != channel)
        {
            return channel;
        }
        channel = getSubscribableChannel(name);

        if (null != channel)
        {
            return channel;
        }
        return getPollableChannel(name);
    }

    @Override
    public final SubscribableChannel getSubscribableChannel(final String name)
    {
        return getBeanSafely(requireNonNull(name), SubscribableChannel.class);
    }

    @Override
    public final PollableChannel getPollableChannel(final String name)
    {
        return getBeanSafely(requireNonNull(name), PollableChannel.class);
    }

    @Override
    public final boolean publish(final String name, final JSONObject message)
    {
        return publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message)));
    }

    @Override
    public final boolean publish(final String name, final JSONObject message, final long timeout)
    {
        return publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message)), timeout);
    }

    @Override
    public final boolean publish(final String name, final JSONObject message, final Map<String, ?> headers)
    {
        return publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message), requireNonNull(headers)));
    }

    @Override
    public final boolean publish(final String name, final JSONObject message, final Map<String, ?> headers, final long timeout)
    {
        return publish(requireNonNull(name), JSONMessageBuilder.createMessage(requireNonNull(message), requireNonNull(headers)), timeout);
    }

    @Override
    public final <T> boolean publish(final String name, final Message<T> message)
    {
        final MessageChannel channel = getMessageChannel(requireNonNull(name));

        if (null != channel)
        {
            return channel.send(requireNonNull(message));
        }
        throw new IllegalArgumentException("MessageChannel " + name + " does not exist.");
    }

    @Override
    public final <T> boolean publish(final String name, final Message<T> message, final long timeout)
    {
        final MessageChannel channel = getMessageChannel(requireNonNull(name));

        if (null != channel)
        {
            return channel.send(requireNonNull(message), timeout);
        }
        throw new IllegalArgumentException("MessageChannel " + name + " does not exist.");
    }

    @Override
    public final String uuid()
    {
        return UUID.randomUUID().toString().toUpperCase();
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }

    @Override
    public final String toTrimOrNull(final String string)
    {
        return StringOps.toTrimOrNull(string);
    }

    @Override
    public final String toTrimOrElse(final String string, final String otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    @Override
    public final String toTrimOrElse(final String string, final Supplier<String> otherwise)
    {
        return StringOps.toTrimOrElse(string, otherwise);
    }

    @Override
    public final <T> T requireNonNull(final T object)
    {
        return CommonOps.requireNonNull(object);
    }

    @Override
    public final <T> T requireNonNull(final T object, final String message)
    {
        return CommonOps.requireNonNull(object, message);
    }

    @Override
    public final <T> T requireNonNull(final T object, final Supplier<String> message)
    {
        return CommonOps.requireNonNull(object, message);
    }

    @Override
    public <T> T requireNonNullOrElse(final T object, final T otherwise)
    {
        return CommonOps.requireNonNullOrElse(object, otherwise);
    }

    @Override
    public <T> T requireNonNullOrElse(final T object, final Supplier<T> otherwise)
    {
        return CommonOps.requireNonNullOrElse(object, otherwise);
    }

    @Override
    public final IScriptingProvider scripting()
    {
        return requireNonNull(getBeanSafely("ScriptingProvider", IScriptingProvider.class), "ScriptingProvider is null, initialization error.");
    }

    @Override
    public final Resource resource(final String location)
    {
        return getApplicationContext().getResource(requireNonNull(location));
    }

    @Override
    public final Reader reader(final String location) throws IOException
    {
        final Resource resource = resource(requireNonNull(location));

        if (null != resource)
        {
            return new InputStreamReader(resource.getInputStream(), IO.UTF_8_CHARSET);
        }
        return null;
    }

    @Override
    public final IWebSocketServiceProvider getWebSocketServiceProvider()
    {
        return requireNonNull(getBeanSafely("WebSocketServiceProvider", IWebSocketServiceProvider.class), "WebSocketServiceProvider is null, initialization error.");
    }

    @Override
    public final IWebSocketService getWebSocketService(final String name)
    {
        return getWebSocketServiceProvider().getWebSocketService(name);
    }

    @Override
    public final CacheManager getCacheManager(final String name)
    {
        return getBeanSafely(requireNonNull(name), CacheManager.class);
    }
}
