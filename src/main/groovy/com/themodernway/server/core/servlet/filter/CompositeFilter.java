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
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.CommonOps;

public class CompositeFilter extends HTTPFilterBase
{
    private final List<? extends Filter> m_filters;

    public CompositeFilter(final List<? extends Filter> filters)
    {
        m_filters = CommonOps.requireNonNull(filters);
    }

    @Override
    public void initialize() throws ServletException
    {
        for (final Filter filter : m_filters)
        {
            filter.init(getFilterConfig());
        }
    }

    @Override
    public void filter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        new CompositeFilterChain(chain, m_filters).doFilter(request, response);
    }

    @Override
    public void destroy()
    {
        for (final Filter filter : m_filters)
        {
            filter.destroy();
        }
    }

    private static final class CompositeFilterChain implements FilterChain
    {
        private final FilterChain            m_cochain;

        private final List<? extends Filter> m_filters;

        private int                          m_curposn = 0;

        public CompositeFilterChain(final FilterChain cochain, final List<? extends Filter> filters)
        {
            m_cochain = cochain;

            m_filters = filters;
        }

        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException
        {
            if (m_curposn == m_filters.size())
            {
                m_cochain.doFilter(request, response);
            }
            else
            {
                m_curposn++;

                m_filters.get(m_curposn - 1).doFilter(request, response, this);
            }
        }
    }
}
