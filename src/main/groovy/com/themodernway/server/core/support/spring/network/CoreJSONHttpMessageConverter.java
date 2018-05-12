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

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;

public class CoreJSONHttpMessageConverter extends AbstractHttpMessageConverter<JSONObject>
{
    private final IBinder      m_binder = BinderType.JSON.getBinder();

    private final ObjectReader m_reader = m_binder.getMapper().readerFor(JSONObject.class);

    private final ObjectWriter m_sender = m_binder.getMapper().writerFor(JSONObject.class);

    public CoreJSONHttpMessageConverter()
    {
        super(IO.UTF_8_CHARSET, MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON);
    }

    @Override
    protected boolean supports(final Class<?> claz)
    {
        return claz == JSONObject.class;
    }

    @Override
    protected JSONObject readInternal(final Class<? extends JSONObject> claz, final HttpInputMessage message) throws IOException
    {
        return m_reader.readValue(message.getBody());
    }

    @Override
    protected void writeInternal(final JSONObject json, final HttpOutputMessage message) throws IOException
    {
        new HTTPHeaders(message.getHeaders()).setIfAccept().setIfAcceptCharset().setIfContentType();

        m_sender.writeValue(message.getBody(), json);
    }
}
