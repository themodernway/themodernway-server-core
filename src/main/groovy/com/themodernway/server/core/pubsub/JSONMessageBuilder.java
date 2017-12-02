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

package com.themodernway.server.core.pubsub;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;

public class JSONMessageBuilder
{
    protected JSONMessageBuilder()
    {
    }

    public static final Message<JSONObject> createMessage(final JSONObject payload, final MessageHeaders headers)
    {
        return MessageBuilder.createMessage(CommonOps.requireNonNull(payload), CommonOps.requireNonNull(headers));
    }

    public static final Message<JSONObject> createMessage(final JSONObject payload)
    {
        return createMessage(CommonOps.requireNonNull(payload), new JSONObject());
    }

    public static final Message<JSONObject> createMessage(final Map<String, ?> payload)
    {
        return createMessage(new JSONObject(CommonOps.requireNonNull(payload)));
    }

    public static final Message<JSONObject> createMessage(final Map<String, ?> payload, final Map<String, ?> headers)
    {
        return createMessage(new JSONObject(CommonOps.requireNonNull(payload)), CommonOps.requireNonNull(headers));
    }

    public static final Message<JSONObject> createMessage(final Map<String, ?> payload, final MessageHeaders headers)
    {
        return createMessage(new JSONObject(CommonOps.requireNonNull(payload)), CommonOps.requireNonNull(headers));
    }

    public static final Message<JSONObject> createMessage(final JSONObject payload, final JSONObject headers)
    {
        return createMessage(CommonOps.requireNonNull(payload), new MessageHeaders(CommonOps.requireNonNull(headers)));
    }

    public static final Message<JSONObject> createMessage(final JSONObject payload, final Map<String, ?> headers)
    {
        return createMessage(CommonOps.requireNonNull(payload), new JSONObject(CommonOps.requireNonNull(headers)));
    }

    public static final MessageBuilder<JSONObject> fromMessage(final Message<JSONObject> message)
    {
        return MessageBuilder.fromMessage(CommonOps.requireNonNull(message));
    }

    public static final MessageBuilder<JSONObject> withPayload(final JSONObject payload)
    {
        return MessageBuilder.withPayload(CommonOps.requireNonNull(payload));
    }

    public static final MessageBuilder<JSONObject> withPayload(final Map<String, ?> payload)
    {
        return MessageBuilder.withPayload(new JSONObject(CommonOps.requireNonNull(payload)));
    }
}
