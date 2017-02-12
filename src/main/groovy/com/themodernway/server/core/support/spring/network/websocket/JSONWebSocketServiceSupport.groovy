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

package com.themodernway.server.core.support.spring.network.websocket

import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.parser.JSONParser

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public abstract class JSONWebSocketServiceSupport extends WebSocketServiceSupport implements IJSONWebSocketService
{
    protected JSONWebSocketServiceSupport()
    {
    }

    @Override
    public void onMessage(final IWebSocketServiceContext context, final String text, final boolean last) throws Exception
    {
        onMessage(context, getJSONParser().parse(text))
    }

    @Override
    public JSONParser getJSONParser()
    {
        new JSONParser()
    }

    @Memoized
    public boolean isText()
    {
        false
    }

    abstract public void onMessage(IWebSocketServiceContext context, JSONObject json) throws Exception
}
