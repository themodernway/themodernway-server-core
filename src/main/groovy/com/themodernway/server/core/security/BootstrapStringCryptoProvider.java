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

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.themodernway.server.core.AbstractCoreLoggingBase;
import com.themodernway.server.core.ICoreBase;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.logging.LoggingOps;

public final class BootstrapStringCryptoProvider extends AbstractCoreLoggingBase implements IStringCryptoProvider, ICoreBase
{
    private final TextEncryptor m_pcrypt;

    public BootstrapStringCryptoProvider(final Resource resource) throws IOException
    {
        this(resource, "bootstrap.crypto.pass", "bootstrap.crypto.salt");
    }

    public BootstrapStringCryptoProvider(final Resource resource, final String passname, final String saltname) throws IOException
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, format("BootstrapStringCryptoProvider(%s, %s, %s)", resource.getURI().toString(), passname, saltname));
        }
        final Properties properties = IO.toProperties(resource);

        final String pass = requireTrimOrNull(properties.getProperty(requireTrimOrNull(passname)));

        final String salt = requireTrimOrNull(properties.getProperty(requireTrimOrNull(saltname)));

        if (SimpleCryptoKeysGenerator.getCryptoKeysGenerator().isPassValid(pass))
        {
            if (logger().isInfoEnabled())
            {
                logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "BootstrapStringCryptoProvider password has validated.");
            }
        }
        else
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "BootstrapStringCryptoProvider password is not valid.");
            }
            throw new IllegalArgumentException("BootstrapStringCryptoProvider password is not valid.");
        }
        m_pcrypt = Encryptors.delux(pass, salt);

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "BootstrapStringCryptoProvider done.");
        }
    }

    @Override
    public final String encrypt(final CharSequence text)
    {
        return m_pcrypt.encrypt(text.toString());
    }

    @Override
    public final String decrypt(final CharSequence text)
    {
        return m_pcrypt.decrypt(text.toString());
    }
}
