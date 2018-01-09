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

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.crypto.SecretKey;

import org.opensaml.xml.security.x509.X509Credential;

import com.themodernway.common.api.java.util.CommonOps;

public class KeyStoreX509Credential extends AbstractCredential implements X509Credential
{
    private final KeyStore m_keystore;

    private final char[]   m_password;

    private final char[]   m_keyspass;

    public KeyStoreX509Credential(final KeyStore keystore, final String alias, final String password, final String keyspass)
    {
        this(keystore, alias, password.toCharArray(), keyspass.toCharArray());
    }

    public KeyStoreX509Credential(final KeyStore keystore, final String alias, final char[] password, final char[] keyspass)
    {
        super(alias);

        m_password = CommonOps.requireNonNull(password);

        m_keyspass = CommonOps.requireNonNull(keyspass);

        m_keystore = CommonOps.requireNonNull(keystore);

        refresh();
    }

    @Override
    public void refresh()
    {
        try
        {
            setPublicKey(getKeyStore().getCertificate(getCredentialsAlias()).getPublicKey());
        }
        catch (final Exception e)
        {
            logger().error(format("error accessing PublicKey in keystore alias(%s).", getCredentialsAlias()), e);
        }
        try
        {
            setPrivateKey((PrivateKey) getKeyStore().getKey(getCredentialsAlias(), m_password));
        }
        catch (final Exception e)
        {
            logger().error(format("error accessing PrivateKey in keystore alias(%s).", getCredentialsAlias()), e);
        }
        try
        {
            setSecretKey((SecretKey) getKeyStore().getKey(getCredentialsAlias(), m_keyspass));
        }
        catch (final Exception e)
        {
            logger().error(format("error accessing SecretKey in keystore alias(%s).", getCredentialsAlias()), e);
        }
    }

    protected KeyStore getKeyStore()
    {
        return m_keystore;
    }

    @Override
    public X509Certificate getEntityCertificate()
    {
        try
        {
            return (X509Certificate) getKeyStore().getCertificate(getCredentialsAlias());
        }
        catch (final Exception e)
        {
            logger().error(format("error accessing certificate in keystore alias(%s).", getCredentialsAlias()), e);

            return null;
        }
    }

    @Override
    public Collection<X509Certificate> getEntityCertificateChain()
    {
        final List<X509Certificate> list = arrayList();

        try
        {
            for (final Certificate cert : getKeyStore().getCertificateChain(getCredentialsAlias()))
            {
                list.add((X509Certificate) cert);
            }
        }
        catch (final Exception e)
        {
            logger().error(format("error accessing certificate chain in keystore alias(%s).", getCredentialsAlias()), e);
        }
        return list;
    }

    @Override
    public Collection<X509CRL> getCRLs()
    {
        return emptyList();
    }
}
