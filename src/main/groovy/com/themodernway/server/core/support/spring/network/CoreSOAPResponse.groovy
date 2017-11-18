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

package com.themodernway.server.core.support.spring.network

import com.themodernway.common.api.java.util.CommonOps

import groovy.util.slurpersupport.GPathResult
import wslite.soap.SOAPResponse

public class CoreSOAPResponse implements ISOAPResponse
{
    private int m_code = 400

    private GPathResult m_body

    private HTTPHeaders m_head

    public CoreSOAPResponse(final SOAPResponse resp)
    {
        final Map<String, List<String>> make = [:]

        if (resp) {
            if (resp.getHttpResponse()) {
                m_code = resp.getHttpResponse().getStatusCode()
            }
            m_body = resp.getBody()

            resp.httpResponse.headers.each { k, v ->
                if (false == k.toString().trim().isEmpty()) {
                    if (v instanceof List) {
                        make[k] = v
                    } else if (v instanceof String) {
                        make[k] = [v]
                    }
                }
            }
        }
        m_head = new HTTPHeaders(CommonOps.toUnmodifiableMap(make))
    }

    @Override
    public int code()
    {
        m_code
    }

    @Override
    public GPathResult body()
    {
        m_body
    }

    @Override
    public HTTPHeaders headers()
    {
        m_head
    }
}
