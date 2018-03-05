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

package com.themodernway.server.core.json.binder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONUtils;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreStrictBinderModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public CoreStrictBinderModule()
    {
        super("CoreStrictBinderModule", PackageVersion.VERSION);

        addSerializer(Long.class, CoreStrictLongSerializer.INSTANCE);

        addSerializer(BigInteger.class, CoreStrictBigIntegerSerializer.INSTANCE);

        addSerializer(BigDecimal.class, CoreStrictBigDecimalSerializer.INSTANCE);
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(final Object o)
    {
        return this == o;
    }

    public static final class CoreStrictLongSerializer extends StdSerializer<Long>
    {
        private static final long                    serialVersionUID = 1L;

        public static final CoreStrictLongSerializer INSTANCE         = new CoreStrictLongSerializer();

        protected CoreStrictLongSerializer()
        {
            super(Long.class);
        }

        @Override
        public void serialize(final Long value, final JsonGenerator g, final SerializerProvider provider) throws IOException
        {
            final Integer o = JSONUtils.asInteger(value);

            if (null == o)
            {
                g.writeNull();
            }
            else
            {
                g.writeNumber(o.intValue());
            }
        }
    }

    public static final class CoreStrictBigIntegerSerializer extends StdSerializer<BigInteger>
    {
        private static final long                          serialVersionUID = 1L;

        public static final CoreStrictBigIntegerSerializer INSTANCE         = new CoreStrictBigIntegerSerializer();

        protected CoreStrictBigIntegerSerializer()
        {
            super(BigInteger.class);
        }

        @Override
        public void serialize(final BigInteger value, final JsonGenerator g, final SerializerProvider provider) throws IOException
        {
            final Integer o = JSONUtils.asInteger(value);

            if (null == o)
            {
                g.writeNull();
            }
            else
            {
                g.writeNumber(o.intValue());
            }
        }
    }

    public static final class CoreStrictBigDecimalSerializer extends StdSerializer<BigDecimal>
    {
        private static final long                          serialVersionUID = 1L;

        public static final CoreStrictBigDecimalSerializer INSTANCE         = new CoreStrictBigDecimalSerializer();

        protected CoreStrictBigDecimalSerializer()
        {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(final BigDecimal value, final JsonGenerator g, final SerializerProvider provider) throws IOException
        {
            final Double o = JSONUtils.asDouble(value);

            if (null == o)
            {
                g.writeNull();
            }
            else
            {
                g.writeNumber(o.doubleValue());
            }
        }
    }

    public static final class PackageVersion implements Versioned
    {
        private static final Logger logger  = LoggingOps.LOGGER(PackageVersion.class);

        public static final Version VERSION = make();

        @Override
        public Version version()
        {
            return VERSION;
        }

        private static final Version make()
        {
            try
            {
                final Properties properties = IO.toProperties(new ClassPathResource("strictbinder.properties", CoreStrictBinderModule.class));

                final String mversion = properties.getProperty("strictbinder.mversion", "2.0");

                final String group_id = properties.getProperty("strictbinder.group_id", "com.themodernway");

                final String artifact = properties.getProperty("strictbinder.artifact", "themodernway-server-core");

                final String gversion = properties.getProperty("strictbinder.gversion", "@GRADLE_BUILD_MODULE_VERSION@");

                final Version version = VersionUtil.parseVersion(gversion.equals("@GRADLE_BUILD_MODULE_VERSION@") ? mversion : gversion, group_id, artifact);

                return version;
            }
            catch (final IOException e)
            {
                if (logger.isErrorEnabled(LoggingOps.TMW_MARKER))
                {
                    logger.error(LoggingOps.TMW_MARKER, "strictbinder.properties", e);
                }
                return Version.unknownVersion();
            }
        }
    }
}
