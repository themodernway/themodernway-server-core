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

package com.themodernway.server.core.security.saml;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialContextSet;
import org.opensaml.xml.security.credential.UsageType;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.IRefreshable;
import com.themodernway.server.core.ICoreCommon;

public abstract class AbstractCredential implements Credential, ICoreCommon, IRefreshable
{
    private SecretKey                  m_secretkey;

    private PublicKey                  m_publickey;

    private PrivateKey                 m_privatkey;

    private final String               m_credalias;

    private String                     m_entity_id;

    private UsageType                  m_usagetype = UsageType.UNSPECIFIED;

    private final CredentialContextSet m_crcontext = new CredentialContextSet();

    private final Logger               m_haslogger = Logger.getLogger(getClass());

    protected AbstractCredential(final String alias)
    {
        m_credalias = StringOps.requireTrimOrNull(alias);
    }

    public String getCredentialsAlias()
    {
        return m_credalias;
    }

    @Override
    public String getEntityId()
    {
        return m_entity_id;
    }

    public void setEntityId(final String id)
    {
        m_entity_id = toTrimOrNull(id);
    }

    @Override
    public UsageType getUsageType()
    {
        return m_usagetype;
    }

    public void setUsageType(final UsageType type)
    {
        m_usagetype = requireNonNullOrElse(type, UsageType.UNSPECIFIED);
    }

    @Override
    public CredentialContextSet getCredentalContextSet()
    {
        return m_crcontext;
    }

    public void setPublicKey(final PublicKey key)
    {
        m_publickey = key;
    }

    @Override
    public PublicKey getPublicKey()
    {
        return m_publickey;
    }

    public void setSecretKey(final SecretKey key)
    {
        m_secretkey = key;
    }

    @Override
    public SecretKey getSecretKey()
    {
        return m_secretkey;
    }

    public void setPrivateKey(final PrivateKey key)
    {
        m_privatkey = key;
    }

    @Override
    public PrivateKey getPrivateKey()
    {
        return m_privatkey;
    }

    @Override
    public Collection<String> getKeyNames()
    {
        return emptyList();
    }

    @Override
    public Class<? extends Credential> getCredentialType()
    {
        return Credential.class;
    }

    @Override
    public Logger logger()
    {
        return m_haslogger;
    }
}
