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
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.servlet.IServletCommonOperations;

public interface IHTTPFilter extends Filter, IServletCommonOperations
{
    public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;

    public FilterConfig getFilterConfig();

    public void setFilterConfig(FilterConfig fc);

    public default void initialize() throws ServletException
    {
    }

    public default boolean isOncePerRequest()
    {
        return false;
    }

    public default String getOncePerRequestAttribute()
    {
        return toTrimOrElse(getName(), getClass().getName()) + ALREADY_FILTERED_SUFFIX;
    }

    @Override
    public default String getName()
    {
        return getFilterConfig().getFilterName();
    }

    @Override
    public default String getConfigurationParameter(final String name)
    {
        return getFilterConfig().getInitParameter(name);
    }

    @Override
    public default List<String> getConfigurationParameterNames()
    {
        return CommonOps.toUnmodifiableList(CommonOps.toList(getFilterConfig().getInitParameterNames()));
    }

    @Override
    public default void destroy()
    {
    }

    @Override
    public default void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        if (isOncePerRequest())
        {
            final String attr = getOncePerRequestAttribute();

            if (null != request.getAttribute(attr))
            {
                chain.doFilter(request, response);
            }
            else
            {
                request.setAttribute(attr, Boolean.TRUE);

                try
                {
                    filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
                }
                finally
                {
                    request.removeAttribute(attr);
                }
            }
        }
        else
        {
            filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        }
    }

    @Override
    public default void init(final FilterConfig fc) throws ServletException
    {
        setFilterConfig(fc);

        initialize();

        doInitializeMaxContentTypeLength();
    }
}
