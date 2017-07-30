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
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreCommon;

public abstract class AbstractFilterContextCustomizer implements IServletContextCustomizer, ICoreCommon
{
    private final Logger   m_logs = Logger.getLogger(getClass());

    private final String   m_name;

    private final String[] m_maps;

    private boolean        m_when = true;

    protected AbstractFilterContextCustomizer(final String name, final String maps)
    {
        this(name, StringOps.toUniqueTokenStringList(maps));
    }

    protected AbstractFilterContextCustomizer(final String name, final Collection<String> maps)
    {
        m_name = requireTrimOrNull(name);

        m_maps = StringOps.toUniqueArray(maps);
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
    }

    @Override
    public void customize(final ServletContext sc, final WebApplicationContext context)
    {
        final String name = StringOps.toTrimOrNull(getFilterName());

        if (null != name)
        {
            final String[] maps = getMappings();

            if ((null != maps) && (maps.length > 0))
            {
                final Filter filter = doMakeFilter(sc, context);

                if (null != filter)
                {
                    final Dynamic dispatcher = sc.addFilter(name, filter);

                    if (null != dispatcher)
                    {
                        dispatcher.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), isMatchAfter(), maps);

                        logger().info(String.format("customize (%s) mapped to (%s).", name, StringOps.toCommaSeparated(maps)));
                    }
                    else
                    {
                        logger().error(String.format("customize (%s) already registered.", name));
                    }
                }
                else
                {
                    logger().error(String.format("customize (%s) null filter.", name));
                }
            }
            else
            {
                logger().error(String.format("customize (%s) empty mappings.", name));
            }
        }
        else
        {
            logger().error("customize() no filter name.");
        }
    }

    protected abstract Filter doMakeFilter(ServletContext sc, WebApplicationContext context);
}
