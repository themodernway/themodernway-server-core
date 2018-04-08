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

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class CoreNativeHttpRequestFactory extends HttpComponentsClientHttpRequestFactory
{
    public CoreNativeHttpRequestFactory()
    {
        this(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 8);
    }

    public CoreNativeHttpRequestFactory(final int route, final int total)
    {
        this(HttpClientBuilder.create().setMaxConnPerRoute(route).setMaxConnTotal(total));
    }

    public CoreNativeHttpRequestFactory(final HttpClientBuilder builder)
    {
        this(builder.build());
    }

    public CoreNativeHttpRequestFactory(final HttpClient client)
    {
        super(client);

        setBufferRequestBody(false);
    }
}
