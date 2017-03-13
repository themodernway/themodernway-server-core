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
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.themodernway.server.core.servlet.IServletCommonOperations;

public abstract class HTTPFilterBase implements Filter, IServletCommonOperations
{
    public abstract void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;

    public abstract void doInit(FilterConfig fc) throws ServletException;

    private FilterConfig m_config = null;

    private Logger       m_logger = Logger.getLogger(getClass());

    protected HTTPFilterBase()
    {
    }

    @Override
    public String getName()
    {
        return getFilterConfig().getFilterName();
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public String getConfigurationParameter(final String name)
    {
        return getFilterConfig().getInitParameter(name);
    }

    @Override
    public List<String> getConfigurationParameterNames()
    {
        return Collections.list(getFilterConfig().getInitParameterNames());
    }

    public FilterConfig getFilterConfig()
    {
        return m_config;
    }

    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    @Override
    public final void init(final FilterConfig fc) throws ServletException
    {
        m_config = fc;

        doInit(m_config);
    }
}
