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

import javax.activation.FileTypeMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.themodernway.server.core.file.FileAndPathUtils;
import com.themodernway.server.core.file.ICoreContentTypeMapper;
import com.themodernway.server.core.io.IO;

public class CoreMailSender extends JavaMailSenderImpl implements IMailSender, InitializingBean
{
    private ICoreContentTypeMapper m_ctmapper;

    private CoreSimpleMailMessage  m_template;

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
        return m_template;
    }

    @Override
    public void setTemplateMailMessage(final CoreSimpleMailMessage template)
    {
        m_template = template;
    }

    @Override
    public void setContentTypeMapper(final ICoreContentTypeMapper ctmapper)
    {
        if (null != ctmapper)
        {
            final FileTypeMap fmap = ctmapper.getFileTypeMap();

            if (null != fmap)
            {
                m_ctmapper = ctmapper;

                setDefaultFileTypeMap(fmap);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (null == m_ctmapper)
        {
            setContentTypeMapper(FileAndPathUtils.CORE_MIMETYPE_MAPPER);
        }
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
        // empty by design.
    }

    protected static class SimpleMailMessageBuilderDefault implements ISimpleMailMessageBuilder
    {
        private final CoreSimpleMailMessage m_mess;

        public SimpleMailMessageBuilderDefault(final IMailSender sender)
        {
            m_mess = new CoreSimpleMailMessage(sender);
        }

        public SimpleMailMessageBuilderDefault(final SimpleMailMessage original, final IMailSender sender)
        {
            m_mess = new CoreSimpleMailMessage(original, sender);
        }

        @Override
        public ISimpleMailMessageBuilder to(final List<String> list)
        {
            m_mess.setMailToList(list);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder cc(final List<String> list)
        {
            m_mess.setMailCcList(list);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder bcc(final List<String> list)
        {
            m_mess.setMailBccList(list);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder date(final Date valu)
        {
            m_mess.setSentDate(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder from(final String valu)
        {
            m_mess.setFrom(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder text(final String valu)
        {
            m_mess.setText(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder reply(final String valu)
        {
            m_mess.setReplyTo(valu);

            return this;
        }

        @Override
        public ISimpleMailMessageBuilder subject(final String valu)
        {
            m_mess.setSubject(valu);

            return this;
        }

        @Override
        public ICoreSimpleMailMessage make()
        {
            return m_mess;
        }
    }
}
