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

package com.themodernway.server.core.security.session;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.server.core.ICoreBase;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.logging.LoggingOps;

public class ServerSessionRepositoryProvider implements IServerSessionRepositoryProvider, ICoreBase, BeanFactoryAware
{
    private static final Logger                                   logger         = LoggingOps.getLogger(ServerSessionRepositoryProvider.class);

    private final LinkedHashMap<String, IServerSessionRepository> m_repositories = linkedMap();

    public ServerSessionRepositoryProvider()
    {
        if (logger.isInfoEnabled())
        {
            logger.info(LoggingOps.THE_MODERN_WAY_MARKER, format("ServerSessionRepositoryProvider()"));
        }
    }

    protected void addSessionRepository(final IServerSessionRepository repository)
    {
        if (null != repository)
        {
            final String realm = toTrimOrNull(repository.getRealm());

            if (null != realm)
            {
                if (null == m_repositories.get(realm))
                {
                    m_repositories.put(realm, repository);

                    if (logger.isInfoEnabled())
                    {
                        logger.info(LoggingOps.THE_MODERN_WAY_MARKER, format("ServerSessionRepositoryProvider.addSessionRepository(%s) Registered", realm));
                    }
                }
                else if (logger.isErrorEnabled())
                {
                    logger.error(LoggingOps.THE_MODERN_WAY_MARKER, format("ServerSessionRepositoryProvider.addSessionRepository(%s) Duplicate ignored", realm));
                }
            }
            else if (logger.isErrorEnabled())
            {
                logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "ServerSessionRepositoryProvider.addSessionRepository() null realm name");
            }
        }
        else if (logger.isErrorEnabled())
        {
            logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "ServerSessionRepositoryProvider.addSessionRepository() null repository");
        }
    }

    @Override
    public void close() throws IOException
    {
        IO.close(m_repositories.values());
    }

    @Override
    public List<String> getServerSessionRepositoryRealms()
    {
        return toUnmodifiableList(m_repositories.keySet());
    }

    @Override
    public IServerSessionRepository getServerSessionRepository(final String domain)
    {
        return m_repositories.get(requireTrimOrNull(domain));
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            for (final IServerSessionRepository repository : ((DefaultListableBeanFactory) factory).getBeansOfType(IServerSessionRepository.class).values())
            {
                addSessionRepository(repository);
            }
        }
    }

    @Override
    public void cleanExpiredSessions()
    {
        for (final IServerSessionRepository repository : m_repositories.values())
        {
            if (null != repository)
            {
                try
                {
                    repository.cleanExpiredSessions();
                }
                catch (final Exception e)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "ServerSessionRepositoryProvider.cleanExpiredSessions() error.", e);
                    }
                }
            }
        }
    }
}
