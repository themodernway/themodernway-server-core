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

package com.themodernway.server.core.support.spring.network.websocket;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface IWebSocketServiceProvider extends Closeable
{
    public List<String> getWebSocketServiceNames();

    public IWebSocketService getWebSocketService(String name);

    public IWebSocketServiceSession getWebSocketServiceSession(String id);

    public List<IWebSocketServiceSession> getWebSocketServiceSessions();

    public boolean addWebSocketServiceSession(IWebSocketServiceSession session);

    public boolean removeWebSocketServiceSession(IWebSocketServiceSession session);

    public List<IWebSocketServiceSession> findSessions(Predicate<IWebSocketServiceSession> predicate);

    public List<IWebSocketServiceSession> findSessionsByIdentifiers(Collection<String> want);

    public List<IWebSocketServiceSession> findSessionsByServiceNames(Collection<String> want);

    public List<IWebSocketServiceSession> findSessionsByPathParameters(Map<String, String> want);

    public List<IWebSocketServiceSession> findSessionsByPathParameters(Map<String, String> want, boolean some);
}