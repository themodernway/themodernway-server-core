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
import java.util.Collections;
import java.util.List;
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
import org.springframework.http.converter.HttpMessageConverter;
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
    private String                              m_user_agent        = HTTPHeaders.DEFAULT_USER_AGENT;

    private final Logger                        m_has_logger        = LoggingOps.getLogger(getClass());

    private final HTTPHeaders                   m_no_headers        = new HTTPHeaders();

    private final RestTemplate                  m_rest_execs        = new RestTemplate();

    private final CoreFactoryCache              m_fact_cache        = new CoreFactoryCache();

    private final DefaultUriBuilderFactory      m_urlhandler        = new DefaultUriBuilderFactory();

    private final List<HttpMessageConverter<?>> m_converters        = CommonOps.arrayList(new CoreJSONHttpMessageConverter());

    private static final String                 APACHE_FACTORY_NAME = "apache";

    private static final String                 OKHTTP_FACTORY_NAME = "okhttp";

    private static final String                 SIMPLE_FACTORY_NAME = "simple";

    private static final String                 NATIVE_FACTORY_NAME = "native";

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
        private final Logger m_has_logger = LoggingOps.getLogger(getClass());

        public CoreFactoryCache()
        {
            super("CoreFactoryCache");
        }

        public CoreFactoryCache(final String named)
        {
            super(named);
        }

        @Override
        public Function<String, ClientHttpRequestFactory> getMappingFunction()
        {
            return name -> {

                switch (StringOps.toTrimOrElse(name, NATIVE_FACTORY_NAME).toLowerCase())
                {
                    case APACHE_FACTORY_NAME:
                        return new HttpComponentsClientHttpRequestFactory();
                    case OKHTTP_FACTORY_NAME:
                        return new OkHttp3ClientHttpRequestFactory();
                    case SIMPLE_FACTORY_NAME:
                        return new SimpleClientHttpRequestFactory();
                    case NATIVE_FACTORY_NAME:
                        return new CoreClientHttpRequestFactory();
                    default:
                        try
                        {
                            final Class<?> type = Class.forName(name);

                            if ((null != type) && (ClientHttpRequestFactory.class.isAssignableFrom(type)))
                            {
                                return CommonOps.cast(type.newInstance());
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
        m_no_headers.doRESTHeaders(getUserAgent());

        m_rest_execs.setUriTemplateHandler(m_urlhandler);

        m_rest_execs.setErrorHandler(new CoreResponseErrorHandler());

        m_converters.addAll(m_rest_execs.getMessageConverters());

        m_rest_execs.setMessageConverters(m_converters);
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
        final String curl = CommonOps.requireNonNullOrElse(path, StringOps.EMPTY_STRING);

        if (null == headers)
        {
            headers = m_no_headers;
        }
        else
        {
            headers.doRESTHeaders(getUserAgent());
        }
        final HttpEntity<JSONObject> entity = (null == request) ? new HttpEntity<>(headers) : new HttpEntity<>(request, headers);

        final RestTemplate template = (null == builder) ? m_rest_execs : builder.build(m_rest_execs);

        try
        {
            if ((null == params) || (params.isEmpty()))
            {
                return new CoreRESTResponse(this, template.exchange(curl, method, entity, JSONObject.class));
            }
            else
            {
                return new CoreRESTResponse(this, template.exchange(curl, method, entity, JSONObject.class, params));
            }
        }
        catch (final Exception e)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("ERROR: method(%s) url(%s) headers(%s).", method, template.getUriTemplateHandler().expand(curl, CommonOps.requireNonNullOrElse(params, Collections.emptyMap())).toString(), headers), e);
            }
            final HTTPHeaders keep = new HTTPHeaders(headers);

            return new CoreRESTResponse(this, HttpStatus.INTERNAL_SERVER_ERROR, new JSONObject(), () -> keep);
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
