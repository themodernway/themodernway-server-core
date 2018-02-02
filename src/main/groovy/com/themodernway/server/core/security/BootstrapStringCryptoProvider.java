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

import java.util.Properties;

import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.logging.LoggingOps;

public final class BootstrapStringCryptoProvider implements IStringCryptoProvider
{
    private static final Logger logger = LoggingOps.LOGGER(BootstrapStringCryptoProvider.class);

    private final TextEncryptor m_pcrypt;

    public BootstrapStringCryptoProvider(final Resource resource) throws Exception
    {
        this(resource, "bootstrap.crypto.pass", "bootstrap.crypto.salt");
    }

    public BootstrapStringCryptoProvider(final Resource resource, final String passname, final String saltname) throws Exception
    {
        logger.info("BootstrapStringCryptoProvider(" + resource.getURI().toString() + ", " + passname + ", " + saltname + ")");

        final Properties properties = IO.toProperties(resource);

        final String pass = StringOps.requireTrimOrNull(properties.getProperty(StringOps.requireTrimOrNull(passname)));

        final String salt = StringOps.requireTrimOrNull(properties.getProperty(StringOps.requireTrimOrNull(saltname)));

        if (SimpleCryptoKeysGenerator.getCryptoKeysGenerator().isPassValid(pass))
        {
            logger.info("BootstrapStringCryptoProvider(password has validated)");
        }
        else
        {
            logger.error("BootstrapStringCryptoProvider(password is not valid)");

            logger.trace("BootstrapStringCryptoProvider(password is not valid) " + pass);

            throw new IllegalArgumentException("BootstrapStringCryptoProvider(password is not valid)");
        }
        m_pcrypt = Encryptors.delux(pass, salt);
    }

    @Override
    public final String encrypt(final String text)
    {
        return m_pcrypt.encrypt(CommonOps.requireNonNull(text));
    }

    @Override
    public final String decrypt(final String text)
    {
        return m_pcrypt.decrypt(CommonOps.requireNonNull(text));
    }
}
