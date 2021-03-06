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

import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;

public class CoreSimpleMailMessage extends SimpleMailMessage implements ICoreSimpleMailMessage
{
    private static final long     serialVersionUID = 1L;

    private transient IMailSender m_send;

    public CoreSimpleMailMessage()
    {
        m_send = CommonOps.NULL();
    }

    public CoreSimpleMailMessage(final IMailSender send)
    {
        m_send = send;
    }

    public CoreSimpleMailMessage(final SimpleMailMessage original)
    {
        super(CommonOps.requireNonNull(original));

        m_send = CommonOps.NULL();
    }

    public CoreSimpleMailMessage(final SimpleMailMessage original, final IMailSender send)
    {
        super(CommonOps.requireNonNull(original));

        m_send = send;
    }

    @Override
    public void setMailToList(final String list)
    {
        StringOps.setConsumerUniqueStringArray(list, this::setTo);
    }

    @Override
    public void setMailToList(final List<String> list)
    {
        StringOps.setConsumerUniqueStringArray(list, this::setTo);
    }

    @Override
    public List<String> getMailToList()
    {
        return StringOps.getSupplierUniqueStringArray(this::getTo);
    }

    @Override
    public void setMailCcList(final String list)
    {
        StringOps.setConsumerUniqueStringArray(list, this::setCc);
    }

    @Override
    public void setMailCcList(final List<String> list)
    {
        StringOps.setConsumerUniqueStringArray(list, this::setCc);
    }

    @Override
    public List<String> getMailCcList()
    {
        return StringOps.getSupplierUniqueStringArray(this::getCc);
    }

    @Override
    public void setMailBccList(final String list)
    {
        StringOps.setConsumerUniqueStringArray(list, this::setBcc);
    }

    @Override
    public void setMailBccList(final List<String> list)
    {
        StringOps.setConsumerUniqueStringArray(list, this::setBcc);
    }

    @Override
    public List<String> getMailBccList()
    {
        return StringOps.getSupplierUniqueStringArray(this::getBcc);
    }

    public JSONObject toJSONObject()
    {
        final JSONObject json = new JSONObject();

        json.set("from", getFrom());

        json.set("to", getMailToList());

        json.set("cc", getMailCcList());

        json.set("bcc", getMailBccList());

        json.set("replyTo", getReplyTo());

        json.set("subject", getSubject());

        json.set("sentDate", getSentDate());

        json.set("text", getText());

        return json;
    }

    @Override
    public String toJSONString()
    {
        return toJSONObject().toJSONString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(final Object other)
    {
        return super.equals(other);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public void send() throws MailException
    {
        if (null == m_send)
        {
            throw new MailSendException("no mail sender.");
        }
        m_send.send(this);
    }
}
