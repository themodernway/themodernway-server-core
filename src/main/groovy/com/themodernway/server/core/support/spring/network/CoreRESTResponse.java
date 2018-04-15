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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.themodernway.server.core.json.JSONObject;

public class CoreRESTResponse implements IRESTResponse
{
    private final HttpStatus            m_stat;

    private final JSONObject            m_json;

    private final Supplier<HTTPHeaders> m_head;

    public CoreRESTResponse(final ResponseEntity<JSONObject> resp)
    {
        this(resp.getStatusCode(), (resp.hasBody() ? resp.getBody() : null), () -> new HTTPHeaders(resp.getHeaders()));
    }

    public CoreRESTResponse(final HttpStatus stat, final JSONObject body, final Supplier<HTTPHeaders> head)
    {
        m_stat = stat;

        m_json = body;

        m_head = head;
    }

    @Override
    public int code()
    {
        return m_stat.value();
    }

    @Override
    public JSONObject json()
    {
        return m_json;
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
}