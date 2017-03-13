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

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;

public class ContentTypeLengthFilter extends HTTPFilterBase
{
    private boolean m_iscontent = false;

    private int     m_contentmx = DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH;

    public ContentTypeLengthFilter()
    {
    }

    @Override
    public int getMaxContentTypeLength()
    {
        return m_contentmx;
    }

    public void setMaxContentTypeLength(final int contentmx)
    {
        m_iscontent = true;

        m_contentmx = Math.min(Math.max(0, contentmx), MAXIMUM_CONTENT_TYPE_MAX_HEADER_LENGTH);
    }

    @Override
    public void doInit(final FilterConfig fc) throws ServletException
    {
        if (false == m_iscontent)
        {
            final String size = StringOps.toTrimOrNull(getConfigurationParameter(CONTENT_TYPE_MAX_HEADER_LENGTH_PARAM));

            if (null != size)
            {
                try
                {
                    setMaxContentTypeLength(Integer.parseInt(size));
                }
                catch (Exception e)
                {
                }
            }
        }
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (false == isMaxContentTypeHeaderLengthValid(request, response))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }
        chain.doFilter(request, response);
    }
}
