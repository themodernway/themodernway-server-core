/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;

import wslite.soap.SOAPClient;

public class CoreNetworkProvider implements ICoreNetworkProvider
{
    private String                                m_user_agent = HTTPHeaders.DEFAULT_USER_AGENT;

    private final HTTPHeaders                     m_no_headers = new HTTPHeaders();

    private final RestTemplate                    m_rest_execs = new RestTemplate();

    private final DefaultUriTemplateHandler       m_urlhandler = new DefaultUriTemplateHandler();

    private static final PathParameters           EMPTY_PARAMS = new PathParameters();

    private static final CoreResponseErrorHandler NO_ERRORS_CB = new CoreResponseErrorHandler();

    private static final class CoreResponseErrorHandler implements ResponseErrorHandler
    {
        @Override
        public boolean hasError(final ClientHttpResponse response) throws IOException
        {
            return false;
        }

        @Override
        public void handleError(final ClientHttpResponse response) throws IOException
        {
        }
    }

    public CoreNetworkProvider()
    {
        m_rest_execs.setErrorHandler(NO_ERRORS_CB);

        m_rest_execs.setUriTemplateHandler(m_urlhandler);

        m_no_headers.doRESTHeaders(getDefaultUserAgent());
    }

    protected IRestTemplateBuilder getDefaultIRestTemplateBuilder()
    {
        return (final DefaultUriTemplateHandler handler) -> m_rest_execs;
    }

    public void setParsePath(final boolean parse)
    {
        m_urlhandler.setParsePath(parse);
    }

    public void setStrictEncoding(final boolean strict)
    {
        m_urlhandler.setStrictEncoding(strict);
    }

    public void setUseHttpComponents(final boolean http)
    {
        if (http)
        {
            m_rest_execs.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        }
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public IRESTResponse get(final String path)
    {
        return exec(path, HttpMethod.GET, null, null, null, null);
    }

    @Override
    public IRESTResponse get(final String path, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.GET, null, null, headers, null);
    }

    @Override
    public IRESTResponse get(final String path, final PathParameters params)
    {
        return exec(path, HttpMethod.GET, null, params, null, null);
    }

    @Override
    public IRESTResponse get(final String path, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.GET, null, params, headers, null);
    }

    @Override
    public IRESTResponse get(final String path, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.GET, null, null, null, builder);
    }

    @Override
    public IRESTResponse get(final String path, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.GET, null, null, headers, builder);
    }

    @Override
    public IRESTResponse get(final String path, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.GET, null, params, null, builder);
    }

    @Override
    public IRESTResponse get(final String path, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.GET, null, params, headers, builder);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body)
    {
        return exec(path, HttpMethod.PUT, body, null, null, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PUT, body, null, headers, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body, final PathParameters params)
    {
        return exec(path, HttpMethod.PUT, body, params, null, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PUT, body, params, headers, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, body, null, null, builder);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, body, null, headers, builder);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, body, params, null, builder);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject body, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, body, params, headers, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body)
    {
        return exec(path, HttpMethod.POST, body, null, null, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.POST, body, null, headers, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body, final PathParameters params)
    {
        return exec(path, HttpMethod.POST, body, params, null, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.POST, body, params, headers, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, body, null, null, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, body, null, headers, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, body, params, null, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject body, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, body, params, headers, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body)
    {
        return exec(path, HttpMethod.PATCH, body, null, null, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PATCH, body, null, headers, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body, final PathParameters params)
    {
        return exec(path, HttpMethod.PATCH, body, params, null, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PATCH, body, params, headers, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, body, null, null, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, body, null, headers, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, body, params, null, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject body, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, body, params, headers, builder);
    }

    @Override
    public IRESTResponse delete(final String path)
    {
        return exec(path, HttpMethod.DELETE, null, null, null, null);
    }

    @Override
    public IRESTResponse delete(final String path, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.DELETE, null, null, headers, null);
    }

    @Override
    public IRESTResponse delete(final String path, final PathParameters params)
    {
        return exec(path, HttpMethod.DELETE, null, params, null, null);
    }

    @Override
    public IRESTResponse delete(final String path, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.DELETE, null, params, headers, null);
    }

    @Override
    public IRESTResponse delete(final String path, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.DELETE, null, null, null, builder);
    }

    @Override
    public IRESTResponse delete(final String path, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.DELETE, null, null, headers, builder);
    }

    @Override
    public IRESTResponse delete(final String path, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.DELETE, null, params, null, builder);
    }

    @Override
    public IRESTResponse delete(final String path, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.DELETE, null, params, headers, builder);
    }

    protected IRESTResponse exec(final String path, final HttpMethod method, final JSONObject body, PathParameters params, HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        final String curl = StringOps.requireTrimOrNull(path);

        if (null == headers)
        {
            headers = m_no_headers;
        }
        else
        {
            headers.doRESTHeaders(getDefaultUserAgent());
        }
        final HttpEntity<String> entity = (null == body) ? new HttpEntity<String>(headers) : new HttpEntity<String>(body.toJSONString(), headers);

        if (null == params)
        {
            params = EMPTY_PARAMS;
        }
        try
        {
            return new CoreRESTResponse(this, CommonOps.requireNonNullOrElse(builder, getDefaultIRestTemplateBuilder()).build(m_urlhandler).exchange(curl, method, entity, String.class, params));
        }
        catch (final Exception e)
        {
            final HTTPHeaders keep = new HTTPHeaders(headers);

            return new CoreRESTResponse(this, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), () -> keep);
        }
    }

    @Override
    public ISOAPClient soap(final String path)
    {
        return new CoreSOAPClient(new SOAPClient(StringOps.requireTrimOrNull(path)));
    }

    @Override
    public String getDefaultUserAgent()
    {
        return m_user_agent;
    }

    @Override
    public void setDefaultUserAgent(final String agent)
    {
        m_user_agent = agent;
    }

    @Override
    public boolean isGoodCode(final int code)
    {
        return HttpStatus.valueOf(code).is2xxSuccessful();
    }
}
