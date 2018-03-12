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

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreRESTResponse implements IRESTResponse
{
    private static final Logger         logger = LoggingOps.getLogger(CoreRESTResponse.class);

    private static final IBinder        BINDER = BinderType.JSON.getBinder();

    private final String                m_body;

    private final Supplier<HTTPHeaders> m_head;

    private final HttpStatus            m_stat;

    private final ICoreNetworkProvider  m_prov;

    private JSONObject                  m_json;

    public CoreRESTResponse(final ICoreNetworkProvider prov, final ResponseEntity<String> resp)
    {
        this(prov, resp.getStatusCode(), (resp.hasBody() ? resp.getBody() : null), () -> new HTTPHeaders(resp.getHeaders()));
    }

    public CoreRESTResponse(final ICoreNetworkProvider prov, final HttpStatus stat, final String body, final Supplier<HTTPHeaders> head)
    {
        m_prov = prov;

        m_stat = stat;

        m_body = body;

        m_head = head;
    }

    @Override
    public int code()
    {
        return m_stat.value();
    }

    @Override
    public String body()
    {
        return m_body;
    }

    @Override
    public JSONObject json()
    {
        if (null != m_json)
        {
            return m_json;
        }
        try
        {
            final String body = body();

            if ((null == body) || (body.isEmpty()))
            {
                return null;
            }
            m_json = BINDER.bindJSON(body);

            return m_json;
        }
        catch (final ParserException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "Error parsing JSON", e);
            }
        }
        return null;
    }

    @Override
    public HTTPHeaders headers()
    {
        return m_head.get();
    }

    @Override
    public boolean good()
    {
        return m_stat.is2xxSuccessful();
    }

    @Override
    public ICoreNetworkProvider network()
    {
        return m_prov;
    }
}