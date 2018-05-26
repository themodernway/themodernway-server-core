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

package com.themodernway.server.core.security;

import java.io.IOException;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.AbstractCoreLoggingBase;
import com.themodernway.server.core.logging.LoggingOps;

public final class CryptoProvider extends AbstractCoreLoggingBase implements ICryptoProvider
{
    private final AESStringCryptoProvider   m_pcrypt;

    private final SimpleBCryptHashProvider  m_bcrypt;

    private final SimpleSHA512HashProvider  m_hasher;

    private final SimpleCryptoKeysGenerator m_keygen;

    public CryptoProvider(final CharSequence pass, final CharSequence salt)
    {
        this(pass, salt, DEFAULT_BCRYPT_ITERATION);
    }

    public CryptoProvider(final CharSequence pass, final CharSequence salt, final int iteration)
    {
        m_hasher = new SimpleSHA512HashProvider();

        m_bcrypt = new SimpleBCryptHashProvider(iteration);

        m_pcrypt = new AESStringCryptoProvider(pass, salt);

        m_keygen = SimpleCryptoKeysGenerator.getCryptoKeysGenerator();
    }

    @Override
    public final String getRandomPass()
    {
        return m_keygen.getRandomPass();
    }

    @Override
    public final String getRandomSalt()
    {
        return m_keygen.getRandomSalt();
    }

    @Override
    public final boolean isPassValid(final CharSequence pass)
    {
        return m_keygen.isPassValid(pass);
    }

    @Override
    public final String encode(final CharSequence text)
    {
        return m_bcrypt.encode(text);
    }

    @Override
    public final String encrypt(final CharSequence text)
    {
        return m_pcrypt.encrypt(text);
    }

    @Override
    public final String decrypt(final CharSequence text)
    {
        return m_pcrypt.decrypt(text);
    }

    @Override
    public final boolean matches(final CharSequence text, final String value)
    {
        return m_bcrypt.matches(CommonOps.requireNonNull(text), CommonOps.requireNonNull(value));
    }

    @Override
    public final String sha512(final CharSequence text, final String salt)
    {
        return m_hasher.sha512(CommonOps.requireNonNull(text), salt);
    }

    @Override
    public final String sha512(final CharSequence text, final String salt, final int iter)
    {
        return m_hasher.sha512(CommonOps.requireNonNull(text), salt, iter);
    }

    @Override
    public final String sha512(final CharSequence text, final int iter)
    {
        return m_hasher.sha512(CommonOps.requireNonNull(text), null, iter);
    }

    @Override
    public final String sha512(final CharSequence text)
    {
        return m_hasher.sha512(CommonOps.requireNonNull(text));
    }

    @Override
    public final void close() throws IOException
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "close().");
        }
    }
}
