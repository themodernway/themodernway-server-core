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

import org.springframework.security.crypto.password.PasswordEncoder;

import com.themodernway.common.api.hash.IHasher;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.security.tools.CheckSums;
import com.themodernway.server.core.security.tools.ICheckSum;

public class CorePasswordEncoder implements PasswordEncoder
{
    private final int             m_iters;

    private final ICheckSum       m_crc32;

    private final ICryptoProvider m_crypt;

    public CorePasswordEncoder(final ICryptoProvider crypt)
    {
        this(crypt, IHasher.SHA512_ITERATIONS);
    }

    public CorePasswordEncoder(final ICryptoProvider crypt, final int iters)
    {
        m_iters = iters;

        m_crc32 = CheckSums.crc32();

        m_crypt = CommonOps.requireNonNull(crypt, "CryptoProvider was null.");
    }

    @Override
    public String encode(final CharSequence password)
    {
        final String stringpw = password.toString();

        return m_crypt.encrypt(m_crypt.encode(m_crypt.sha512(stringpw, m_crc32.tohex(stringpw), m_iters)));
    }

    @Override
    public boolean matches(final CharSequence password, final String encoded)
    {
        final String stringpw = password.toString();

        return m_crypt.matches(m_crypt.sha512(stringpw, m_crc32.tohex(stringpw), m_iters), m_crypt.decrypt(encoded));
    }
}
