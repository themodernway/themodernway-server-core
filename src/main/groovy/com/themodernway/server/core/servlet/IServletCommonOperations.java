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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;

import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.ITimeSupplier;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;
import com.themodernway.server.core.security.IAuthorizationResult;
import com.themodernway.server.core.security.session.IServerSession;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;
import com.themodernway.server.core.support.spring.network.HTTPHeaders;

public interface IServletCommonOperations extends ICoreCommon, ICoreServletConstants, INamed, IHasLogging
{
    public static IServerContext getServerContextInstance()
    {
        return ServerContextInstance.getServerContextInstance();
    }

    default LinkedHashMap<String, String> getParametersFromRequest(final HttpServletRequest request)
    {
        final LinkedHashMap<String, String> parm = linkedMap();

        toList(request.getParameterNames()).forEach(name -> parm.put(name, request.getParameter(name)));

        return parm;
    }

    default JSONObject getJSONParametersFromRequest(final HttpServletRequest request)
    {
        final JSONObject parm = new JSONObject();

        toList(request.getParameterNames()).forEach(name -> parm.put(name, request.getParameter(name)));

        return parm;
    }

    default HTTPHeaders getHeadersFromRequest(final HttpServletRequest request)
    {
        return new HTTPHeaders(request);
    }

    default JSONObject getJSONHeadersFromRequest(final HttpServletRequest request)
    {
        return getHeadersFromRequest(request).toJSONObject();
    }

    default JSONObject getUserPrincipalsFromRequest(final HttpServletRequest request, final List<String> keys)
    {
        final JSONObject principals = new JSONObject();

        for (final String k : keys)
        {
            final String name = toTrimOrNull(k);

            if (null != name)
            {
                String valu = request.getHeader(name);

                if (null != valu)
                {
                    principals.put(name, valu);
                }
                else
                {
                    valu = getServerContextInstance().getPropertyByName(name);

                    if (null != valu)
                    {
                        principals.put(name, valu);
                    }
                }
            }
        }
        return principals;
    }

    default IServerSession getSessionFromRequest(final HttpServletRequest request)
    {
        final Object valu = request.getAttribute(SESSION_REQUEST_ATTRIBUTE_ID);

        if (valu instanceof IServerSession)
        {
            return CAST(valu);
        }
        return null;
    }

    default void setSessionIntoRequest(final HttpServletRequest request, final IServerSession session)
    {
        if (null == session)
        {
            request.removeAttribute(SESSION_REQUEST_ATTRIBUTE_ID);
        }
        else
        {
            request.setAttribute(SESSION_REQUEST_ATTRIBUTE_ID, session);
        }
    }

    default IServerSession getSession(final String sessid, final String domain)
    {
        return HTTPUtils.getSession(sessid, domain);
    }

    default IServerSession getSession(final String sessid)
    {
        return getSession(sessid, getSessionProviderDomainName());
    }

    @Override
    default IServerContext getServerContext()
    {
        return getServerContextInstance();
    }

    @SuppressWarnings("unchecked")
    default List<String> getCombinedRoles(final IServerSession sess, final List<String> role)
    {
        return toUnique(toListOfLists(role, (sess == null) ? arrayList() : sess.getRoles()));
    }

    default IAuthorizationResult isAuthorized(final HttpServletRequest request, final IServerSession session, final Object target, final List<String> roles)
    {
        return getServerContext().isAuthorized(target, getCombinedRoles(session, roles));
    }

    default boolean isApplicationContextInitialized()
    {
        return getServerContext().isApplicationContextInitialized();
    }

    default ApplicationContext getApplicationContext()
    {
        return getServerContext().getApplicationContext();
    }

    default WebApplicationContext getWebApplicationContext()
    {
        return getServerContext().getWebApplicationContext();
    }

    default void doNeverCache(final HttpServletRequest request, final HttpServletResponse response)
    {
        final long time = ITimeSupplier.now();

        response.setDateHeader(DATE_HEADER, time);

        response.setDateHeader(EXPIRES_HEADER, time - YEAR_IN_MILLISECONDS);

        response.setHeader(PRAGMA_HEADER, NO_CACHE_PRAGMA_HEADER_VALUE);

        response.setHeader(CACHE_CONTROL_HEADER, NO_CACHE_CONTROL_HEADER_VALUE);
    }

    default void doLongFuture(final HttpServletRequest request, final HttpServletResponse response)
    {
        response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_MAX_AGE_PREFIX + YEAR_IN_SECONDS);

        response.setDateHeader(EXPIRES_HEADER, ITimeSupplier.now() + YEAR_IN_MILLISECONDS);
    }

    default void doNearFuture(final HttpServletRequest request, final HttpServletResponse response)
    {
        response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_MAX_AGE_PREFIX + WEEK_IN_SECONDS);

        response.setDateHeader(EXPIRES_HEADER, ITimeSupplier.now() + WEEK_IN_MILLISECONDS);
    }

    default int getMaxContentTypeLength()
    {
        return DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH;
    }

    default boolean isMaxContentTypeHeaderLengthValid(final HttpServletRequest request, final HttpServletResponse response)
    {
        return isMaxHeaderLengthValid(request, response, CONTENT_TYPE_HEADER, Math.min(Math.max(0, getMaxContentTypeLength()), MAXIMUM_CONTENT_TYPE_MAX_HEADER_LENGTH));
    }

    default boolean isMaxHeaderLengthValid(final HttpServletRequest request, final HttpServletResponse response, String head, int leng)
    {
        if ((null != (head = toTrimOrNull(head))) && ((leng = Math.max(0, leng)) > 0))
        {
            final String valu = toTrimOrNull(request.getHeader(head));

            if ((null != valu) && (valu.length() > leng))
            {
                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, format("possible header attack on (%s), max is (%d), found (%d), value (%s).", head, leng, valu.length(), valu));
                }
                return false;
            }
        }
        return true;
    }

    default void destroy()
    {
    }

    default String getSessionProviderDomainName()
    {
        return toTrimOrElse(getConfigurationParameter(SESSION_PROVIDER_DOMAIN_NAME_PARAM), STRING_DEFAULT);
    }

    default List<String> getConfigurationParameterNames()
    {
        return emptyList();
    }

    default List<String> getDefaultRoles()
    {
        return toUnmodifiableList("ANONYMOUS");
    }

    default String getConfigurationParameter(final String name)
    {
        return null;
    }

    default Map<String, String> getConfigurationParameters()
    {
        final LinkedHashMap<String, String> conf = new LinkedHashMap<>();

        for (final String name : getConfigurationParameterNames())
        {
            final String valu = getConfigurationParameter(name);

            if (null != valu)
            {
                conf.put(name, valu);
            }
        }
        return conf;
    }

    default String getConfigurationParameterOrProperty(final String name)
    {
        return toTrimOrElse(getConfigurationParameter(name), getServerContext().getPropertyByName(name));
    }

    default String getConfigurationParameterOrPropertyOtherwise(final String name, final String otherwise)
    {
        return toTrimOrElse(getConfigurationParameter(name), getServerContext().getPropertyByName(name, otherwise));
    }

    default boolean isNotModifiedSince(final HttpServletRequest request, final HttpServletResponse response, final long time)
    {
        return getServletWebRequest(request, response).checkNotModified(time);
    }

    default boolean isModifiedSince(final HttpServletRequest request, final HttpServletResponse response, final long time)
    {
        return (false == isNotModifiedSince(request, response, time));
    }

    default ServletWebRequest getServletWebRequest(final HttpServletRequest request, final HttpServletResponse response)
    {
        return new ServletWebRequest(request, response);
    }
}
