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

package com.themodernway.server.core.support.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.server.core.file.vfs.IFileItemStorage;
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.support.CoreJSONOperations;
import com.themodernway.server.core.logging.LoggingOps;
import com.themodernway.server.core.mail.IMailSender;
import com.themodernway.server.core.mail.IMailSenderProvider;
import com.themodernway.server.core.scripting.IScriptingProvider;
import com.themodernway.server.core.security.DefaultAuthorizationProvider;
import com.themodernway.server.core.security.IAuthorizationProvider;
import com.themodernway.server.core.security.IAuthorizationResult;
import com.themodernway.server.core.security.ICryptoProvider;
import com.themodernway.server.core.security.session.IServerSessionRepository;
import com.themodernway.server.core.security.session.IServerSessionRepositoryProvider;
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider;

public class ServerContextInstance extends CoreJSONOperations implements IServerContext
{
    private static ApplicationContext                APPCONTEXT   = null;

    public static final DefaultAuthorizationProvider DEFAULT_AUTH = new DefaultAuthorizationProvider();

    public static final ServerContextInstance        INSTANCE     = new ServerContextInstance();

    private final Logger                             m_logger     = LoggingOps.getLogger(getClass());

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
    public final <B> B getBean(final String name, final Class<B> type) throws BeansException
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
                    if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("getBeanSafely(%s,%s) error.", name, type.getName()), e);
                    }
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
                        if (logger().isErrorEnabled())
                        {
                            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("getBeanSafely(%s,%s) error.", name, type.getName()), e);
                        }
                    }
                }
            }
        }
        catch (final Exception e)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("getBeanSafely(%s,%s) error.", name, type.getName()), e);
            }
        }
        return bean;
    }

    @Override
    public final <B> Map<String, B> getBeansOfType(final Class<B> type) throws BeansException
    {
        return toUnmodifiableMap(getApplicationContext().getBeansOfType(requireNonNull(type)));
    }

    @Override
    public final String getOriginalBeanName(final String name)
    {
        return toTrimOrNull(BeanFactoryUtils.originalBeanName(requireNonNull(name)));
    }

    @Override
    public final IPropertiesResolver getPropertiesResolver()
    {
        return this;
    }

    private final CorePropertiesResolver getCorePropertiesResolver()
    {
        return requireNonNull(getBeanSafely("CorePropertiesResolver", CorePropertiesResolver.class), "CorePropertiesResolver is null, initialization error.");
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
    public final String getPropertyByName(final String name, final Supplier<String> otherwise)
    {
        final String valu = getEnvironment().getProperty(requireNonNull(name));

        if (null != valu)
        {
            return valu;
        }
        return getCorePropertiesResolver().getPropertyByName(name, otherwise);
    }

    @Override
    public final String getResolvedExpression(final String expr)
    {
        return getCorePropertiesResolver().getResolvedExpression(requireNonNull(expr));
    }

    @Override
    public final String getResolvedExpression(final String expr, final String otherwise)
    {
        return getCorePropertiesResolver().getResolvedExpression(requireNonNull(expr), otherwise);
    }

    @Override
    public final String getResolvedExpression(final String expr, final Supplier<String> otherwise)
    {
        return getCorePropertiesResolver().getResolvedExpression(requireNonNull(expr), otherwise);
    }

    @Override
    public final IAuthorizationProvider getAuthorizationProvider()
    {
        final IAuthorizationProvider auth = getBeanSafely("AuthorizationProvider", IAuthorizationProvider.class);

        if (null != auth)
        {
            return auth;
        }
        if (logger().isDebugEnabled())
        {
            logger().debug(LoggingOps.THE_MODERN_WAY_MARKER, format("Using AuthorizationProvider default (%s).", DEFAULT_AUTH.getClass().getName()));
        }
        return DEFAULT_AUTH;
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
    public final IFileItemStorage getFileItemStorage(final String name)
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
    public final IServletContextCustomizerProvider getServletContextCustomizerProvider()
    {
        return requireNonNull(getBeanSafely("ServletContextCustomizerProvider", IServletContextCustomizerProvider.class), "ServletContextCustomizerProvider is null, initialization error.");
    }

    @Override
    public final ICryptoProvider getCryptoProvider()
    {
        return requireNonNull(getBeanSafely("CryptoProvider", ICryptoProvider.class), "CryptoProvider is null, initialization error.");
    }

    @Override
    public final ICoreNetworkProvider network()
    {
        return requireNonNull(getBeanSafely("NetworkProvider", ICoreNetworkProvider.class), "NetworkProvider is null, initialization error.");
    }

    @Override
    public final IAuthorizationResult isAuthorized(final Object target, final List<String> roles)
    {
        return getAuthorizationProvider().isAuthorized(target, roles);
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

        if (isNonNull(resource))
        {
            return new InputStreamReader(resource.getInputStream(), IO.UTF_8_CHARSET);
        }
        return null;
    }

    @Override
    public final CacheManager getCacheManager(final String name)
    {
        return getBeanSafely(requireNonNull(name), CacheManager.class);
    }
}
