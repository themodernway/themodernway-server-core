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

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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

    public static final Cookie newCookie(final HttpServletRequest request, final String name, final String value, final String path)
    {
        return newCookie(request, name, value, path, TimeUnit.SECONDS, YEAR_IN_SECONDS);
    }

    public static final Cookie newCookie(final HttpServletRequest request, final String name, final String value, final TimeUnit unit, final long duration)
    {
        return newCookie(request, name, value, null, unit, duration);
    }

    public static final Cookie newCookie(final HttpServletRequest request, String name, final String value, String path, final TimeUnit unit, final long duration)
    {
        if ((null != request) && (null != (name = StringOps.toTrimOrNull(name))))
        {
            if (null == value)
            {
                final Cookie cookie = new Cookie(name, StringOps.EMPTY_STRING);

                cookie.setMaxAge(0);

                if (request.isSecure())
                {
                    cookie.setSecure(true);
                }
                else
                {
                    final String ruri = StringOps.toTrimOrNull(request.getHeader(REFERER_HEADER));

                    if ((null != ruri) && (ruri.startsWith(HTTPS_URL_PREFIX_DEFAULT)))
                    {
                        cookie.setSecure(true);
                    }
                }
                if (null != (path = StringOps.toTrimOrNull(path)))
                {
                    cookie.setPath(path);
                }
                return cookie;
            }
            else
            {
                final Cookie cookie = new Cookie(name, value);

                final long seconds = unit.toSeconds(duration);

                final long rounded = Math.max(0, Math.min(seconds, YEAR_IN_SECONDS));

                cookie.setMaxAge(Math.toIntExact(rounded));

                if (request.isSecure())
                {
                    cookie.setSecure(true);
                }
                else
                {
                    final String ruri = StringOps.toTrimOrNull(request.getHeader(REFERER_HEADER));

                    if ((null != ruri) && (ruri.startsWith(HTTPS_URL_PREFIX_DEFAULT)))
                    {
                        cookie.setSecure(true);
                    }
                }
                if (null != (path = StringOps.toTrimOrNull(path)))
                {
                    cookie.setPath(path);
                }
                return cookie;
            }
        }
        return null;
    }

    public static final IServerSession getSession(String sessid)
    {
        if (null == (sessid = StringOps.toTrimOrNull(sessid)))
        {
            return null;
        }
        return getSession(sessid, STRING_DEFAULT);
    }

    public static final IServerSession getSession(String sessid, final String domain)
    {
        if (null == (sessid = StringOps.toTrimOrNull(sessid)))
        {
            return null;
        }
        final IServerContext context = ServerContextInstance.getServerContextInstance();

        if (context.isApplicationContextInitialized())
        {
            return getSession(sessid, context.getServerSessionRepository(StringOps.toTrimOrElse(domain, STRING_DEFAULT)));
        }
        return null;
    }

    public static final IServerSession getSession(String sessid, final IServerSessionRepository repository)
    {
        if (null == (sessid = StringOps.toTrimOrNull(sessid)))
        {
            return null;
        }
        if (null != repository)
        {
            final IServerSession session = repository.findById(sessid);

            if (null != session)
            {
                return session;
            }
        }
        return null;
    }
}
