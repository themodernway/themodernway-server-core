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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class SimpleHexEncoder implements IHexEncoder
{
    private static final SimpleHexEncoder INSTANCE = new SimpleHexEncoder();

    public static final IHexEncoder get()
    {
        return INSTANCE;
    }

    @Override
    public String encode(final byte[] src)
    {
        return Hex.encodeHexString(src);
    }

    @Override
    public byte[] decode(final String src)
    {
        try
        {
            return Hex.decodeHex(src);
        }
        catch (final DecoderException e)
        {
            throw new IllegalArgumentException("decode (" + src + ") error.", e);
        }
    }
}
