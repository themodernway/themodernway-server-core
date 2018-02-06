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

package com.themodernway.server.core.support.spring.network.websocket;

import java.io.IOException;
import java.util.Map;

import javax.websocket.Session;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;

public class WebSocketServiceContext implements IWebSocketServiceContext
{
    private final Session           m_session;

    private final JSONObject        m_attribs;

    private final IWebSocketService m_service;

    public WebSocketServiceContext(final Session session, final IWebSocketService service)
    {
        this(new JSONObject(), session, service);
    }

    public WebSocketServiceContext(final JSONObject attribs, final Session session, final IWebSocketService service)
    {
        m_session = CommonOps.requireNonNull(session);

        m_service = CommonOps.requireNonNull(service);

        m_attribs = CommonOps.requireNonNull(attribs).merge(m_session.getPathParameters()).merge(m_service.getAttributes());
    }

    @Override
    public IWebSocketService getService()
    {
        return m_service;
    }

    @Override
    public void close() throws IOException
    {
        final Session session = getSession();

        if (session.isOpen())
        {
            session.close();
        }
    }

    @Override
    public Session getSession()
    {
        return m_session;
    }

    @Override
    public boolean isOpen()
    {
        return getSession().isOpen();
    }

    @Override
    public String getPathParameter(final String name)
    {
        return getPathParameters().get(StringOps.requireTrimOrNull(name));
    }

    @Override
    public Map<String, String> getPathParameters()
    {
        return getSession().getPathParameters();
    }

    @Override
    public void reply(final String text) throws IOException
    {
        reply(CommonOps.requireNonNull(text), true);
    }

    @Override
    public void reply(final String text, final boolean last) throws IOException
    {
        final Session sess = getSession();

        synchronized (sess)
        {
            sess.getBasicRemote().sendText(CommonOps.requireNonNull(text), last);
        }
    }

    @Override
    public void reply(final JSONObject json) throws IOException
    {
        reply(json.toJSONString());
    }

    @Override
    public void reply(final JSONArray batch) throws IOException
    {
        reply(batch.toJSONString());
    }

    @Override
    public void batch(final JSONObject json) throws IOException
    {
        reply(json.toJSONString());
    }

    @Override
    public String getId()
    {
        return getSession().getId();
    }

    @Override
    public JSONObject getAttributes()
    {
        return m_attribs;
    }
}