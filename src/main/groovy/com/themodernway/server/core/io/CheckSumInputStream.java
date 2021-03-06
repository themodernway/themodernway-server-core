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

package com.themodernway.server.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import com.themodernway.common.api.java.util.CommonOps;

public class CheckSumInputStream extends CheckedInputStream
{
    private final boolean m_close;

    public CheckSumInputStream(final InputStream in)
    {
        this(in, new CRC32(), true);
    }

    public CheckSumInputStream(final InputStream in, final boolean close)
    {
        this(in, new CRC32(), close);
    }

    public CheckSumInputStream(final InputStream in, final Checksum ck)
    {
        this(CommonOps.requireNonNull(in), CommonOps.requireNonNull(ck), true);
    }

    public CheckSumInputStream(final InputStream in, final Checksum ck, final boolean close)
    {
        super(CommonOps.requireNonNull(in), CommonOps.requireNonNull(ck));

        m_close = close;
    }

    @Override
    public void close() throws IOException
    {
        if (m_close)
        {
            super.close();
        }
    }
}
