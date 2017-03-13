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

package com.themodernway.server.core.servlet.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.io.IO;

public class HeaderInjectorFilter extends HTTPFilterBase
{
    private final ArrayList<IHeaderInjector> m_injectors = new ArrayList<IHeaderInjector>();

    public HeaderInjectorFilter()
    {
    }

    public HeaderInjectorFilter(final List<IHeaderInjector> injectors)
    {
        addHeaderInjectors(injectors);
    }

    public HeaderInjectorFilter(final IHeaderInjector... injectors)
    {
        addHeaderInjectors(injectors);
    }

    public final void addHeaderInjector(final IHeaderInjector injector)
    {
        if (null != injector)
        {
            if (m_injectors.contains(injector))
            {
                m_injectors.remove(injector);
            }
            m_injectors.add(injector);

            logger().info("HeaderInjectorFilter.addHeaderInjector(" + injector.getClass().getName() + ")");
        }
    }

    public final void setHeaderInjectors(final List<IHeaderInjector> injectors)
    {
        m_injectors.clear();

        addHeaderInjectors(injectors);
    }

    public final void addHeaderInjectors(final List<IHeaderInjector> injectors)
    {
        if (null != injectors)
        {
            for (IHeaderInjector injector : injectors)
            {
                addHeaderInjector(injector);
            }
        }
    }

    public final void setHeaderInjectors(final IHeaderInjector... injectors)
    {
        m_injectors.clear();

        addHeaderInjectors(injectors);
    }

    public final void addHeaderInjectors(final IHeaderInjector... injectors)
    {
        if (null != injectors)
        {
            for (IHeaderInjector injector : injectors)
            {
                addHeaderInjector(injector);
            }
        }
    }

    public final List<IHeaderInjector> getInjectors()
    {
        return Collections.unmodifiableList(m_injectors);
    }

    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        for (IHeaderInjector injector : getInjectors())
        {
            if (null != injector)
            {
                try
                {
                    final int code = injector.inject(request, response);

                    if (code != HttpServletResponse.SC_OK)
                    {
                        response.setStatus(code);

                        return;
                    }
                }
                catch (Throwable t)
                {
                    logger().error("Could not inject headers " + injector.getClass().getName(), t);

                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void doInit(final FilterConfig fc) throws ServletException
    {
        try
        {
            final String name = StringOps.toTrimOrNull(fc.getInitParameter("config"));

            if (null != name)
            {
                final InputStream in = fc.getServletContext().getResourceAsStream(name);

                try
                {
                    final HeaderInjectorParser parser = new HeaderInjectorParser();

                    parser.parse(in);

                    addHeaderInjectors(parser.getInjectors());
                }
                catch (Throwable t)
                {
                    logger().error("Could not create injectors", t);
                }
                finally
                {
                    IO.close(in);
                }
            }
        }
        catch (Throwable t)
        {
            logger().error("Could not create injectors", t);
        }
    }
}
