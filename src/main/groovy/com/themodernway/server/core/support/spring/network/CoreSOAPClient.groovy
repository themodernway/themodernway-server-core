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

import groovy.transform.CompileStatic
import wslite.soap.SOAPClient

@CompileStatic
public class CoreSOAPClient implements ISOAPClient
{
    private final SOAPClient m_client

    public CoreSOAPClient(final SOAPClient client)
    {
        m_client = CommonOps.requireNonNull(client)
    }

    @Override
    public ISOAPResponse send(final Closure<?> content)
    {
        new CoreSOAPResponse(m_client.send(CommonOps.requireNonNull(content)))
    }

    @Override
    public ISOAPResponse send(final Map<String, ?> params, final Closure<?> content)
    {
        new CoreSOAPResponse(m_client.send(CommonOps.requireNonNull(params), CommonOps.requireNonNull(content)))
    }

    @Override
    public ISOAPResponse send(final String content)
    {
        new CoreSOAPResponse(m_client.send(CommonOps.requireNonNull(content)))
    }

    @Override
    public ISOAPResponse send(final Map<String, ?> params, final String content)
    {
        new CoreSOAPResponse(m_client.send(CommonOps.requireNonNull(params), CommonOps.requireNonNull(content)))
    }
}
