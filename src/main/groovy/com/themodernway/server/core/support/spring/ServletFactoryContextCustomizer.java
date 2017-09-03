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

package com.themodernway.server.core.support.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreCommon;

public class ServletFactoryContextCustomizer implements IServletContextCustomizer, ICoreCommon
{
    private final Logger   m_logs = Logger.getLogger(getClass());

    private final String   m_name;

    private final String[] m_maps;

    private int            m_load = 1;

    private double         m_rate = 0.0;

    private List<String>   m_role = arrayList();

    private IServletFactory m_fact;

    public ServletFactoryContextCustomizer(final String name, final String maps)
    {
        this(name, StringOps.toUniqueTokenStringList(maps));
    }

    public ServletFactoryContextCustomizer(final String name, final Collection<String> maps)
    {
        m_name = requireTrimOrNull(name);

        m_maps = StringOps.toUniqueArray(maps);
    }

    public void setServletFactory(final IServletFactory fact)
    {
        m_fact = Objects.requireNonNull(fact);
    }

    public void setRateLinit(final double rate)
    {
        m_rate = rate;
    }

    public double getRateLimit()
    {
        return m_rate;
    }

    @Override
    public Logger logger()
    {
        return m_logs;
    }

    public void setLoadOnStartup(final int load)
    {
        m_load = load;
    }

    public int getLoadOnStartup()
    {
        return m_load;
    }

    public String getServletName()
    {
        return m_name;
    }

    public String[] getMappings()
    {
        return StringOps.toUniqueArray(m_maps);
    }

    public List<String> getRequiredRoles()
    {
        return toUnmodifiableList(m_role);
    }

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

    public void setRequiredRoles(final List<String> roles)
    {
        m_role = (roles == null ? arrayList() : roles);
    }

    @Override
    public void close() throws IOException
    {
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
                final Servlet servlet = m_fact.make(sc, context);

                if (null != servlet)
                {
                    final Dynamic dispatcher = sc.addServlet(name, servlet);

                    if (null != dispatcher)
                    {
                        final Collection<String> done = dispatcher.addMapping(maps);

                        if (false == done.isEmpty())
                        {
                            logger().warn(format("customize (%s) already mapped (%s).", name, StringOps.toCommaSeparated(done)));
                        }
                        dispatcher.setLoadOnStartup(getLoadOnStartup());

                        logger().info(format("customize (%s) mapped to (%s).", name, StringOps.toCommaSeparated(maps)));
                    }
                    else
                    {
                        logger().error(format("customize (%s) already registered.", name));
                    }
                }
                else
                {
                    logger().error(format("customize (%s) null servlet.", name));
                }
            }
            else
            {
                logger().error(format("customize (%s) empty mappings.", name));
            }
        }
        else
        {
            logger().error("customize() no servlet name.");
        }
    }
}
