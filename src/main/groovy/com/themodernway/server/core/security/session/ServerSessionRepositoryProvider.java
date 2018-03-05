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

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.Activatable;
import com.themodernway.server.core.logging.LoggingOps;

public class ServerSessionRepositoryProvider extends Activatable implements IServerSessionRepositoryProvider, BeanFactoryAware
{
    private static final Logger                                   logger         = LoggingOps.LOGGER(ServerSessionRepositoryProvider.class);

    private final LinkedHashMap<String, IServerSessionRepository> m_repositories = new LinkedHashMap<>();

    public ServerSessionRepositoryProvider()
    {
        super(true);
    }

    protected void addSessionRepository(final IServerSessionRepository repository)
    {
        if (null != repository)
        {
            final String domain = StringOps.toTrimOrNull(repository.getDomain());

            if (null != domain)
            {
                if (null == m_repositories.get(domain))
                {
                    m_repositories.put(domain, repository);

                    repository.setActive(true);

                    logger.info("ServerSessionRepositoryProvider.addSessionRepository(" + domain + ") Registered");
                }
                else
                {
                    logger.error("ServerSessionRepositoryProvider.addSessionRepository(" + domain + ") Duplicate ignored");
                }
            }
            else
            {
                logger.error("ServerSessionRepositoryProvider.addSessionRepository() null domain name");
            }
        }
        else
        {
            logger.error("ServerSessionRepositoryProvider.addSessionRepository() null repository");
        }
    }

    @Override
    public void close() throws IOException
    {
        setActive(false);

        for (final IServerSessionRepository repository : m_repositories.values())
        {
            if (null != repository)
            {
                try
                {
                    repository.close();
                }
                catch (final Exception e)
                {
                    logger.error("ServerSessionRepositoryProvider.close() error.", e);
                }
            }
        }
    }

    @Override
    public List<String> getServerSessionRepositoryDomains()
    {
        return CommonOps.toUnmodifiableList(m_repositories.keySet());
    }

    @Override
    public IServerSessionRepository getServerSessionRepository(final String domain)
    {
        return m_repositories.get(StringOps.requireTrimOrNull(domain));
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
                    logger.error("ServerSessionRepositoryProvider.cleanExpiredSessions() error.", e);
                }
            }
        }
    }
}
