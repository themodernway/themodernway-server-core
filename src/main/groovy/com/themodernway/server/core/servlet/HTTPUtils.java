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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.themodernway.common.api.java.util.IHTTPConstants;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.security.session.IServerSession;
import com.themodernway.server.core.security.session.IServerSessionRepository;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public final class HTTPUtils implements IHTTPConstants
{
    public static final String PROTO_1_1_SUFFIX_DEFAULT = "1.1";

    public static final String HTTPS_URL_PREFIX_DEFAULT = "https";

    public static final String SESSION_PROVIDER_DEFAULT = "default";

    private HTTPUtils()
    {
    }

    public static Cookie setCookie(final HttpServletRequest request, final HttpServletResponse response, final String name, final String value, final String path)
    {
        return setCookie(request, response, name, value, path, YEAR_IN_SECONDS);
    }

    public static Cookie setCookie(final HttpServletRequest request, final HttpServletResponse response, final String name, final String value, final long seconds)
    {
        return setCookie(request, response, name, value, null, seconds);
    }

    public static Cookie setCookie(final HttpServletRequest request, final HttpServletResponse response, String name, final String value, String path, final long seconds)
    {
        if ((null != request) && (null != response) && (null != (name = StringOps.toTrimOrNull(name))))
        {
            if (null == value)
            {
                final Cookie cookie = new Cookie(name, StringOps.EMPTY_STRING);

                cookie.setMaxAge(0);

                final String ruri = request.getHeader(REFERER_HEADER);

                if (null != ruri)
                {
                    if (ruri.startsWith(HTTPS_URL_PREFIX_DEFAULT))
                    {
                        cookie.setSecure(true);
                    }
                }
                if (null != (path = StringOps.toTrimOrNull(path)))
                {
                    cookie.setPath(path);
                }
                response.addCookie(cookie);

                return cookie;
            }
            else
            {
                final Cookie cookie = new Cookie(name, value);

                cookie.setMaxAge((int) seconds);

                final String ruri = request.getHeader(REFERER_HEADER);

                if (null != ruri)
                {
                    if (ruri.startsWith(HTTPS_URL_PREFIX_DEFAULT))
                    {
                        cookie.setSecure(true);
                    }
                }
                if (null != (path = StringOps.toTrimOrNull(path)))
                {
                    cookie.setPath(path);
                }
                response.addCookie(cookie);

                return cookie;
            }
        }
        return null;
    }

    public static String getSessionID(final HttpServletRequest request, String lookup)
    {
        lookup = StringOps.toTrimOrElse(lookup, X_SESSION_ID_HEADER);

        String sessid = StringOps.toTrimOrNull(request.getHeader(lookup));

        if (null != sessid)
        {
            return sessid;
        }
        final HttpSession httpsession = request.getSession(false);

        if (null != httpsession)
        {
            final Object attribute = httpsession.getAttribute(lookup);

            if (attribute instanceof String)
            {
                sessid = StringOps.toTrimOrNull(attribute.toString());

                if (null != sessid)
                {
                    return sessid;
                }
            }
        }
        return null;
    }

    public static IServerSession getSession(String sessid)
    {
        if (null == (sessid = StringOps.toTrimOrNull(sessid)))
        {
            return null;
        }
        return getSession(sessid, SESSION_PROVIDER_DEFAULT);
    }

    public static IServerSession getSession(String sessid, final String domain)
    {
        if (null == (sessid = StringOps.toTrimOrNull(sessid)))
        {
            return null;
        }
        final IServerContext context = getServerContext();

        if (context.isApplicationContextInitialized())
        {
            return getSession(sessid, context.getServerSessionRepository(StringOps.toTrimOrElse(domain, SESSION_PROVIDER_DEFAULT)));
        }
        return null;
    }

    public static IServerSession getSession(String sessid, final IServerSessionRepository repository)
    {
        if (null == (sessid = StringOps.toTrimOrNull(sessid)))
        {
            return null;
        }
        if (null != repository)
        {
            final IServerSession session = repository.getSession(sessid);

            if (null != session)
            {
                return session;
            }
        }
        return null;
    }

    public static String getSessionID(final HttpServletRequest request)
    {
        return getSessionID(request, X_SESSION_ID_HEADER);
    }

    public static IServerContext getServerContext()
    {
        return ServerContextInstance.getServerContextInstance();
    }
}
