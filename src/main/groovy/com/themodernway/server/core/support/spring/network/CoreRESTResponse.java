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

package com.themodernway.server.core.support.spring.network;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;

import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.parser.JSONParser;

public class CoreRESTResponse implements IRESTResponse
{
    private static final Logger        logger = Logger.getLogger(CoreRESTResponse.class);

    private final int                  m_code;

    private final String               m_body;

    private final HTTPHeaders          m_head;

    private final ICoreNetworkProvider m_prov;

    private JSONObject                 m_json;

    public CoreRESTResponse(ICoreNetworkProvider prov, final ResponseEntity<String> resp)
    {
        this(prov, resp.getStatusCode().value(), (resp.hasBody() ? resp.getBody() : null), new HTTPHeaders(Collections.unmodifiableMap(resp.getHeaders())));
    }

    public CoreRESTResponse(ICoreNetworkProvider prov, final int code, final String body, final HTTPHeaders head)
    {
        m_code = code;

        m_body = body;

        m_head = head;

        m_prov = prov;
    }

    @Override
    public int code()
    {
        return m_code;
    }

    @Override
    public String body()
    {
        return m_body;
    }

    @Override
    public synchronized JSONObject json()
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
            return (m_json = (new JSONParser().parse(body)));
        }
        catch (ParserException e)
        {
            logger.error("Error parsing JSON", e);
        }
        return null;
    }

    @Override
    public HTTPHeaders headers()
    {
        return m_head;
    }

    @Override
    public boolean good()
    {
        return network().isGoodCode(code());
    }

    @Override
    public ICoreNetworkProvider network()
    {
        return m_prov;
    }
}