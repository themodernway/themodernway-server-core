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

import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.limiting.IRateLimited;

public interface IWebSocketService extends IRateLimited, INamed, Closeable
{
    public void onMessage(IWebSocketServiceContext context, String text, boolean last) throws Exception;
    
    public JSONObject getAttributes();
    
    public boolean isText();
}
