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

package com.themodernway.server.core.servlet.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.logging.LoggingOps;

public class HeaderInjectorFilter extends HTTPFilterBase implements IHeaderInjectorFilter
{
    private final ArrayList<IHeaderInjector> m_injectors = new ArrayList<>();

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

    @Override
    public void destroy()
    {
        for (final IHeaderInjector injector : getHeaderInjectors())
        {
            if (null != injector)
            {
                injector.destroy();
            }
        }
        m_injectors.clear();
    }

    @Override
    public final void addHeaderInjector(final IHeaderInjector injector)
    {
        if (null != injector)
        {
            if (m_injectors.contains(injector))
            {
                m_injectors.remove(injector);
            }
            m_injectors.add(injector);

            configure(injector);

            if (logger().isInfoEnabled())
            {
                logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "HeaderInjectorFilter.addHeaderInjector(" + injector.getName() + ")");
            }
        }
    }

    @Override
    public final void setHeaderInjectors(final List<IHeaderInjector> injectors)
    {
        m_injectors.clear();

        addHeaderInjectors(injectors);
    }

    @Override
    public final void addHeaderInjectors(final List<IHeaderInjector> injectors)
    {
        if (null != injectors)
        {
            for (final IHeaderInjector injector : injectors)
            {
                addHeaderInjector(injector);
            }
        }
    }

    @Override
    public final void setHeaderInjectors(final IHeaderInjector... injectors)
    {
        m_injectors.clear();

        addHeaderInjectors(injectors);
    }

    @Override
    public final void addHeaderInjectors(final IHeaderInjector... injectors)
    {
        if (null != injectors)
        {
            for (final IHeaderInjector injector : injectors)
            {
                addHeaderInjector(injector);
            }
        }
    }

    @Override
    public final List<IHeaderInjector> getHeaderInjectors()
    {
        return CommonOps.toUnmodifiableList(m_injectors);
    }

    @Override
    public void initialize() throws ServletException
    {
        for (final IHeaderInjector injector : getHeaderInjectors())
        {
            if (null != injector)
            {
                configure(injector);
            }
        }
    }

    @Override
    public void configure(final IHeaderInjector injector)
    {
        if (null == injector.getHeaderInjectorFilter())
        {
            injector.setHeaderInjectorFilter(this);
        }
        if (null != getFilterConfig())
        {
            injector.config(new JSONObject(getConfigurationParameters()));
        }
    }

    @Override
    public void filter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        for (final IHeaderInjector injector : getHeaderInjectors())
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
                catch (final Exception e)
                {
                    if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Could not inject headers " + injector.getName(), e);
                    }
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }
}
