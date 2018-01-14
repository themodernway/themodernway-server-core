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

import java.io.Closeable;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.servlet.ICoreServletConstants;

public class CoreNetworkProvider implements ICoreNetworkProvider, IHasLogging, InitializingBean
{
    private String                          m_user_agent = HTTPHeaders.DEFAULT_USER_AGENT;

    private final Logger                    m_has_logger = Logger.getLogger(getClass());

    private final HTTPHeaders               m_no_headers = new HTTPHeaders();

    private final RestTemplate              m_rest_execs = new RestTemplate();

    private final DefaultUriTemplateHandler m_urlhandler = new DefaultUriTemplateHandler();

    private static final PathParameters     EMPTY_PARAMS = new PathParameters();

    private static final IBinder            VALUE_MAPPER = BinderType.JSON.getBinder();

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
        m_no_headers.doRESTHeaders(getUserAgent());

        m_rest_execs.setUriTemplateHandler(m_urlhandler);

        m_rest_execs.setErrorHandler(new CoreResponseErrorHandler());
    }

    public void setParsePath(final boolean parse)
    {
        m_urlhandler.setParsePath(parse);
    }

    public void setStrictEncoding(final boolean strict)
    {
        m_urlhandler.setStrictEncoding(strict);
    }

    public void setHttpFactoryByName(final String name)
    {
        final String impl = StringOps.toTrimOrElse(name, ICoreServletConstants.STRING_DEFAULT);

        switch (impl.toLowerCase())
        {
            case "apache":
                setClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory());
                break;
            case "simple":
                setClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
                break;
            case ICoreServletConstants.STRING_DEFAULT:
                setClientHttpRequestFactory(new CoreClientHttpRequestFactory());
                break;
            default:
                try
                {
                    final Class<?> type = Class.forName(impl);

                    if ((null != type) && (ClientHttpRequestFactory.class.isAssignableFrom(type)))
                    {
                        setClientHttpRequestFactory(CommonOps.CAST(type.newInstance()));
                    }
                    else
                    {
                        logger().error(String.format("ERROR: can not create (%s) as ClientHttpRequestFactory.", impl));
                    }
                }
                catch (final Exception e)
                {
                    logger().error(String.format("ERROR: can not create (%s) as ClientHttpRequestFactory.", impl), e);
                }
                break;
        }
    }

    @Override
    public void setClientHttpRequestFactory(final ClientHttpRequestFactory factory)
    {
        m_rest_execs.setRequestFactory(CommonOps.requireNonNull(factory));

        logger().info(String.format("setClientHttpRequestFactory(%s).", factory.getClass().getName()));
    }

    @Override
    public String getUserAgent()
    {
        return m_user_agent;
    }

    @Override
    public void setUserAgent(final String agent)
    {
        m_user_agent = StringOps.toTrimOrElse(agent, HTTPHeaders.DEFAULT_USER_AGENT);
    }

    @Override
    public void close() throws IOException
    {
        logger().info("close().");

        final ClientHttpRequestFactory factory = m_rest_execs.getRequestFactory();

        if (factory instanceof DisposableBean)
        {
            logger().info(String.format("close(%s).", factory.getClass().getName()));

            try
            {
                ((DisposableBean) factory).destroy();
            }
            catch (final Exception e)
            {
                logger().error("close().", e);
            }
        }
        else if (factory instanceof Closeable)
        {
            logger().info(String.format("close(%s).", factory.getClass().getName()));

            try
            {
                ((Closeable) factory).close();
            }
            catch (final Exception e)
            {
                logger().error("close().", e);
            }
        }
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
    public IRESTResponse put(final String path, final JSONObject request)
    {
        return exec(path, HttpMethod.PUT, request, null, null, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PUT, request, null, headers, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final PathParameters params)
    {
        return exec(path, HttpMethod.PUT, request, params, null, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PUT, request, params, headers, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, request, null, null, builder);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, request, null, headers, builder);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, request, params, null, builder);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PUT, request, params, headers, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request)
    {
        return exec(path, HttpMethod.POST, request, null, null, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.POST, request, null, headers, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final PathParameters params)
    {
        return exec(path, HttpMethod.POST, request, params, null, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.POST, request, params, headers, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, request, null, null, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, request, null, headers, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, request, params, null, builder);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.POST, request, params, headers, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request)
    {
        return exec(path, HttpMethod.PATCH, request, null, null, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PATCH, request, null, headers, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final PathParameters params)
    {
        return exec(path, HttpMethod.PATCH, request, params, null, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PATCH, request, params, headers, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, request, null, null, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, request, null, headers, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final PathParameters params, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, request, params, null, builder);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        return exec(path, HttpMethod.PATCH, request, params, headers, builder);
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

    protected IRESTResponse exec(final String path, final HttpMethod method, final JSONObject request, final PathParameters params, HTTPHeaders headers, final IRestTemplateBuilder builder)
    {
        final String curl = StringOps.requireTrimOrNull(path);

        if (null == headers)
        {
            headers = m_no_headers;
        }
        else
        {
            headers.doRESTHeaders(getUserAgent());
        }
        final String mapped = getMappedValue(request, method, headers);

        final HttpEntity<String> entity = (null == mapped) ? new HttpEntity<String>(headers) : new HttpEntity<String>(mapped, headers);

        final RestTemplate template = (null == builder) ? m_rest_execs : builder.build(m_rest_execs);

        if (logger().isDebugEnabled())
        {
            logger().debug(String.format("DEBUG: method(%s) url(%s) headers(%s).", method, template.getUriTemplateHandler().expand(curl, CommonOps.requireNonNullOrElse(params, EMPTY_PARAMS)).toString(), headers));
        }
        try
        {
            return new CoreRESTResponse(this, template.exchange(curl, method, entity, String.class, CommonOps.requireNonNullOrElse(params, EMPTY_PARAMS)));
        }
        catch (final Exception e)
        {
            logger().error(String.format("ERROR: method(%s) url(%s) headers(%s).", method, template.getUriTemplateHandler().expand(curl, CommonOps.requireNonNullOrElse(params, EMPTY_PARAMS)).toString(), headers), e);

            final HTTPHeaders keep = new HTTPHeaders(headers);

            return new CoreRESTResponse(this, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), () -> keep);
        }
    }

    protected String getMappedValue(final Object request, final HttpMethod method, final HTTPHeaders headers)
    {
        if (null == request)
        {
            return null;
        }
        if (null == headers.getContentType())
        {
            headers.setContentType(HTTPHeaders.JSON_MEDIA_TYPE);
        }
        try
        {
            return VALUE_MAPPER.toString(request);
        }
        catch (final ParserException e)
        {
            logger().error("getMappedValue()", e);

            return null;
        }
    }

    @Override
    public Logger logger()
    {
        return m_has_logger;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        logger().info("start().");
    }
}
