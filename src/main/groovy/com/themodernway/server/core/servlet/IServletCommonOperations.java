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

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.IHTTPConstants;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.security.AuthorizationResult;
import com.themodernway.server.core.security.session.IServerSession;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public interface IServletCommonOperations extends IHTTPConstants, INamed
{
    public static final int    DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH = 64;

    public static final int    MAXIMUM_CONTENT_TYPE_MAX_HEADER_LENGTH = 128;

    public static final String SESSION_PROVIDER_DOMAIN_NAME_PARAM     = "core.server.session.provider.domain.name";

    public static final String CONTENT_TYPE_MAX_HEADER_LENGTH_PARAM   = "core.server.content.type.max.header.length";

    public static final String UNKNOWN_USER                           = "%-UNKNOWN-USER-%";

    public static final String NULL_SESSION                           = "%-NULL-SESSION-%";

    public static IServerContext getServerContextInstance()
    {
        return ServerContextInstance.getServerContextInstance();
    }

    public default LinkedHashMap<String, String> getParametersFromRequest(final HttpServletRequest request)
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

    public default JSONObject getJSONParametersFromRequest(final HttpServletRequest request)
    {
        return new JSONObject(getParametersFromRequest(request));
    }

    public default LinkedHashMap<String, String> getHeadersFromRequest(final HttpServletRequest request)
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

    public default JSONObject getJSONHeadersFromRequest(final HttpServletRequest request)
    {
        return new JSONObject(getHeadersFromRequest(request));
    }

    public default JSONObject getUserPrincipalsFromRequest(final HttpServletRequest request, final List<String> keys)
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

    public default IServerContext getServerContext()
    {
        return getServerContextInstance();
    }

    public default AuthorizationResult isAuthorized(final HttpServletRequest request, final IServerSession session, final Object target, final List<String> roles)
    {
        return getServerContext().isAuthorized(target, roles);
    }

    public default boolean isApplicationContextInitialized()
    {
        return getServerContext().isApplicationContextInitialized();
    }

    public default ApplicationContext getApplicationContext()
    {
        return getServerContext().getApplicationContext();
    }

    public default WebApplicationContext getWebApplicationContext()
    {
        return getServerContext().getWebApplicationContext();
    }

    public default boolean isRunning()
    {
        return getServerContext().getCoreServerManager().isRunning();
    }

    public default void doNeverCache(final HttpServletRequest request, final HttpServletResponse response)
    {
        final long time = System.currentTimeMillis();

        response.setDateHeader(DATE_HEADER, time);

        response.setDateHeader(EXPIRES_HEADER, time - YEAR_IN_MILLISECONDS);

        response.setHeader(PRAGMA_HEADER, NO_CACHE_PRAGMA_HEADER_VALUE);

        response.setHeader(CACHE_CONTROL_HEADER, NO_CACHE_CONTROL_HEADER_VALUE);
    }

    public default void doLongFuture(final HttpServletRequest request, final HttpServletResponse response)
    {
        response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_MAX_AGE_PREFIX + YEAR_IN_SECONDS);

        response.setDateHeader(EXPIRES_HEADER, System.currentTimeMillis() + YEAR_IN_MILLISECONDS);
    }

    public default void doNearFuture(final HttpServletRequest request, final HttpServletResponse response)
    {
        response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_MAX_AGE_PREFIX + WEEK_IN_SECONDS);

        response.setDateHeader(EXPIRES_HEADER, System.currentTimeMillis() + WEEK_IN_MILLISECONDS);
    }

    public default Logger logger()
    {
        return getServerContext().logger();
    }

    public default int getMaxContentTypeLength()
    {
        return DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH;
    }

    public default boolean isMaxContentTypeLengthInitialized()
    {
        return false;
    }

    public void setMaxContentTypeLength(int max);

    public default boolean isMaxContentTypeHeaderLengthValid(final HttpServletRequest request, final HttpServletResponse response)
    {
        return isMaxHeaderLengthValid(request, response, CONTENT_TYPE_HEADER, Math.min(Math.max(0, getMaxContentTypeLength()), MAXIMUM_CONTENT_TYPE_MAX_HEADER_LENGTH));
    }

    public default boolean isMaxHeaderLengthValid(final HttpServletRequest request, final HttpServletResponse response, String head, int leng)
    {
        if (null != (head = StringOps.toTrimOrNull(head)))
        {
            if ((leng = Math.max(0, leng)) > 0)
            {
                final String valu = StringOps.toTrimOrNull(request.getHeader(head));

                if ((null != valu) && (valu.length() > leng))
                {
                    logger().error(String.format("Possible header attack on %s, max is %d, found %d, value (%s).", head, leng, valu.length(), valu));

                    return false;
                }
            }
        }
        return true;
    }

    public default void destroy()
    {
    }

    public default String getSessionProviderDomainName()
    {
        return StringOps.toTrimOrElse(getConfigurationParameter(SESSION_PROVIDER_DOMAIN_NAME_PARAM), "default");
    }

    public default List<String> getConfigurationParameterNames()
    {
        return Collections.emptyList();
    }

    public default String getConfigurationParameter(String name)
    {
        return null;
    }

    public default void doInitializeMaxContentTypeLength()
    {
        if (false == isMaxContentTypeLengthInitialized())
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
                    logger().error(String.format("Error parsing parameter %s, value (%s)", CONTENT_TYPE_MAX_HEADER_LENGTH_PARAM, size), e);
                }
            }
        }
    }

    public default Map<String, String> getConfigurationParameters()
    {
        final LinkedHashMap<String, String> conf = new LinkedHashMap<String, String>();

        for (String name : getConfigurationParameterNames())
        {
            final String valu = getConfigurationParameter(name);

            if (null != valu)
            {
                conf.put(name, valu);
            }
        }
        return conf;
    }

    public default String getConfigurationParameterOrProperty(final String name)
    {
        return StringOps.toTrimOrElse(getConfigurationParameter(name), getServerContext().getPropertyByName(name));
    }

    public default String getConfigurationParameterOrPropertyOtherwise(final String name, final String otherwise)
    {
        return StringOps.toTrimOrElse(getConfigurationParameter(name), getServerContext().getPropertyByName(name, otherwise));
    }
}
