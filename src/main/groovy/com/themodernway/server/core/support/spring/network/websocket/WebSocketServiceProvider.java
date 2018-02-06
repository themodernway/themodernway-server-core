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

package com.themodernway.server.core.support.spring.network.websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.INamedType;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.logging.LoggingOps;

public class WebSocketServiceProvider implements IWebSocketServiceProvider, BeanFactoryAware
{
    private static final Logger                                       logger     = LoggingOps.LOGGER(WebSocketServiceProvider.class);

    private final LinkedHashMap<String, IWebSocketServiceFactory>     m_services = new LinkedHashMap<String, IWebSocketServiceFactory>();

    private final ConcurrentHashMap<String, IWebSocketServiceSession> m_sessions = new ConcurrentHashMap<String, IWebSocketServiceSession>();

    public WebSocketServiceProvider()
    {
    }

    protected void addWebSocketServiceFactory(final String name, final IWebSocketServiceFactory service)
    {
        if ((null != name) && (null != service))
        {
            if (null == m_services.get(name))
            {
                m_services.put(name, service);

                logger.info("WebSocketServiceProvider.addWebSocketServiceFactory(" + name + ") Registered");
            }
            else
            {
                logger.error("WebSocketServiceProvider.addWebSocketServiceFactory(" + name + ") Duplicate ignored");
            }
        }
    }

    @Override
    public IWebSocketService getWebSocketService(final String name)
    {
        final IWebSocketServiceFactory fact = m_services.get(name);

        if (null != fact)
        {
            return fact.get();
        }
        return null;
    }

    @Override
    public List<String> getWebSocketServiceNames()
    {
        return CommonOps.toUnmodifiableList(m_services.keySet());
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            final DefaultListableBeanFactory listable = ((DefaultListableBeanFactory) factory);

            for (final String name : listable.getBeanNamesForType(IWebSocketService.class))
            {
                final BeanDefinition defn = listable.getBeanDefinition(name);

                if (false == defn.isAbstract())
                {
                    if (defn.isPrototype())
                    {
                        addWebSocketServiceFactory(name, new IWebSocketServiceFactory()
                        {
                            @Override
                            public IWebSocketService get()
                            {
                                final IWebSocketService service = listable.getBean(name, IWebSocketService.class);

                                if (service instanceof INamedType)
                                {
                                    ((INamedType) service).setName(name);
                                }
                                return service;
                            }

                            @Override
                            public boolean isPrototype()
                            {
                                return true;
                            }
                        });
                    }
                    else
                    {
                        final IWebSocketService service = listable.getBean(name, IWebSocketService.class);

                        if (service instanceof INamedType)
                        {
                            ((INamedType) service).setName(name);
                        }
                        addWebSocketServiceFactory(name, new IWebSocketServiceFactory()
                        {
                            @Override
                            public IWebSocketService get()
                            {
                                return service;
                            }

                            @Override
                            public boolean isPrototype()
                            {
                                return false;
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void close() throws IOException
    {
        IO.close(m_sessions.values());
    }

    @Override
    public boolean addWebSocketServiceSession(final IWebSocketServiceSession sess)
    {
        return CommonOps.isNull(m_sessions.putIfAbsent(sess.getId(), sess));
    }

    @Override
    public boolean removeWebSocketServiceSession(final IWebSocketServiceSession sess)
    {
        return m_sessions.remove(sess.getId(), sess);
    }

    @Override
    public IWebSocketServiceSession getWebSocketServiceSession(final String iden)
    {
        return m_sessions.get(CommonOps.requireNonNull(iden));
    }

    @Override
    public List<IWebSocketServiceSession> getWebSocketServiceSessions()
    {
        return CommonOps.toUnmodifiableList(m_sessions.values());
    }

    @Override
    public List<IWebSocketServiceSession> findSessions(final Predicate<IWebSocketServiceSession> pred)
    {
        CommonOps.requireNonNull(pred);

        return CommonOps.toUnmodifiableList(m_sessions.values().stream().filter(pred));
    }

    @Override
    public List<IWebSocketServiceSession> findSessionsById(final Collection<String> want)
    {
        final LinkedHashSet<String> look = CommonOps.linkedSet(want);

        if (look.isEmpty())
        {
            return CommonOps.emptyList();
        }
        return findSessions(session -> look.contains(session.getId()));
    }

    @Override
    public List<IWebSocketServiceSession> findSessionsByServiceName(final Collection<String> want)
    {
        final LinkedHashSet<String> look = CommonOps.linkedSet(want);

        if (look.isEmpty())
        {
            return CommonOps.emptyList();
        }
        return findSessions(session -> look.contains(session.getService().getName()));
    }

    @Override
    public List<IWebSocketServiceSession> findSessionsByPathParameters(final Map<String, String> want)
    {
        return findSessionsByPathParameters(want, false);
    }

    @Override
    public List<IWebSocketServiceSession> findSessionsByPathParameters(final Map<String, String> want, final boolean some)
    {
        return findSessions(new PathPredicate(want, some));
    }

    private static class PathPredicate implements Predicate<IWebSocketServiceSession>
    {
        private final boolean             m_some;

        private final Map<String, String> m_want;

        public PathPredicate(final Map<String, String> want, final boolean some)
        {
            m_some = some;

            m_want = CommonOps.requireNonNull(want);
        }

        @Override
        public boolean test(final IWebSocketServiceSession session)
        {
            final Map<String, String> have = session.getPathParameters();

            boolean find = false;

            for (final Entry<String, String> each : m_want.entrySet())
            {
                final String look = have.get(each.getKey());

                if (look != null)
                {
                    if (look.equals(each.getValue()))
                    {
                        if (m_some)
                        {
                            return true;
                        }
                        find = true;
                    }
                    else if (false == m_some)
                    {
                        return false;
                    }
                }
                else if (false == m_some)
                {
                    return false;
                }
            }
            return find;
        }
    }

    protected interface IWebSocketServiceFactory extends Supplier<IWebSocketService>
    {
        public boolean isPrototype();
    }
}
