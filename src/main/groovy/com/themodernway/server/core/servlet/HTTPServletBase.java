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
import com.themodernway.server.core.limiting.IRateLimited;
import com.themodernway.server.core.security.session.IServerSession;

@SuppressWarnings("serial")
public abstract class HTTPServletBase extends HttpServlet implements IRateLimited, IServletCommonOperations
{
    private final Logger m_logger    = Logger.getLogger(getClass());

    private RateLimiter  m_ratelimit = null;

    private boolean      m_iscontent = false;

    private List<String> m_roleslist = arrayList();

    private int          m_contentmx = DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH;

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
    public boolean isMaxContentTypeLengthInitialized()
    {
        return m_iscontent;
    }

    @Override
    public int getMaxContentTypeLength()
    {
        return m_contentmx;
    }

    @Override
    public void setMaxContentTypeLength(final int contentmx)
    {
        m_iscontent = true;

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
        return toList(getInitParameterNames());
    }

    public List<String> getRequiredRoles()
    {
        return toUnmodifiableList(m_roleslist);
    }

    public void setRequiredRoles(String roles)
    {
        if (null == (roles = toTrimOrNull(roles)))
        {
            setRequiredRoles(arrayList());
        }
        else
        {
            setRequiredRoles(toUniqueTokenStringList(roles));
        }
    }

    public void setRequiredRoles(final List<String> roles)
    {
        m_roleslist = (roles == null ? arrayList() : roles);
    }

    protected void doPatch(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        if (request.getProtocol().endsWith("1.1"))
        {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "PATCH");
        }
        else
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "PATCH");
        }
    }

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            if (false == isRunning())
            {
                logger().error("server is suspended, refused request.");

                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

                return;
            }
            if (false == isMaxContentTypeHeaderLengthValid(request, response))
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                return;
            }
            acquire();

            IServerSession session = null;

            final String sessid = getSessionID(request);

            if (null != sessid)
            {
                session = getSession(sessid);

                if (null == session)
                {
                    logger().error(format("invalid session (%s).", sessid));

                    response.addHeader(WWW_AUTHENTICATE, "no permission");

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                if (session.isExpired())
                {
                    logger().error(format("expired session (%s).", session.getId()));

                    response.addHeader(WWW_AUTHENTICATE, "expired session");

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
            }
            final List<String> roles = getRequiredRoles();

            if ((null != roles) && (false == roles.isEmpty()))
            {
                if (null == session)
                {
                    logger().error(format("no session with required roles in (%s).", toPrintableString(roles)));

                    response.addHeader(WWW_AUTHENTICATE, "no permission");

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                final List<String> perms = session.getRoles();

                if ((null == perms) || (perms.isEmpty()))
                {
                    logger().error(format("session (%s) with empty roles in (%s).", session.getId(), toPrintableString(roles)));

                    response.addHeader(WWW_AUTHENTICATE, "no permission");

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                if (Collections.disjoint(roles, perms))
                {
                    logger().error(format("session (%s) with no matching roles of (%s) in (%s).", session.getId(), toPrintableString(perms), toPrintableString(roles)));

                    response.addHeader(WWW_AUTHENTICATE, "no permission");

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
            }
            final String meth = toTrimOrElse(request.getMethod(), EMPTY_STRING).toUpperCase();

            if (HTTP_METHOD_GET.equals(meth))
            {
                doGet(request, response);

                return;
            }
            else if (HTTP_METHOD_HEAD.equals(meth))
            {
                doHead(request, response);

                return;
            }
            else if (HTTP_METHOD_POST.equals(meth))
            {
                doPost(request, response);

                return;
            }
            else if (HTTP_METHOD_PUT.equals(meth))
            {
                doPut(request, response);

                return;
            }
            else if (HTTP_METHOD_DELETE.equals(meth))
            {
                doDelete(request, response);

                return;
            }
            else if (HTTP_METHOD_PATCH.equals(meth))
            {
                doPatch(request, response);

                return;
            }
            else if (HTTP_METHOD_OPTIONS.equals(meth))
            {
                doOptions(request, response);

                return;
            }
            else if (HTTP_METHOD_TRACE.equals(meth))
            {
                doTrace(request, response);

                return;
            }
            super.service(request, response);
        }
        catch (Exception e)
        {
            logger().error("captured overall exception for security.", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
    }

    @Override
    public void init()
    {
        doInitializeMaxContentTypeLength();
    }
}
