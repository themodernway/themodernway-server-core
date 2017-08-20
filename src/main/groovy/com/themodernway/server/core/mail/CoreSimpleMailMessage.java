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

package com.themodernway.server.core.mail;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;

import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.json.JSONObject;

@SuppressWarnings("serial")
public class CoreSimpleMailMessage extends SimpleMailMessage implements ICoreSimpleMailMessage
{
    private final IMailSender m_send;

    public CoreSimpleMailMessage()
    {
        m_send = null;
    }

    public CoreSimpleMailMessage(final IMailSender send)
    {
        m_send = send;
    }

    public CoreSimpleMailMessage(final SimpleMailMessage original)
    {
        super(Objects.requireNonNull(original));

        m_send = null;
    }

    public CoreSimpleMailMessage(final SimpleMailMessage original, final IMailSender send)
    {
        super(Objects.requireNonNull(original));

        m_send = send;
    }

    @Override
    public void setMailToList(final String list)
    {
        ICoreCommon.setConsumerUniqueStringArray(list, this::setTo);
    }

    @Override
    public void setMailToList(final Collection<String> list)
    {
        ICoreCommon.setConsumerUniqueStringArray(list, this::setTo);
    }

    @Override
    public List<String> getMailToList()
    {
        return ICoreCommon.getSupplierUniqueStringArray(this::getTo);
    }

    @Override
    public void setMailCcList(final String list)
    {
        ICoreCommon.setConsumerUniqueStringArray(list, this::setCc);
    }

    @Override
    public void setMailCcList(final Collection<String> list)
    {
        ICoreCommon.setConsumerUniqueStringArray(list, this::setCc);
    }

    @Override
    public List<String> getMailCcList()
    {
        return ICoreCommon.getSupplierUniqueStringArray(this::getCc);
    }

    @Override
    public void setMailBccList(final String list)
    {
        ICoreCommon.setConsumerUniqueStringArray(list, this::setBcc);
    }

    @Override
    public void setMailBccList(final Collection<String> list)
    {
        ICoreCommon.setConsumerUniqueStringArray(list, this::setBcc);
    }

    @Override
    public List<String> getMailBccList()
    {
        return ICoreCommon.getSupplierUniqueStringArray(this::getBcc);
    }

    @Override
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
