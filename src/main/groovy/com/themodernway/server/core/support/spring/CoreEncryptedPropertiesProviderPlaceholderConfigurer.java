/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

package com.themodernway.server.core.support.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.logging.ICoreLoggingOperations;
import com.themodernway.server.core.security.IStringCryptoProvider;

public final class CoreEncryptedPropertiesProviderPlaceholderConfigurer extends CorePropertiesProviderPlaceholderConfigurer implements ICoreLoggingOperations
{
    private static final Logger logger   = Logger.getLogger(CoreEncryptedPropertiesProviderPlaceholderConfigurer.class);

    private Level               m_levels = Level.DEBUG;

    private boolean             m_onsave = false;

    public CoreEncryptedPropertiesProviderPlaceholderConfigurer(final IStringCryptoProvider crypto, final String prefix)
    {
        setPropertiesPersister(new CoreEncryptionPropertiesPersister(CommonOps.requireNonNull(crypto), this, StringOps.requireTrimOrNull(prefix)));
    }

    @Override
    public Level getLoggingLevel()
    {
        return m_levels;
    }

    @Override
    public void setLoggingLevel(final Level level)
    {
        if (null != level)
        {
            m_levels = level;
        }
        else
        {
            logger.error("Error setting log level to null");
        }
    }

    @Override
    public String getLoggingLevelAsString()
    {
        return getLoggingLevel().toString();
    }

    @Override
    public void setLoggingLevelAsString(final String level)
    {
        try
        {
            setLoggingLevel(Level.toLevel(StringOps.toTrimOrNull(level), Level.DEBUG));
        }
        catch (final Exception e)
        {
            logger.error("Error setting log level string to " + level, e);
        }
    }

    public void setEncryptOnSave(final boolean onsave)
    {
        m_onsave = onsave;
    }

    public boolean isEncryptOnSave()
    {
        return m_onsave;
    }

    private static final class CoreEncryptionPropertiesPersister extends DefaultPropertiesPersister implements PropertiesPersister
    {
        private static final Logger                                        logger = Logger.getLogger(CoreEncryptionPropertiesPersister.class);

        private final IStringCryptoProvider                                m_crypto;

        private final CoreEncryptedPropertiesProviderPlaceholderConfigurer m_parent;

        private final String                                               m_prefix;

        public CoreEncryptionPropertiesPersister(final IStringCryptoProvider crypto, final CoreEncryptedPropertiesProviderPlaceholderConfigurer parent, final String prefix)
        {
            m_crypto = CommonOps.requireNonNull(crypto);

            m_parent = CommonOps.requireNonNull(parent);

            m_prefix = StringOps.requireTrimOrNull(prefix);
        }

        @Override
        public void load(final Properties props, final InputStream is) throws IOException
        {
            super.load(props, is);

            decrypt(props);
        }

        @Override
        public void load(final Properties props, final Reader reader) throws IOException
        {
            super.load(props, reader);

            decrypt(props);
        }

        @Override
        public void store(final Properties props, final OutputStream os, final String header) throws IOException
        {
            if (m_parent.isEncryptOnSave())
            {
                encrypt(props);
            }
            super.store(props, os, header);
        }

        @Override
        public void store(final Properties props, final Writer writer, final String header) throws IOException
        {
            if (m_parent.isEncryptOnSave())
            {
                encrypt(props);
            }
            super.store(props, writer, header);
        }

        @Override
        public void loadFromXml(final Properties props, final InputStream is) throws IOException
        {
            super.loadFromXml(props, is);

            decrypt(props);
        }

        @Override
        public void storeToXml(final Properties props, final OutputStream os, final String header) throws IOException
        {
            if (m_parent.isEncryptOnSave())
            {
                encrypt(props);
            }
            super.storeToXml(props, os, header);
        }

        @Override
        public void storeToXml(final Properties props, final OutputStream os, final String header, final String encoding) throws IOException
        {
            if (m_parent.isEncryptOnSave())
            {
                encrypt(props);
            }
            super.storeToXml(props, os, header, encoding);
        }

        private final void decrypt(final Properties props)
        {
            final Level level = m_parent.getLoggingLevel();

            final boolean logson = (false == level.toString().equalsIgnoreCase("OFF"));

            final LinkedHashMap<String, String> saved = new LinkedHashMap<String, String>();

            props.forEach((e, o) -> {

                final String k = e.toString();

                final String v = props.getProperty(k);

                if ((null != v) && (false == v.isEmpty()))
                {
                    if (v.startsWith(m_prefix))
                    {
                        final String r = v.replace(m_prefix, "");

                        final String d = (r.isEmpty() ? r : m_crypto.decrypt(r));

                        if (logson)
                        {
                            logger.log(level, "decrypt(name: '" + k + "', decrypted length: '" + d.length() + "')");
                        }
                        saved.put(k, d);
                    }
                    else
                    {
                        if (logson)
                        {
                            logger.log(level, "decrypt(name: '" + k + "', origvalue: '" + v + "')");
                        }
                    }
                }
            });
            saved.forEach((k, v) -> props.setProperty(k, v));
        }

        private final void encrypt(final Properties props)
        {
            final Level level = m_parent.getLoggingLevel();

            final LinkedHashMap<String, String> saved = new LinkedHashMap<String, String>(props.size());

            for (final Object o : props.keySet())
            {
                final String k = o.toString();

                final String v = props.getProperty(k);

                if ((null != v) && (false == v.isEmpty()))
                {
                    logger.log(level, "encrypt(name:" + k + ",prop-value;" + v + ")");

                    final String e = m_prefix + m_crypto.encrypt(v);

                    logger.log(level, "encrypt(name:" + k + ",encrypted:" + e + ")");

                    saved.put(k, e);
                }
            }
            saved.forEach((k, v) -> props.setProperty(k, v));
        }
    }
}
