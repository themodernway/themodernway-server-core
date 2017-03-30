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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.servlet.filter.HeaderInjectorFilter;
import com.themodernway.server.core.servlet.filter.IHeaderInjector;

public class HeaderInjectorFilterContextCustomizer implements IServletContextCustomizer
{
    private static final Logger              logger      = Logger.getLogger(HeaderInjectorFilterContextCustomizer.class);

    private final String                     m_name;

    private final String[]                   m_maps;

    private boolean                          m_when      = true;

    private final ArrayList<IHeaderInjector> m_injectors = new ArrayList<IHeaderInjector>();

    protected HeaderInjectorFilterContextCustomizer(final String name, final String maps)
    {
        final String path = StringOps.requireTrimOrNull(maps);

        if (path.contains(","))
        {
            m_maps = StringOps.toUniqueArray(StringOps.tokenizeToStringCollection(path, ",", true, true));
        }
        else
        {
            m_maps = StringOps.toUniqueArray(path);
        }
        m_name = StringOps.requireTrimOrNull(name);
    }

    public HeaderInjectorFilterContextCustomizer(final String name, final String maps, final IHeaderInjector injector)
    {
        this(name, maps);

        if (null != injector)
        {
            m_injectors.add(injector);
        }
    }

    public HeaderInjectorFilterContextCustomizer(final String name, final Collection<String> maps, final IHeaderInjector injector)
    {
        this(name, StringOps.toCommaSeparated(maps));

        if (null != injector)
        {
            m_injectors.add(injector);
        }
    }

    public HeaderInjectorFilterContextCustomizer(final String name, final String maps, final List<IHeaderInjector> injectors)
    {
        this(name, maps);

        for (IHeaderInjector injector : injectors)
        {
            if (null != injector)
            {
                m_injectors.add(injector);
            }
        }
    }

    public HeaderInjectorFilterContextCustomizer(final String name, final Collection<String> maps, final List<IHeaderInjector> injectors)
    {
        this(name, StringOps.toCommaSeparated(maps));

        for (IHeaderInjector injector : injectors)
        {
            if (null != injector)
            {
                m_injectors.add(injector);
            }
        }
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
        return m_maps;
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
            final List<IHeaderInjector> list = Collections.unmodifiableList(m_injectors);

            if (false == list.isEmpty())
            {
                final String[] maps = getMappings();

                if ((null != maps) && (maps.length > 0))
                {
                    final Dynamic dispatcher = sc.addFilter(name, doMakeHeaderInjectorFilter(sc, context, list));

                    if (null != dispatcher)
                    {
                        dispatcher.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), isMatchAfter(), maps);

                        logger.info("customize(" + name + ",\"" + StringOps.toCommaSeparated(maps) + "\"): COMPLETE");
                    }
                    else
                    {
                        logger.error("customize(" + name + ",\"" + StringOps.toCommaSeparated(maps) + "\"): already registered.");
                    }
                }
                else
                {
                    logger.error("customize(" + name + "): empty mapping.");
                }
            }
            else
            {
                logger.error("customize(" + name + "): empty injectors.");
            }
        }
        else
        {
            logger.error("customize(): no name.");
        }
    }

    protected HeaderInjectorFilter doMakeHeaderInjectorFilter(final ServletContext sc, final WebApplicationContext context, final List<IHeaderInjector> injectors)
    {
        return new HeaderInjectorFilter(injectors);
    }
}
