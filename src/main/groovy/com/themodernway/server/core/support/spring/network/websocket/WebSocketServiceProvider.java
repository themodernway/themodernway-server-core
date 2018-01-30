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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.INamedType;
import com.themodernway.server.core.logging.LoggingOps;

public class WebSocketServiceProvider implements IWebSocketServiceProvider, BeanFactoryAware
{
    private static final Logger                                   logger     = LoggingOps.LOGGER(WebSocketServiceProvider.class);

    private final LinkedHashMap<String, IWebSocketServiceFactory> m_services = new LinkedHashMap<String, IWebSocketServiceFactory>();

    private final LinkedHashMap<String, IWebSocketServiceSession> m_sessions = new LinkedHashMap<String, IWebSocketServiceSession>();

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
        for (final IWebSocketServiceSession sock : m_sessions.values())
        {
            if (null != sock)
            {
                try
                {
                    sock.close();
                }
                catch (final Exception e)
                {
                    logger.error(sock.getId(), e);
                }
            }
        }
    }

    @Override
    public boolean addWebSocketServiceSession(final IWebSocketServiceSession session)
    {
        final String iden = session.getId();

        if (null == m_sessions.get(iden))
        {
            m_sessions.put(iden, session);

            return true;
        }
        return false;
    }

    @Override
    public boolean removeWebSocketServiceSession(final IWebSocketServiceSession session)
    {
        final String iden = session.getId();

        if (null != m_sessions.get(iden))
        {
            m_sessions.remove(iden);

            return true;
        }
        return false;
    }

    @Override
    public IWebSocketServiceSession getWebSocketServiceSession(final String iden)
    {
        return m_sessions.get(iden);
    }

    @Override
    public List<IWebSocketServiceSession> getWebSocketServiceSessions()
    {
        return CommonOps.toUnmodifiableList(m_sessions.values());
    }

    @Override
    public List<IWebSocketServiceSession> findSessions(final Predicate<IWebSocketServiceSession> predicate)
    {
        return CommonOps.toUnmodifiableList(getWebSocketServiceSessions().stream().filter(predicate).collect(Collectors.toList()));
    }

    @Override
    public List<IWebSocketServiceSession> findSessionsByIdentifiers(final Collection<String> want)
    {
        return findSessions(session -> want.contains(session.getId()));
    }

    @Override
    public List<IWebSocketServiceSession> findSessionsByServiceNames(final Collection<String> want)
    {
        return findSessions(session -> want.contains(session.getService().getName()));
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
        private final Map<String, String> m_want;

        private final boolean             m_some;

        public PathPredicate(final Map<String, String> want, final boolean some)
        {
            m_want = want;

            m_some = some;
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
