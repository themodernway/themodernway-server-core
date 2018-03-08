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
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.logging.LoggingOps;

public class FilterFactoryContextCustomizer implements IServletContextCustomizer, ICoreCommon
{
    private final Logger   m_logs = LoggingOps.getLogger(getClass());

    private final String   m_name;

    private final String[] m_maps;

    private boolean        m_when = true;

    private IFilterFactory m_fact;

    public FilterFactoryContextCustomizer(final String name, final String maps)
    {
        this(name, StringOps.toUniqueTokenStringList(maps));
    }

    public FilterFactoryContextCustomizer(final String name, final Collection<String> maps)
    {
        m_maps = StringOps.toUniqueArray(maps);

        m_name = StringOps.requireTrimOrNull(name);
    }

    public void setFilterFactory(final IFilterFactory fact)
    {
        m_fact = CommonOps.requireNonNull(fact);
    }

    @Override
    public Logger logger()
    {
        return m_logs;
    }

    public void setMatchAfter(final boolean when)
    {
        m_when = when;
    }

    public boolean isMatchAfter()
    {
        return m_when;
    }

    public String getFilterName()
    {
        return m_name;
    }

    public String[] getMappings()
    {
        return StringOps.toUniqueArray(m_maps);
    }

    @Override
    public void close() throws IOException
    {
        // empty by design.
    }

    @Override
    public void customize(final ServletContext sc, final WebApplicationContext context)
    {
        if (null != m_fact)
        {
            final String name = StringOps.toTrimOrNull(getFilterName());

            if (null != name)
            {
                final String[] maps = getMappings();

                if ((null != maps) && (maps.length > 0))
                {
                    final Filter filter = m_fact.make(this, sc, context);

                    if (null != filter)
                    {
                        final Dynamic dispatcher = sc.addFilter(name, filter);

                        if (null != dispatcher)
                        {
                            dispatcher.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), isMatchAfter(), maps);

                            if (logger().isErrorEnabled())
                            {
                                logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("customize (%s) mapped to (%s).", name, StringOps.toCommaSeparated(maps)));
                            }
                        }
                        else if (logger().isErrorEnabled())
                        {
                            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("customize (%s) already registered.", name));
                        }
                    }
                    else if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("customize (%s) null filter.", name));
                    }
                }
                else if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("customize (%s) empty mappings.", name));
                }
            }
            else if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "customize() no filter name.");
            }
        }
        else if (logger().isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "customize() no filter factory.");
        }
    }
}
