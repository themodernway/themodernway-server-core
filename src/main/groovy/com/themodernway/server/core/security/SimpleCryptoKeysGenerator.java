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

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.zip.CRC32;

import org.apache.commons.lang.RandomStringUtils;

import com.themodernway.server.core.io.IO;

public class SimpleCryptoKeysGenerator implements ICryptoKeysGenerator
{
    private static final SimpleCryptoKeysGenerator INSTANCE = new SimpleCryptoKeysGenerator();

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
            builder.append(Tools.randomString(8)).append("-");
        }
        return builder.append(Tools.checksumOfChars(builder)).toString();
    }

    @Override
    public String getRandomSalt()
    {
        return SimpleHexEncoder.get().encode(Tools.randomBytes(32));
    }

    @Override
    public boolean isPassValid(final String pass)
    {
        final int last = pass.lastIndexOf("-");

        if (last <= 0)
        {
            return false;
        }
        return pass.endsWith(Tools.checksumOfChars(pass.substring(0, last + 1)));
    }

    private static final class Tools
    {
        // In separate class because it defers the initialization till used, SecureRandom is expensive.

        private static final SecureRandom RAND = new SecureRandom();

        private static final char[]       CHRS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

        static final String randomString(final int leng)
        {
            return RandomStringUtils.random(leng, 0, CHRS.length, false, false, CHRS, RAND);
        }

        static final byte[] randomBytes(final int leng)
        {
            final byte[] data = new byte[leng];

            RAND.nextBytes(data);

            return data;
        }

        static final long checksumOfBytes(final byte[] data)
        {
            final CRC32 check = new CRC32();

            check.update(data);

            return check.getValue();
        }

        static final byte[] checksumToBytes(final long valu)
        {
            final ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);

            buffer.putInt(0, ((int) (valu & 0xffffffffL)));

            return buffer.array();
        }

        static final String checksumOfChars(final CharSequence valu)
        {
            return SimpleHexEncoder.get().encode(Tools.checksumToBytes(Tools.checksumOfBytes(valu.toString().getBytes(IO.UTF_8_CHARSET))));
        }
    }
}
