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

package com.themodernway.server.core.support.spring.network;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.IHTTPConstants;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;

public class HTTPHeaders extends HttpHeaders
{
    private static final long            serialVersionUID       = 1L;

    public static final String           DEFAULT_USER_AGENT     = String.format("The-Modern-Way/2.0.10 (Language=Java/%s)", System.getProperty("java.version"));

    public static final MediaType        XML_MEDIA_TYPE         = MediaType.APPLICATION_XML;

    public static final MediaType        JSON_MEDIA_TYPE        = MediaType.APPLICATION_JSON_UTF8;

    public static final MediaType        YAML_MEDIA_TYPE        = MediaType.valueOf(IHTTPConstants.CONTENT_TYPE_APPLICATION_YAML);

    public static final MediaType        PROPERTIES_MEDIA_TYPE  = MediaType.valueOf(IHTTPConstants.CONTENT_TYPE_TEXT_PROPERTIES);

    private static final List<MediaType> JSON_ACCEPT_MEDIA_TYPE = CommonOps.toList(JSON_MEDIA_TYPE);

    public HTTPHeaders(final HttpHeaders head)
    {
        addAll(head);
    }

    public HTTPHeaders()
    {
    }

    public HTTPHeaders(final HttpServletRequest request)
    {
        addAll(new ServletServerHttpRequest(request).getHeaders());
    }

    public void setHttpServletResponse(final HttpServletResponse response)
    {
        final ServletServerHttpResponse resp = new ServletServerHttpResponse(response);

        resp.getHeaders().addAll(this);

        resp.close();
    }

    public HTTPHeaders doRESTHeaders()
    {
        return doRESTHeaders(DEFAULT_USER_AGENT);
    }

    public HTTPHeaders doRESTHeaders(final String ua)
    {
        final List<String> json = get(ACCEPT);

        if ((null == json) || (json.isEmpty()))
        {
            setAccept(JSON_ACCEPT_MEDIA_TYPE);
        }
        final List<MediaType> list = getAccept();

        if ((null == list) || (list.isEmpty()))
        {
            setAccept(JSON_ACCEPT_MEDIA_TYPE);
        }
        return doUserAgent(ua);
    }

    public HTTPHeaders doUserAgent()
    {
        return doUserAgent(DEFAULT_USER_AGENT);
    }

    public HTTPHeaders doUserAgent(final String ua)
    {
        return addUserAgent(StringOps.toTrimOrElse(ua, DEFAULT_USER_AGENT));
    }

    public HTTPHeaders addUserAgent(String ua)
    {
        ua = StringOps.toTrimOrNull(ua);

        if (null != ua)
        {
            final List<String> list = get(USER_AGENT);

            if ((null == list) || (list.isEmpty()))
            {
                add(USER_AGENT, ua);
            }
            else
            {
                for (String item : list)
                {
                    item = StringOps.toTrimOrNull(item);

                    if ((null != item) && (ua.equalsIgnoreCase(item)))
                    {
                        return this;
                    }
                }
                add(USER_AGENT, ua);
            }
        }
        return this;
    }

    public JSONObject toJSONObject()
    {
        final JSONObject make = new JSONObject();

        for (final String k : CommonOps.toList(keySet()))
        {
            if (null != k)
            {
                final List<String> list = get(k);

                if ((null != list) && (false == list.isEmpty()))
                {
                    make.put(k, StringOps.toList(list.stream()));
                }
            }
        }
        return make;
    }

    public String toJSONString()
    {
        return toJSONObject().toJSONString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }
}
