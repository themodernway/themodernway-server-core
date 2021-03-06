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

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.themodernway.server.core.json.JSONObject;

public class SimpleJSONServerSessionRepository extends AbstractServerSessionRepository
{
    private static final long                               serialVersionUID = 1L;

    private final ConcurrentHashMap<String, IServerSession> m_sessions       = new ConcurrentHashMap<>();

    public SimpleJSONServerSessionRepository()
    {
    }

    public SimpleJSONServerSessionRepository(final IServerSessionHelper helper)
    {
        super(helper);
    }

    @Override
    public void save(final IServerSession session)
    {
        final String id = session.getId();

        final String od = session.getOriginalId();

        if (false == id.equals(od))
        {
            m_sessions.remove(od);

            session.setOriginalId(id);
        }
        m_sessions.put(id, session);
    }

    @Override
    public void deleteById(final String id)
    {
        m_sessions.remove(id);
    }

    @Override
    public void cleanExpiredSessions()
    {
        final HashSet<String> dead = new HashSet<>();

        for (final IServerSession session : m_sessions.values())
        {
            if (session.isExpired())
            {
                dead.add(session.getId());
            }
        }
        for (final String id : dead)
        {
            deleteById(id);
        }
    }

    @Override
    public IServerSession createSession()
    {
        return createSession(new JSONObject());
    }

    @Override
    public IServerSession createSession(final Map<String, ?> keys)
    {
        return new SimpleJSONServerSession(keys, this);
    }

    @Override
    public IServerSession findById(final String id)
    {
        final IServerSession sess = m_sessions.get(id);

        if ((null != sess) && (sess.isExpired()))
        {
            deleteById(sess.getId());

            return null;
        }
        return sess;
    }

    @Override
    public void close() throws IOException
    {
        m_sessions.clear();
    }
}
