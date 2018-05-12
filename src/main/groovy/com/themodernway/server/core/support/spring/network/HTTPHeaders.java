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

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.IHTTPConstants;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.json.JSONStringifyStrict;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONObjectSupplier;

public class HTTPHeaders extends HttpHeaders implements JSONObjectSupplier, JSONStringifyStrict
{
    private static final long            serialVersionUID       = 1L;

    public static final String           DEFAULT_USER_AGENT     = String.format("The-Modern-Way/2.2.2 (Language=Java/%s)", System.getProperty("java.version"));

    public static final MediaType        XML_MEDIA_TYPE         = MediaType.APPLICATION_XML;

    public static final MediaType        JSON_MEDIA_TYPE        = MediaType.APPLICATION_JSON;

    public static final MediaType        JSON_MEDIA_TYPE_UTF8   = MediaType.APPLICATION_JSON_UTF8;

    public static final MediaType        YAML_MEDIA_TYPE        = MediaType.valueOf(IHTTPConstants.CONTENT_TYPE_APPLICATION_YAML);

    public static final MediaType        PROPERTIES_MEDIA_TYPE  = MediaType.valueOf(IHTTPConstants.CONTENT_TYPE_TEXT_PROPERTIES);

    private static final List<Charset>   JSON_ACCEPT_CHARSET    = CommonOps.toList(IO.UTF_8_CHARSET);

    private static final List<MediaType> JSON_ACCEPT_MEDIA_TYPE = CommonOps.toList(JSON_MEDIA_TYPE, JSON_MEDIA_TYPE_UTF8);

    public HTTPHeaders(final HttpHeaders head)
    {
        addAll(head);
    }

    public HTTPHeaders()
    {
    }

    public HTTPHeaders(final HttpServletRequest request)
    {
        this(new ServletServerHttpRequest(request).getHeaders());
    }

    public HTTPHeaders setHttpServletResponse(final HttpServletResponse response)
    {
        try (ServletServerHttpResponse resp = new ServletServerHttpResponse(response))
        {
            resp.getHeaders().addAll(this);
        }
        return this;
    }

    public HTTPHeaders setIfAccept()
    {
        if (null == get(ACCEPT))
        {
            setAccept();
        }
        return this;
    }

    public HTTPHeaders setIfAcceptCharset()
    {
        if (null == get(ACCEPT_CHARSET))
        {
            setAcceptCharset();
        }
        return this;
    }

    public HTTPHeaders setIfContentType()
    {
        if (null == get(CONTENT_TYPE))
        {
            setContentType();
        }
        return this;
    }

    public HTTPHeaders setIfUserAgent(final String ua)
    {
        if (null == get(USER_AGENT))
        {
            setUserAgent(ua);
        }
        return this;
    }

    public HTTPHeaders setAccept()
    {
        setAccept(JSON_ACCEPT_MEDIA_TYPE);

        return this;
    }

    public HTTPHeaders setAcceptCharset()
    {
        setAcceptCharset(JSON_ACCEPT_CHARSET);

        return this;
    }

    public HTTPHeaders setContentType()
    {
        setContentType(JSON_MEDIA_TYPE_UTF8);

        return this;
    }

    public HTTPHeaders setUserAgent(final String ua)
    {
        return reset(USER_AGENT, StringOps.toTrimOrElse(ua, DEFAULT_USER_AGENT));
    }

    public HTTPHeaders append(final HttpHeaders head)
    {
        super.addAll(head);

        return this;
    }

    public HTTPHeaders append(String name, final String valu)
    {
        name = StringOps.requireTrimOrNull(name);

        super.add(name, valu);

        return this;
    }

    public HTTPHeaders append(String name, final String... collection)
    {
        name = StringOps.requireTrimOrNull(name);

        final List<String> list = StringOps.toUnique(collection);

        if ((null != list) && (false == list.isEmpty()))
        {
            super.addAll(name, list);
        }
        return this;
    }

    public HTTPHeaders append(String name, final Collection<String> collection)
    {
        name = StringOps.requireTrimOrNull(name);

        final List<String> list = StringOps.toUnique(collection);

        if ((null != list) && (false == list.isEmpty()))
        {
            super.addAll(name, list);
        }
        return this;
    }

    public HTTPHeaders reset(String name, final String valu)
    {
        name = StringOps.requireTrimOrNull(name);

        super.set(name, valu);

        return this;
    }

    public HTTPHeaders reset(final Map<String, String> values)
    {
        super.setAll(CommonOps.requireNonNull(values));

        return this;
    }

    @Override
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

    @Override
    public String toJSONString()
    {
        return toJSONString(false);
    }

    @Override
    public String toJSONString(final boolean strict)
    {
        return toJSONObject().toJSONString(strict);
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }
}
