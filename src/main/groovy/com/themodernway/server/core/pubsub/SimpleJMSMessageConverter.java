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

package com.themodernway.server.core.pubsub;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.ObjectUtils;

import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.BinderType;

public class SimpleJMSMessageConverter extends SimpleMessageConverter
{
    public SimpleJMSMessageConverter()
    {
    }

    @Override
    public Message toMessage(final Object object, final Session session) throws JMSException, MessageConversionException
    {
        if (object instanceof JSONObject)
        {
            return session.createTextMessage(object.toString());
        }
        if (object instanceof org.springframework.messaging.Message)
        {
            final Object payload = ((org.springframework.messaging.Message<?>) object).getPayload();

            if (payload instanceof JSONObject)
            {
                return session.createTextMessage(payload.toString());
            }
            throw new MessageConversionException("Can't convert payload of type [" + ObjectUtils.nullSafeClassName(payload) + "] to JSON text for JMS");
        }
        throw new MessageConversionException("Can't convert object of type [" + ObjectUtils.nullSafeClassName(object) + "] to JSON text for JMS");
    }

    @Override
    public Object fromMessage(final Message message) throws JMSException, MessageConversionException
    {
        if (message instanceof TextMessage)
        {
            try
            {
                return BinderType.JSON.getBinder().bindJSON(((TextMessage) message).getText());
            }
            catch (final ParserException e)
            {
                throw new MessageConversionException("Error parsing JSON", e);
            }
        }
        return super.fromMessage(message);
    }
}
