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
import java.util.Collection;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

import org.slf4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.logging.LoggingOps;
import com.themodernway.server.core.servlet.IServletResponseErrorCodeManager;
import com.themodernway.server.core.servlet.ISessionIDFromRequestExtractor;

public class ServletFactoryContextCustomizer implements IServletContextCustomizer, ICoreCommon, IServletFactoryContextCustomizer
{
    private final Logger                     m_logs = LoggingOps.getLogger(getClass());

    private final String                     m_name;

    private final String[]                   m_maps;

    private int                              m_load = 1;

    private double                           m_rate = 0.0;

    private List<String>                     m_role = arrayList();

    private IServletFactory                  m_fact;

    private ISessionIDFromRequestExtractor   m_extr;

    private IServletResponseErrorCodeManager m_code;

    public ServletFactoryContextCustomizer(final String name, final String maps)
    {
        this(name, StringOps.toUniqueTokenStringList(maps));
    }

    public ServletFactoryContextCustomizer(final String name, final Collection<String> maps)
    {
        m_name = requireTrimOrNull(name);

        m_maps = StringOps.toUniqueArray(maps);
    }

    @Override
    public void setServletFactory(final IServletFactory fact)
    {
        m_fact = CommonOps.requireNonNull(fact);
    }

    @Override
    public void setRateLimit(final double rate)
    {
        m_rate = rate;
    }

    @Override
    public double getRateLimit()
    {
        return m_rate;
    }

    @Override
    public Logger logger()
    {
        return m_logs;
    }

    @Override
    public void setLoadOnStartup(final int load)
    {
        m_load = load;
    }

    @Override
    public int getLoadOnStartup()
    {
        return m_load;
    }

    @Override
    public String getServletName()
    {
        return m_name;
    }

    @Override
    public String[] getMappings()
    {
        return StringOps.toUniqueArray(m_maps);
    }

    @Override
    public List<String> getRequiredRoles()
    {
        return toUnmodifiableList(m_role);
    }

    @Override
    public void setRequiredRoles(String roles)
    {
        if (null == (roles = toTrimOrNull(roles)))
        {
            setRequiredRoles(arrayList());
        }
        else
        {
            setRequiredRoles(toUniqueTokenStringList(roles));
        }
    }

    @Override
    public void setRequiredRoles(final List<String> roles)
    {
        m_role = (roles == null ? arrayList() : roles);
    }

    @Override
    public void close() throws IOException
    {
        // empty by design.
    }

    @Override
    public ISessionIDFromRequestExtractor getSessionIDFromRequestExtractor()
    {
        return m_extr;
    }

    @Override
    public void setSessionIDFromRequestExtractor(final ISessionIDFromRequestExtractor extractor)
    {
        m_extr = extractor;
    }

    @Override
    public void setServletResponseErrorCodeManager(final IServletResponseErrorCodeManager manager)
    {
        m_code = requireNonNull(manager);
    }

    @Override
    public IServletResponseErrorCodeManager getServletResponseErrorCodeManager()
    {
        return m_code;
    }

    @Override
    public void customize(final ServletContext sc, final WebApplicationContext context)
    {
        final String name = toTrimOrNull(getServletName());

        if (null != name)
        {
            final String[] maps = getMappings();

            if ((null != maps) && (maps.length > 0))
            {
                final Servlet servlet = m_fact.make(this, sc, context);

                if (null != servlet)
                {
                    final Dynamic dispatcher = sc.addServlet(name, servlet);

                    if (null != dispatcher)
                    {
                        final Collection<String> done = dispatcher.addMapping(maps);

                        if ((false == done.isEmpty()) && (logger().isWarnEnabled()))
                        {
                            logger().warn(LoggingOps.THE_MODERN_WAY_MARKER, format("customize (%s) already mapped (%s).", name, StringOps.toCommaSeparated(done)));
                        }
                        dispatcher.setLoadOnStartup(getLoadOnStartup());

                        if (logger().isInfoEnabled())
                        {
                            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("customize (%s) mapped to (%s).", name, StringOps.toCommaSeparated(maps)));
                        }
                    }
                    else if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("customize (%s) already registered.", name));
                    }
                }
                else if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("customize (%s) null servlet.", name));
                }
            }
            else if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("customize (%s) empty mappings.", name));
            }
        }
        else if (logger().isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "customize() no servlet name.");
        }
    }
}
