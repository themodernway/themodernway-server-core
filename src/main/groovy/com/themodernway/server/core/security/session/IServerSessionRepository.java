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

package com.themodernway.server.core.security.session;

import java.io.Closeable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.session.SessionRepository;

public interface IServerSessionRepository extends SessionRepository<IServerSession>, Serializable, Closeable
{
    public String getRealm();

    public boolean touch(String id);

    public boolean touch(IServerSession session);

    public boolean delete(IServerSession session);

    public void cleanExpiredSessions();

    public IServerSession createSession(Map<String, ?> keys);

    public List<String> getDefaultRoles();

    public IServerSessionHelper getHelper();
}
