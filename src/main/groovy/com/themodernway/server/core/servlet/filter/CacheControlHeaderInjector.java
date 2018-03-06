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

package com.themodernway.server.core.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.logging.LoggingOps;

public class CacheControlHeaderInjector extends HeaderInjectorBase
{
    private String m_dont_cache_regex = null;

    private String m_long_cache_regex = null;

    private String m_near_cache_regex = null;

    private String m_skip_cache_regex = null;

    public CacheControlHeaderInjector()
    {
    }

    public CacheControlHeaderInjector(final IHeaderInjectorFilter filter)
    {
        setHeaderInjectorFilter(filter);
    }

    @Override
    public int inject(final HttpServletRequest request, final HttpServletResponse response)
    {
        final String url = StringOps.toTrimOrNull(request.getRequestURI());

        if (null != url)
        {
            String regex = StringOps.toTrimOrNull(getDontCacheRegex());

            if ((null != regex) && (url.matches(regex)))
            {
                doNeverCache(request, response);

                return HttpServletResponse.SC_OK;
            }
            regex = StringOps.toTrimOrNull(getLongCacheRegex());

            if ((null != regex) && (url.matches(regex)))
            {
                doLongFuture(request, response);

                return HttpServletResponse.SC_OK;
            }
            regex = StringOps.toTrimOrNull(getNearCacheRegex());

            if ((null != regex) && (url.matches(regex)))
            {
                doNearFuture(request, response);

                return HttpServletResponse.SC_OK;
            }
            regex = StringOps.toTrimOrNull(getSkipCacheRegex());

            if ((null != regex) && (url.matches(regex)))
            {
                return HttpServletResponse.SC_OK;
            }
            doCacheByURL(url, request, response);
        }
        return HttpServletResponse.SC_OK;
    }

    public String getDontCacheRegex()
    {
        return m_dont_cache_regex;
    }

    public void setDontCacheRegex(final String regex)
    {
        m_dont_cache_regex = StringOps.toTrimOrNull(regex);

        if (null != m_dont_cache_regex)
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CacheControlHeaderInjector().setDontCacheRegex(" + m_dont_cache_regex + ")");
        }
    }

    public String getLongCacheRegex()
    {
        return m_long_cache_regex;
    }

    public void setLongCacheRegex(final String regex)
    {
        m_long_cache_regex = StringOps.toTrimOrNull(regex);

        if (null != m_long_cache_regex)
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CacheControlHeaderInjector().setLongCacheRegex(" + m_long_cache_regex + ")");
        }
    }

    public String getNearCacheRegex()
    {
        return m_near_cache_regex;
    }

    public void setNearCacheRegex(final String regex)
    {
        m_near_cache_regex = StringOps.toTrimOrNull(regex);

        if (null != m_near_cache_regex)
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CacheControlHeaderInjector().setNearCacheRegex(" + m_near_cache_regex + ")");
        }
    }

    public String getSkipCacheRegex()
    {
        return m_skip_cache_regex;
    }

    public void setSkipCacheRegex(final String regex)
    {
        m_skip_cache_regex = StringOps.toTrimOrNull(regex);

        if (null != m_skip_cache_regex)
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CacheControlHeaderInjector().setSkipCacheRegex(" + m_skip_cache_regex + ")");
        }
    }

    protected void doCacheByURL(String url, final HttpServletRequest request, final HttpServletResponse response)
    {
        url = StringOps.toTrimOrNull(url);

        if (null != url)
        {
            if (url.endsWith(".rpc"))
            {
                doNeverCache(request, response);
            }
            else if (url.indexOf(".nocache.") > 0)
            {
                doNeverCache(request, response);
            }
            else if (url.indexOf(".cache.") > 0)
            {
                doLongFuture(request, response);
            }
            else if (url.endsWith(".js"))
            {
                doNearFuture(request, response);
            }
            else if (url.endsWith(".jpg"))
            {
                doNearFuture(request, response);
            }
            else if (url.endsWith(".png"))
            {
                doNearFuture(request, response);
            }
            else if (url.endsWith(".gif"))
            {
                doNearFuture(request, response);
            }
            else if (url.endsWith(".jsp"))
            {
                doNeverCache(request, response);
            }
            else if (url.endsWith(".css"))
            {
                doNearFuture(request, response);
            }
            else if (url.endsWith(".swf"))
            {
                doNearFuture(request, response);
            }
            else if (url.endsWith(".html"))
            {
                ;// let it check every request
            }
            else if (url.endsWith(".htm"))
            {
                ;// let it check every request
            }
            else if (url.endsWith(".ws"))
            {
                ;// let it check every request
            }
            else if (url.endsWith(".websocket"))
            {
                ;// let it check every request
            }
            else
            {
                doNeverCache(request, response);
            }
        }
    }

    @Override
    public void config(final JSONObject config)
    {
        if (null != config)
        {
            setDontCacheRegex(config.getAsString("dont-cache-regex"));

            setLongCacheRegex(config.getAsString("long-cache-regex"));

            setNearCacheRegex(config.getAsString("near-cache-regex"));

            setSkipCacheRegex(config.getAsString("skip-cache-regex"));
        }
    }
}
