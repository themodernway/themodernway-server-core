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

import com.themodernway.common.api.hash.Hasher;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.security.tools.Digests;

public final class SimpleSHA512HashProvider implements ISHA512HashProvider
{
    private final Hasher m_hasher = new Hasher(this);

    public SimpleSHA512HashProvider()
    {
    }

    @Override
    public String sha512(final String text)
    {
        return SimpleHexEncoder.get().encode(Digests.sha512().digest(text.getBytes(IO.UTF_8_CHARSET)));
    }

    @Override
    public String sha512(final String text, final String salt)
    {
        return m_hasher.sha512(CommonOps.requireNonNull(text), CommonOps.requireNonNull(salt));
    }

    @Override
    public String sha512(final String text, final String salt, final int iter)
    {
        return m_hasher.sha512(CommonOps.requireNonNull(text), salt, iter);
    }

    @Override
    public String sha512(final String text, final int iter)
    {
        return m_hasher.sha512(CommonOps.requireNonNull(text), null, iter);
    }
}
