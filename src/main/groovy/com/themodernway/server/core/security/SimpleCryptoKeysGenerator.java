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

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.security.tools.Hashing;
import com.themodernway.server.core.security.tools.ICheckSum;
import com.themodernway.server.core.security.tools.Randoms;

public class SimpleCryptoKeysGenerator implements ICryptoKeysGenerator
{
    private static final SimpleCryptoKeysGenerator INSTANCE = new SimpleCryptoKeysGenerator();

    private static final ICheckSum                 CRC_HASH = Hashing.crc32();

    public static final SimpleCryptoKeysGenerator getCryptoKeysGenerator()
    {
        return INSTANCE;
    }

    public SimpleCryptoKeysGenerator()
    {
    }

    @Override
    public String getRandomPass()
    {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 11; i++)
        {
            builder.append(Randoms.Secure.getString(8)).append(StringOps.MINUS_STRING);
        }
        return builder.append(CRC_HASH.tohex(builder.toString())).toString();
    }

    @Override
    public String getRandomSalt()
    {
        return SimpleHexEncoder.get().encode(Randoms.Secure.nextBytes(32));
    }

    @Override
    public boolean isPassValid(final String pass)
    {
        final int last = pass.lastIndexOf(StringOps.MINUS_STRING);

        if (last <= 0)
        {
            return false;
        }
        return pass.endsWith(CRC_HASH.tohex(pass.substring(0, last + 1)));
    }
}
