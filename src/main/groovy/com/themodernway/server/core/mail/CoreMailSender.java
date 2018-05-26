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

package com.themodernway.server.core.mail;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreMailSender extends JavaMailSenderImpl implements IMailSender, IHasLogging
{
    private CoreSimpleMailMessage m_message;

    private final Logger          m_logging = LoggingOps.getLogger(getClass());

    public CoreMailSender()
    {
    }

    public CoreMailSender(final Properties properties)
    {
        setJavaMailProperties(properties);
    }

    public CoreMailSender(final Resource resource) throws IOException
    {
        this(IO.toProperties(resource));
    }

    @Override
    public CoreSimpleMailMessage getTemplateMailMessage()
    {
        return m_message;
    }

    @Override
    public void setTemplateMailMessage(final CoreSimpleMailMessage template)
    {
        m_message = template;
    }

    @Override
    public ISimpleMailMessageBuilder builder()
    {
        return new SimpleMailMessageBuilderDefault(this);
    }

    @Override
    public ISimpleMailMessageBuilder builder(final SimpleMailMessage original)
    {
        if (null == original)
        {
            return new SimpleMailMessageBuilderDefault(this);
        }
        return new SimpleMailMessageBuilderDefault(original, this);
    }

    @Override
    public void close() throws IOException
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "close().");
        }
    }

    @Override
    public Logger logger()
    {
        return m_logging;
    }

    protected static class SimpleMailMessageBuilderDefault implements ISimpleMailMessageBuilder
    {
        private final CoreSimpleMailMessage m_message;

        public SimpleMailMessageBuilderDefault(final IMailSender sender)
        {
            m_message = new CoreSimpleMailMessage(sender);
        }

        public SimpleMailMessageBuilderDefault(final SimpleMailMessage original, final IMailSender sender)
        {
            m_message = new CoreSimpleMailMessage(original, sender);
        }

        @Override
        public ISimpleMailMessageBuilder to(final List<String> list)
        {
            m_message.setMailToList(list);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder cc(final List<String> list)
        {
            m_message.setMailCcList(list);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder bcc(final List<String> list)
        {
            m_message.setMailBccList(list);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder date(final Date valu)
        {
            m_message.setSentDate(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder from(final String valu)
        {
            m_message.setFrom(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder text(final String valu)
        {
            m_message.setText(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder reply(final String valu)
        {
            m_message.setReplyTo(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder subject(final String valu)
        {
            m_message.setSubject(valu);

            return this;
        }

        @Override
        public CoreSimpleMailMessage build()
        {
            return m_message;
        }
    }
}
