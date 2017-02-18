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

import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.themodernway.common.api.java.util.IHTTPConstants;

public class SimpleKeyStringSigningProvider implements IStringSigningProvider
{
    private static final Logger logger = Logger.getLogger(SimpleKeyStringSigningProvider.class);

    private final SecretKeySpec m_secret;

    public SimpleKeyStringSigningProvider(final String sign)
    {
        try
        {
            m_secret = new SecretKeySpec(Objects.requireNonNull(sign).getBytes(IHTTPConstants.CHARSET_UTF_8), HMAC_ALGORITHM);
        }
        catch (Exception e)
        {
            logger.error("hmac error", e);

            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String makeSignature(final String text)
    {
        return hmac(Objects.requireNonNull(text));
    }

    @Override
    public boolean testSignature(final String text, final String value)
    {
        return Objects.requireNonNull(value).equals(hmac(Objects.requireNonNull(text)));
    }

    private final String hmac(final String text)
    {
        try
        {
            final Mac hmac = Mac.getInstance(HMAC_ALGORITHM);

            hmac.init(m_secret);

            return Hex.encodeHexString(hmac.doFinal(text.getBytes(IHTTPConstants.CHARSET_UTF_8)));
        }
        catch (Exception e)
        {
            logger.error("hmac error", e);

            throw new IllegalArgumentException(e);
        }
    }
}
