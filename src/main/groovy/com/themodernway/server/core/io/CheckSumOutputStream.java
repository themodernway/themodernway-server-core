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
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;

import com.themodernway.common.api.java.util.CommonOps;

public class CheckSumOutputStream extends CheckedOutputStream
{
    private final boolean m_close;

    public CheckSumOutputStream(final OutputStream os)
    {
        this(os, new CRC32(), true);
    }

    public CheckSumOutputStream(final OutputStream os, final boolean close)
    {
        this(os, new CRC32(), close);
    }

    public CheckSumOutputStream(final OutputStream os, final Checksum ck)
    {
        this(CommonOps.requireNonNull(os), CommonOps.requireNonNull(ck), true);
    }

    public CheckSumOutputStream(final OutputStream os, final Checksum ck, final boolean close)
    {
        super(CommonOps.requireNonNull(os), CommonOps.requireNonNull(ck));

        m_close = close;
    }

    @Override
    public void close() throws IOException
    {
        if (m_close)
        {
            super.close();
        }
        else
        {
            flush();
        }
    }
}
