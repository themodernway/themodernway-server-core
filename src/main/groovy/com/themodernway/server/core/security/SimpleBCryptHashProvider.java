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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class SimpleBCryptHashProvider implements IBCryptHashProvider
{
    private final BCryptPasswordEncoder m_bcrypt;

    public SimpleBCryptHashProvider()
    {
        this(DEFAULT_BCRYPT_ITERATION);
    }

    public SimpleBCryptHashProvider(final int iteration)
    {
        m_bcrypt = new BCryptPasswordEncoder(iteration);
    }

    @Override
    public final String encode(final CharSequence text)
    {
        return m_bcrypt.encode(text);
    }

    @Override
    public final synchronized boolean matches(final CharSequence text, final String value)
    {
        return m_bcrypt.matches(text, value);
    }
}
