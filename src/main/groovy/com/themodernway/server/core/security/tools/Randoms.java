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

package com.themodernway.server.core.security.tools;

import java.security.SecureRandom;

import org.apache.commons.lang3.RandomStringUtils;

public final class Randoms
{
    private Randoms()
    {
    }

    public static final class Secure
    {
        // In separate class because it defers the initialization till used, SecureRandom is expensive.

        private static final SecureRandom RAND = new SecureRandom();

        private static final char[]       CHRS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

        private Secure()
        {
        }

        public static final String getString(final int leng)
        {
            return getString(validate(leng), CHRS);
        }

        public static final String getString(final int leng, final char[] from)
        {
            return RandomStringUtils.random(validate(leng), 0, from.length, false, false, from, RAND);
        }

        public static final byte[] nextBytes(final int leng)
        {
            final byte[] data = new byte[validate(leng)];

            RAND.nextBytes(data);

            return data;
        }

        private static final int validate(final int leng)
        {
            if (leng < 0)
            {
                throw new IllegalArgumentException("length " + leng + " is less than 0.");
            }
            return leng;
        }
    }
}
