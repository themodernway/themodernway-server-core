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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import com.fasterxml.jackson.dataformat.yaml.UTF8Reader;
import com.fasterxml.jackson.dataformat.yaml.UTF8Writer;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreJSONHttpMessageConverter extends AbstractHttpMessageConverter<JSONObject>
{
    private static final IBinder         BINDER = BinderType.JSON.getBinder();

    private static final List<Charset>   ACCEPT = Arrays.asList(IO.UTF_8_CHARSET);

    private static final List<MediaType> MEDIAT = Arrays.asList(MediaType.APPLICATION_JSON_UTF8);

    private static final Logger          LOGGER = LoggingOps.getLogger(CoreJSONHttpMessageConverter.class);

    public CoreJSONHttpMessageConverter()
    {
        super(IO.UTF_8_CHARSET, MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_PLAIN, MediaType.ALL);
    }

    @Override
    protected boolean supports(final Class<?> claz)
    {
        return claz == JSONObject.class;
    }

    @Override
    protected JSONObject readInternal(final Class<? extends JSONObject> claz, final HttpInputMessage message) throws IOException
    {
        try
        {
            return BINDER.bindJSON(new UTF8Reader(message.getBody(), false));
        }
        catch (final ParserException e)
        {
            if (LOGGER.isErrorEnabled())
            {
                LOGGER.error(LoggingOps.THE_MODERN_WAY_MARKER, "bind().", e);
            }
            throw new IOException("bind", e);
        }
    }

    @Override
    protected void writeInternal(final JSONObject json, final HttpOutputMessage message) throws IOException
    {
        final HttpHeaders head = message.getHeaders();

        head.setAccept(MEDIAT);

        head.setAcceptCharset(ACCEPT);

        head.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try
        {
            BINDER.send(new UTF8Writer(message.getBody()), json);
        }
        catch (final ParserException e)
        {
            if (LOGGER.isErrorEnabled())
            {
                LOGGER.error(LoggingOps.THE_MODERN_WAY_MARKER, "send().", e);
            }
            throw new IOException("send", e);
        }
    }
}
