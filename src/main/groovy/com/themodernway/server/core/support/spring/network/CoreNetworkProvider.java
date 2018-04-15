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
import java.util.function.Function;

import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.cache.AbstractConcurrentCache;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreNetworkProvider implements ICoreNetworkProvider, IHasLogging, InitializingBean
{
    public static final String             APACHE_FACTORY_NAME = "apache";

    public static final String             OKHTTP_FACTORY_NAME = "okhttp";

    public static final String             SIMPLE_FACTORY_NAME = "simple";

    public static final String             NATIVE_FACTORY_NAME = "native";

    private String                         m_user_agent        = HTTPHeaders.DEFAULT_USER_AGENT;

    private final Logger                   m_has_logger        = LoggingOps.getLogger(getClass());

    private final HTTPHeaders              m_no_headers        = new HTTPHeaders().setAccept().setUserAgent(m_user_agent);

    private final RestTemplate             m_rest_execs        = new RestTemplate(CommonOps.toList(new CoreJSONHttpMessageConverter()));

    private final CoreFactoryCache         m_fact_cache        = new CoreFactoryCache(m_has_logger);

    private final DefaultUriBuilderFactory m_urlhandler        = new DefaultUriBuilderFactory();

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
            // empty by design.
        }
    }

    protected static final class CoreFactoryCache extends AbstractConcurrentCache<ClientHttpRequestFactory> implements IHasLogging
    {
        private final Logger m_has_logger;

        public CoreFactoryCache(final Logger logger)
        {
            this("CoreFactoryCache", logger);
        }

        public CoreFactoryCache(final String named, final Logger logger)
        {
            super(named);

            m_has_logger = logger;
        }

        @Override
        public Function<String, ClientHttpRequestFactory> getMappingFunction()
        {
            return name -> {

                switch (StringOps.toTrimOrElse(name, NATIVE_FACTORY_NAME).toLowerCase())
                {
                    case APACHE_FACTORY_NAME:
                        final HttpComponentsClientHttpRequestFactory apache = new HttpComponentsClientHttpRequestFactory();
                        apache.setBufferRequestBody(false);
                        return apache;
                    case OKHTTP_FACTORY_NAME:
                        return new OkHttp3ClientHttpRequestFactory();
                    case SIMPLE_FACTORY_NAME:
                        final SimpleClientHttpRequestFactory simple = new SimpleClientHttpRequestFactory();
                        simple.setBufferRequestBody(false);
                        return simple;
                    case NATIVE_FACTORY_NAME:
                        return new CoreNativeHttpRequestFactory();
                    default:
                        try
                        {
                            final Class<?> type = Class.forName(name);

                            if ((null != type) && (ClientHttpRequestFactory.class.isAssignableFrom(type)))
                            {
                                return CommonOps.CAST(type.newInstance());
                            }
                            else if (logger().isErrorEnabled())
                            {
                                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("ERROR: can not create (%s) as ClientHttpRequestFactory.", name));
                            }
                        }
                        catch (final Exception e)
                        {
                            if (logger().isErrorEnabled())
                            {
                                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("ERROR: can not create (%s) as ClientHttpRequestFactory.", name), e);
                            }
                        }
                        return null;
                }
            };
        }

        @Override
        public void close() throws IOException
        {
            for (final ClientHttpRequestFactory factory : values())
            {
                if (factory instanceof DisposableBean)
                {
                    if (logger().isInfoEnabled())
                    {
                        logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("close(%s).", factory.getClass().getName()));
                    }
                    try
                    {
                        ((DisposableBean) factory).destroy();
                    }
                    catch (final Exception e)
                    {
                        if (logger().isErrorEnabled())
                        {
                            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "close().", e);
                        }
                    }
                }
                else if (factory instanceof Closeable)
                {
                    if (logger().isInfoEnabled())
                    {
                        logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("close(%s).", factory.getClass().getName()));
                    }
                    try
                    {
                        ((Closeable) factory).close();
                    }
                    catch (final Exception e)
                    {
                        if (logger().isErrorEnabled())
                        {
                            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "close().", e);
                        }
                    }
                }
            }
            clear();
        }

        @Override
        public Logger logger()
        {
            return m_has_logger;
        }
    }

    public CoreNetworkProvider()
    {
        m_rest_execs.setUriTemplateHandler(m_urlhandler);

        m_rest_execs.setErrorHandler(new CoreResponseErrorHandler());
    }

    @Override
    public void setParsePath(final boolean parse)
    {
        m_urlhandler.setParsePath(parse);
    }

    @Override
    public void setStrictEncoding(final boolean strict)
    {
        m_urlhandler.setEncodingMode(strict ? EncodingMode.URI_COMPONENT : EncodingMode.NONE);
    }

    @Override
    public void setHttpFactoryByName(final String name)
    {
        final String impl = StringOps.toTrimOrElse(name, NATIVE_FACTORY_NAME);

        final ClientHttpRequestFactory factory = m_fact_cache.get(impl);

        if (null != factory)
        {
            m_rest_execs.setRequestFactory(factory);

            if (logger().isInfoEnabled())
            {
                logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("setHttpFactoryByName(%s).", factory.getClass().getName()));
            }
        }
        else if (logger().isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("setHttpFactoryByName(%s) not found.", impl));
        }
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

        m_no_headers.setUserAgent(m_user_agent);
    }

    @Override
    public void close() throws IOException
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "close().");
        }
        m_fact_cache.close();
    }

    @Override
    public IRESTResponse get(final String path)
    {
        return exec(path, HttpMethod.GET, null, null, null);
    }

    @Override
    public IRESTResponse get(final String path, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.GET, null, null, headers);
    }

    @Override
    public IRESTResponse get(final String path, final PathParameters params)
    {
        return exec(path, HttpMethod.GET, null, params, null);
    }

    @Override
    public IRESTResponse get(final String path, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.GET, null, params, headers);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request)
    {
        return exec(path, HttpMethod.PUT, request, null, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PUT, request, null, headers);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final PathParameters params)
    {
        return exec(path, HttpMethod.PUT, request, params, null);
    }

    @Override
    public IRESTResponse put(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PUT, request, params, headers);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request)
    {
        return exec(path, HttpMethod.POST, request, null, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.POST, request, null, headers);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final PathParameters params)
    {
        return exec(path, HttpMethod.POST, request, params, null);
    }

    @Override
    public IRESTResponse post(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.POST, request, params, headers);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request)
    {
        return exec(path, HttpMethod.PATCH, request, null, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PATCH, request, null, headers);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final PathParameters params)
    {
        return exec(path, HttpMethod.PATCH, request, params, null);
    }

    @Override
    public IRESTResponse patch(final String path, final JSONObject request, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.PATCH, request, params, headers);
    }

    @Override
    public IRESTResponse delete(final String path)
    {
        return exec(path, HttpMethod.DELETE, null, null, null);
    }

    @Override
    public IRESTResponse delete(final String path, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.DELETE, null, null, headers);
    }

    @Override
    public IRESTResponse delete(final String path, final PathParameters params)
    {
        return exec(path, HttpMethod.DELETE, null, params, null);
    }

    @Override
    public IRESTResponse delete(final String path, final PathParameters params, final HTTPHeaders headers)
    {
        return exec(path, HttpMethod.DELETE, null, params, headers);
    }

    protected IRESTResponse exec(final String path, final HttpMethod method, final JSONObject request, final PathParameters params, HTTPHeaders headers)
    {
        if (null == headers)
        {
            headers = m_no_headers;
        }
        else
        {
            headers.setIfAccept().setIfUserAgent(getUserAgent());
        }
        try
        {
            return new CoreRESTResponse(m_rest_execs.exchange(path, method, new HttpEntity<>(request, headers), JSONObject.class, PathParameters.parameters(params)));
        }
        catch (final Exception e)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("ERROR: method(%s) url(%s) headers(%s).", method, m_rest_execs.getUriTemplateHandler().expand(path, PathParameters.parameters(params)).toString(), headers), e);
            }
            final HTTPHeaders keep = new HTTPHeaders(headers);

            return new CoreRESTResponse(HttpStatus.INTERNAL_SERVER_ERROR, new JSONObject().set("error", e.getMessage()), () -> keep);
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
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "start().");
        }
    }
}
