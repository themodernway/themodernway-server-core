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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.themodernway.server.core.logging.ICoreLoggingOperations;

@ManagedResource
public class CorePublishSubscribeLoggingService implements ICoreLoggingOperations
{
    private final Logger m_logger = Logger.getLogger(getClass());

    public CorePublishSubscribeLoggingService(final PublishSubscribeChannel channel)
    {
        channel.subscribe(new MessageHandler()
        {
            @Override
            public void handleMessage(final Message<?> message) throws MessagingException
            {
                Level level = getLoggingLevel();

                if (null != level)
                {
                    if (Level.OFF.equals(level))
                    {
                        return;
                    }
                    if (null != message)
                    {
                        final Object look = message.getHeaders().get(CORE_LOGGING_OPERATIONS_KEY);

                        if (null != look)
                        {
                            if (look instanceof Level)
                            {
                                level = ((Level) look);
                            }
                            else if (look instanceof String)
                            {
                                level = Level.toLevel(look.toString(), level);
                            }
                            if (Level.OFF.equals(level))
                            {
                                return;
                            }
                        }

                        /*
                         * Converting a JSONObject to a String MAY be an expensive operation if
                         * logging is not enabled at this level.
                         */

                        if (LogManager.getRootLogger().isEnabledFor(level))
                        {
                            final Object payload = message.getPayload();

                            if (null != payload)
                            {
                                m_logger.log(level, payload.toString());
                            }
                        }
                    }
                }
            }
        });
        m_logger.setLevel(Level.INFO);
    }

    @Override
    public Level getLoggingLevel()
    {
        final Level level = m_logger.getLevel();

        if (null != level)
        {
            return level;
        }
        m_logger.setLevel(Level.INFO);

        return m_logger.getLevel();
    }

    @Override
    public void setLoggingLevel(final Level level)
    {
        if (null != level)
        {
            m_logger.setLevel(level);
        }
    }

    @Override
    @ManagedAttribute(description = "Get Log4j Level.")
    public String getLoggingLevelAsString()
    {
        final Level level = getLoggingLevel();

        if (null != level)
        {
            return level.toString();
        }
        return "";
    }

    @Override
    @ManagedAttribute(description = "Set Log4j Level.")
    public void setLoggingLevelAsString(final String level)
    {
        setLoggingLevel(Level.toLevel(level, Level.OFF));
    }
}
