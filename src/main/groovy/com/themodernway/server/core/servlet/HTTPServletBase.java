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

package com.themodernway.server.core.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.RateLimiter;
import com.themodernway.server.core.locking.IRateLimited;

@SuppressWarnings("serial")
public abstract class HTTPServletBase extends HttpServlet implements IRateLimited, IServletCommonOperations
{
    private Logger      m_logger    = Logger.getLogger(getClass());

    private RateLimiter m_ratelimit = null;

    private int         m_contentmx = DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH;

    protected HTTPServletBase()
    {
        setRateLimiter(RateLimiterFactory.create(getClass()));
    }

    protected HTTPServletBase(final double rate)
    {
        setRateLimit(rate);
    }

    @Override
    public String getName()
    {
        return getServletConfig().getServletName();
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }

    public RateLimiter getRateLimiter()
    {
        return m_ratelimit;
    }

    public void setRateLimiter(final RateLimiter rate)
    {
        m_ratelimit = rate;
    }

    public void setRateLimit(final double rate)
    {
        setRateLimiter(RateLimiterFactory.create(rate));
    }

    @Override
    public void acquire()
    {
        final RateLimiter rate = getRateLimiter();

        if (null != rate)
        {
            rate.acquire();
        }
    }

    @Override
    public int getMaxContentTypeLength()
    {
        return m_contentmx;
    }

    public void setMaxContentTypeLength(final int contentmx)
    {
        m_contentmx = Math.min(Math.max(0, contentmx), MAXIMUM_CONTENT_TYPE_MAX_HEADER_LENGTH);
    }

    @Override
    public String getConfigurationParameter(final String name)
    {
        return getInitParameter(name);
    }

    @Override
    public List<String> getConfigurationParameterNames()
    {
        return Collections.list(getInitParameterNames());
    }

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            if (false == isRunning())
            {
                logger().error("Server is suspended, refused request.");

                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

                return;
            }
            if (false == isMaxContentTypeHeaderLengthValid(request, response))
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                return;
            }
            acquire();

            super.service(request, response);
        }
        catch (Exception e)
        {
            logger().error("Captured overall exception for security.", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
    }
}
