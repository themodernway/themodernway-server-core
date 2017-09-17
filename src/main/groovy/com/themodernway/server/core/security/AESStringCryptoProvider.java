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

package com.themodernway.server.core.security;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.themodernway.common.api.java.util.CommonOps;

public final class AESStringCryptoProvider implements IStringCryptoProvider
{
    private final TextEncryptor m_pcrypt;

    public AESStringCryptoProvider(final String pass, final String salt)
    {
        m_pcrypt = Encryptors.text(CommonOps.requireNonNull(pass), CommonOps.requireNonNull(salt));
    }

    @Override
    public final String encrypt(final String text)
    {
        return m_pcrypt.encrypt(CommonOps.requireNonNull(text));
    }

    @Override
    public final String decrypt(final String text)
    {
        return m_pcrypt.decrypt(CommonOps.requireNonNull(text));
    }
}
