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

package com.themodernway.server.core.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.google.common.util.concurrent.RateLimiter;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.limiting.IRateLimited;
import com.themodernway.server.core.logging.LoggingOps;
import com.themodernway.server.core.security.session.IServerSession;

public class CoreSpringDispatcherServlet extends DispatcherServlet implements IRateLimited, IServletCommonOperations
{
    private static final long                      serialVersionUID = 1L;

    private transient Logger                       m_logger         = LoggingOps.getLogger(getClass());

    private transient RateLimiter                  m_ratelimit;

    private final List<String>                     m_roleslist;

    private final ISessionIDFromRequestExtractor   m_extractor;

    private final IServletResponseErrorCodeManager m_errorcode;

    public CoreSpringDispatcherServlet(final WebApplicationContext context, final double rate, final List<String> role, final IServletResponseErrorCodeManager code, final ISessionIDFromRequestExtractor extr)
    {
        super(context);

        m_ratelimit = RateLimiterFactory.create(rate);

        m_roleslist = requireNonNullOrElse(role, () -> arrayList());

        m_errorcode = requireNonNullOrElse(code, CoreServletResponseErrorCodeManager.DEFAULT);

        m_extractor = requireNonNullOrElse(extr, DefaultHeaderNameSessionIDFromRequestExtractor.DEFAULT);
    }

    @Override
    public Logger logger()
    {
        return m_logger;
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

    public RateLimiter getRateLimiter()
    {
        return m_ratelimit;
    }

    public List<String> getRequiredRoles()
    {
        return toUnmodifiableList(m_roleslist);
    }

    public IServletResponseErrorCodeManager getServletResponseErrorCodeManager()
    {
        return m_errorcode;
    }

    protected void sendErrorCode(final HttpServletRequest request, final HttpServletResponse response, final int code)
    {
        getServletResponseErrorCodeManager().sendErrorCode(request, response, code);
    }

    protected void sendErrorCode(final HttpServletRequest request, final HttpServletResponse response, final int code, final String mess)
    {
        getServletResponseErrorCodeManager().sendErrorCode(request, response, code, mess);
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            if (false == isMaxContentTypeHeaderLengthValid(request, response))
            {
                sendErrorCode(request, response, HttpServletResponse.SC_BAD_REQUEST);

                return;
            }
            acquire();

            IServerSession session = null;

            final String sessid = requireNonNullOrElse(getSessionIDFromRequestExtractor(), DefaultHeaderNameSessionIDFromRequestExtractor.DEFAULT).getSessionID(request);

            if (null != sessid)
            {
                session = getSession(sessid);

                if (null == session)
                {
                    if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("invalid session (%s).", sessid));
                    }
                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                if (session.isExpired())
                {
                    if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("expired session (%s).", session.getId()));
                    }
                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
            }
            final List<String> roles = getRequiredRoles();

            if ((null != roles) && (false == roles.isEmpty()))
            {
                if (null == session)
                {
                    if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("no session with required roles in (%s).", toPrintableString(roles)));
                    }
                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                final List<String> perms = session.getRoles();

                if ((null == perms) || (perms.isEmpty()))
                {
                    if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("session (%s) with empty roles in (%s).", session.getId(), toPrintableString(roles)));
                    }
                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                if (CommonOps.none(roles, perms))
                {
                    if (logger().isErrorEnabled())
                    {
                        logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("session (%s) with no matching roles of (%s) in (%s).", session.getId(), toPrintableString(perms), toPrintableString(roles)));
                    }
                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
            }
            response.setCharacterEncoding(CHARSET_UTF_8);

            super.service(request, response);
        }
        catch (final Exception e)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "captured overall exception for security.", e);
            }
            sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
    }

    @Override
    public String getName()
    {
        return getServletConfig().getServletName();
    }

    public ISessionIDFromRequestExtractor getSessionIDFromRequestExtractor()
    {
        return m_extractor;
    }
}
