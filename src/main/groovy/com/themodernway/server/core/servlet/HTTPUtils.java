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

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.security.session.IServerSession;
import com.themodernway.server.core.security.session.IServerSessionRepository;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public final class HTTPUtils implements ICoreServletConstants
{
    private HTTPUtils()
    {
    }

    public static Cookie setCookie(final HttpServletRequest request, final HttpServletResponse response, final String name, final String value, final String path)
    {
        return setCookie(request, response, name, value, path, TimeUnit.SECONDS, YEAR_IN_SECONDS);
    }

    public static Cookie setCookie(final HttpServletRequest request, final HttpServletResponse response, final String name, final String value, final TimeUnit unit, final long duration)
    {
        return setCookie(request, response, name, value, null, unit, duration);
    }

    public static Cookie setCookie(final HttpServletRequest request, final HttpServletResponse response, String name, final String value, String path, final TimeUnit unit, final long duration)
    {
        if ((null != request) && (null != response) && (null != (name = StringOps.toTrimOrNull(name))))
        {
            if (null == value)
            {
                final Cookie cookie = new Cookie(name, StringOps.EMPTY_STRING);

                cookie.setMaxAge(0);

                final String ruri = StringOps.toTrimOrNull(request.getHeader(REFERER_HEADER));

                if ((null != ruri) && (ruri.startsWith(HTTPS_URL_PREFIX_DEFAULT)))
                {
                    cookie.setSecure(true);
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

                final long seconds = unit.toSeconds(duration);

                final long rounded = Math.max(0, Math.min(seconds, YEAR_IN_SECONDS));

                cookie.setMaxAge(Math.toIntExact(rounded));

                final String ruri = StringOps.toTrimOrNull(request.getHeader(REFERER_HEADER));

                if ((null != ruri) && (ruri.startsWith(HTTPS_URL_PREFIX_DEFAULT)))
                {
                    cookie.setSecure(true);
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

    public static IServerContext getServerContext()
    {
        return ServerContextInstance.getServerContextInstance();
    }
}
