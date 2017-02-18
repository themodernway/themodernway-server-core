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
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.RateLimiter;
import com.themodernway.common.api.java.util.IHTTPConstants;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.locking.IRateLimited;
import com.themodernway.server.core.security.AuthorizationResult;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

@SuppressWarnings("serial")
public abstract class HTTPServletBase extends HttpServlet implements IRateLimited, IHTTPConstants
{
    private static final Logger logger                             = Logger.getLogger(HTTPServletBase.class);

    public static final String  SESSION_PROVIDER_DOMAIN_NAME_PARAM = "core.server.session.provider.domain.name";

    public static final String  UNKNOWN_USER                       = "%-UNKNOWN-USER-%";

    public static final String  NULL_SESSION                       = "%-NULL-SESSION-%";

    private final RateLimiter   m_ratelimit;

    protected HTTPServletBase()
    {
        m_ratelimit = RateLimiterFactory.create(getClass());
    }

    protected HTTPServletBase(final double rate)
    {
        m_ratelimit = RateLimiterFactory.create(rate);
    }

    @Override
    public final void acquire()
    {
        if (null != m_ratelimit)
        {
            m_ratelimit.acquire();
        }
    }

    protected String getSessionProviderDomainName()
    {
        return StringOps.toTrimOrElse(getInitParameter(SESSION_PROVIDER_DOMAIN_NAME_PARAM), "default");
    }

    protected boolean isRunning()
    {
        return getServerContext().getCoreServerManager().isRunning();
    }

    protected final static IServerContext getServerContext()
    {
        return ServerContextInstance.getServerContextInstance();
    }

    protected void doNoCache(final HttpServletResponse response)
    {
        final long time = System.currentTimeMillis();

        response.setDateHeader(DATE_HEADER, time);

        response.setDateHeader(EXPIRES_HEADER, time - YEAR_IN_MILLISECONDS);

        response.setHeader(PRAGMA_HEADER, NO_CACHE_PRAGMA_HEADER_VALUE);

        response.setHeader(CACHE_CONTROL_HEADER, NO_CACHE_CONTROL_HEADER_VALUE);
    }

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        if (false == isRunning())
        {
            logger.error("server is suspended, refused request");

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

            return;
        }
        acquire();

        super.service(request, response);
    }

    public static final LinkedHashMap<String, String> getParametersFromRequest(final HttpServletRequest request)
    {
        final LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        final Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements())
        {
            final String name = names.nextElement();

            params.put(name, request.getParameter(name));
        }
        return params;
    }

    public static final JSONObject getJSONParametersFromRequest(final HttpServletRequest request)
    {
        return new JSONObject(getParametersFromRequest(request));
    }

    public static final LinkedHashMap<String, String> getHeadersFromRequest(final HttpServletRequest request)
    {
        final LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        final Enumeration<String> names = request.getHeaderNames();

        while (names.hasMoreElements())
        {
            final String name = names.nextElement();

            params.put(name, request.getHeader(name));
        }
        return params;
    }

    public static final JSONObject getJSONHeadersFromRequest(final HttpServletRequest request)
    {
        return new JSONObject(getHeadersFromRequest(request));
    }

    public static final JSONObject getUserPrincipalsFromRequest(final HttpServletRequest request, final List<String> keys)
    {
        final JSONObject principals = new JSONObject();

        for (String k : keys)
        {
            final String name = StringOps.toTrimOrNull(k);

            if (null != name)
            {
                String valu = request.getHeader(name);

                if (null != valu)
                {
                    principals.put(name, valu);
                }
                else
                {
                    valu = getServerContext().getPropertyByName(name);

                    if (null != valu)
                    {
                        principals.put(name, valu);
                    }
                }
            }
        }
        return principals;
    }

    protected final AuthorizationResult isAuthorized(final Object target, final List<String> roles)
    {
        return getServerContext().isAuthorized(target, roles);
    }
}
