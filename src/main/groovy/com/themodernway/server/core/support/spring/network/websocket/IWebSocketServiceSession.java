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

package com.themodernway.server.core.support.spring.network.websocket;

import java.io.Closeable;
import java.util.Map;

import javax.websocket.Session;

import com.themodernway.common.api.types.IIdentified;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;

public interface IWebSocketServiceSession extends IIdentified, Closeable
{
    public boolean isOpen();

    public Session getSession();

    public String getPathParameter(String name);

    public boolean isStrict();

    public JSONObject getAttributes();

    public Map<String, String> getPathParameters();

    public IWebSocketService getService();

    public void reply(String text) throws Exception;

    public void reply(String text, boolean last) throws Exception;

    public void reply(JSONObject json) throws Exception;

    public void reply(JSONArray batch) throws Exception;

    public void batch(JSONObject json) throws Exception;
}