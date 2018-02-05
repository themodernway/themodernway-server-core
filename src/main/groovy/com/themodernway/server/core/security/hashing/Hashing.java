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

package com.themodernway.server.core.security.hashing;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.security.SimpleHexEncoder;

public final class Hashing
{
    private Hashing()
    {
    }

    public static final ICheckSum crc()
    {
        return new CheckSumBuilder(() -> new CRC32());
    }

    private static final class CheckSumBuilder implements ICheckSum
    {
        private final ICheckSumSupplier m_supp;

        public CheckSumBuilder(final ICheckSumSupplier supp)
        {
            m_supp = CommonOps.requireNonNull(supp);
        }

        @Override
        public long ofBytes(final byte[] buff)
        {
            final Checksum check = m_supp.get();

            check.update(buff, 0, buff.length);

            return check.getValue();
        }

        @Override
        public byte[] toBytes(final long valu)
        {
            final ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);

            buffer.putInt(0, ((int) (valu & 0xffffffffL)));

            return buffer.array();
        }

        @Override
        public long ofChars(final String buff)
        {
            return ofBytes(buff.getBytes(IO.UTF_8_CHARSET));
        }

        @Override
        public String toChars(final long valu)
        {
            return SimpleHexEncoder.get().encode(toBytes(valu));
        }
    }
}