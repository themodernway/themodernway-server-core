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
    private static final long                serialVersionUID = 1L;

    private final Logger                     m_logger         = LoggingOps.LOGGER(getClass());

    private RateLimiter                      m_ratelimit      = null;

    private boolean                          m_iscontent      = false;

    private int                              m_contentmx      = DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH;

    private List<String>                     m_roleslist      = arrayList();

    private ISessionIDFromRequestExtractor   m_extractor      = DefaultHeaderNameSessionIDFromRequestExtractor.DEFAULT;

    private IServletResponseErrorCodeManager m_errorcode      = CoreServletResponseErrorCodeManager.DEFAULT;

    public CoreSpringDispatcherServlet(final WebApplicationContext context)
    {
        super(context);
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

    public void setRateLimiter(final RateLimiter rate)
    {
        m_ratelimit = rate;
    }

    public void setRateLimit(final double rate)
    {
        setRateLimiter(RateLimiterFactory.create(rate));
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

    public void setServletResponseErrorCodeManager(final IServletResponseErrorCodeManager manager)
    {
        m_errorcode = requireNonNull(manager);
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
                    logger().error(format("invalid session (%s).", sessid));

                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                if (session.isExpired())
                {
                    logger().error(format("expired session (%s).", session.getId()));

                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
            }
            final List<String> roles = getRequiredRoles();

            if ((null != roles) && (false == roles.isEmpty()))
            {
                if (null == session)
                {
                    logger().error(format("no session with required roles in (%s).", toPrintableString(roles)));

                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                final List<String> perms = session.getRoles();

                if ((null == perms) || (perms.isEmpty()))
                {
                    logger().error(format("session (%s) with empty roles in (%s).", session.getId(), toPrintableString(roles)));

                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
                if (CommonOps.none(roles, perms))
                {
                    logger().error(format("session (%s) with no matching roles of (%s) in (%s).", session.getId(), toPrintableString(perms), toPrintableString(roles)));

                    sendErrorCode(request, response, HttpServletResponse.SC_FORBIDDEN);

                    return;
                }
            }
            response.setCharacterEncoding(CHARSET_UTF_8);

            super.service(request, response);
        }
        catch (final Exception e)
        {
            logger().error("captured overall exception for security.", e);

            sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
    }

    @Override
    public String getName()
    {
        return getServletConfig().getServletName();
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

    public ISessionIDFromRequestExtractor getSessionIDFromRequestExtractor()
    {
        return m_extractor;
    }

    public void setSessionIDFromRequestExtractor(final ISessionIDFromRequestExtractor extractor)
    {
        m_extractor = extractor;
    }

    @Override
    protected void initFrameworkServlet() throws ServletException
    {
        doInitializeMaxContentTypeLength();
    }
}
