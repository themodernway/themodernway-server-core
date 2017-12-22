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

import java.io.Closeable;

import org.springframework.http.client.ClientHttpRequestFactory;

import com.themodernway.server.core.json.JSONObject;

public interface ICoreNetworkProvider extends Closeable
{
    public String getDefaultUserAgent();

    public void setDefaultUserAgent(String agent);

    public void setClientHttpRequestFactory(ClientHttpRequestFactory factory);

    public IRESTResponse get(String path);

    public IRESTResponse get(String path, HTTPHeaders headers);

    public IRESTResponse get(String path, PathParameters params);

    public IRESTResponse get(String path, PathParameters params, HTTPHeaders headers);

    public IRESTResponse get(String path, IRestTemplateBuilder builder);

    public IRESTResponse get(String path, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse get(String path, PathParameters params, IRestTemplateBuilder builder);

    public IRESTResponse get(String path, PathParameters params, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse put(String path, JSONObject body);

    public IRESTResponse put(String path, JSONObject body, HTTPHeaders headers);

    public IRESTResponse put(String path, JSONObject body, PathParameters params);

    public IRESTResponse put(String path, JSONObject body, PathParameters params, HTTPHeaders headers);

    public IRESTResponse put(String path, JSONObject body, IRestTemplateBuilder builder);

    public IRESTResponse put(String path, JSONObject body, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse put(String path, JSONObject body, PathParameters params, IRestTemplateBuilder builder);

    public IRESTResponse put(String path, JSONObject body, PathParameters params, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse post(String path, JSONObject body);

    public IRESTResponse post(String path, JSONObject body, HTTPHeaders headers);

    public IRESTResponse post(String path, JSONObject body, PathParameters params);

    public IRESTResponse post(String path, JSONObject body, PathParameters params, HTTPHeaders headers);

    public IRESTResponse post(String path, JSONObject body, IRestTemplateBuilder builder);

    public IRESTResponse post(String path, JSONObject body, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse post(String path, JSONObject body, PathParameters params, IRestTemplateBuilder builder);

    public IRESTResponse post(String path, JSONObject body, PathParameters params, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse patch(String path, JSONObject body);

    public IRESTResponse patch(String path, JSONObject body, HTTPHeaders headers);

    public IRESTResponse patch(String path, JSONObject body, PathParameters params);

    public IRESTResponse patch(String path, JSONObject body, PathParameters params, HTTPHeaders headers);

    public IRESTResponse patch(String path, JSONObject body, IRestTemplateBuilder builder);

    public IRESTResponse patch(String path, JSONObject body, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse patch(String path, JSONObject body, PathParameters params, IRestTemplateBuilder builder);

    public IRESTResponse patch(String path, JSONObject body, PathParameters params, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse delete(String path);

    public IRESTResponse delete(String path, HTTPHeaders headers);

    public IRESTResponse delete(String path, PathParameters params);

    public IRESTResponse delete(String path, PathParameters params, HTTPHeaders headers);

    public IRESTResponse delete(String path, IRestTemplateBuilder builder);

    public IRESTResponse delete(String path, HTTPHeaders headers, IRestTemplateBuilder builder);

    public IRESTResponse delete(String path, PathParameters params, IRestTemplateBuilder builder);

    public IRESTResponse delete(String path, PathParameters params, HTTPHeaders headers, IRestTemplateBuilder builder);
}
