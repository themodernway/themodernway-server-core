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

import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.ICoreObjectMapper.Modules;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;

public class JSONBinder extends AbstractDataBinder<CoreObjectMapper>
{
    private final AtomicBoolean m_strict = new AtomicBoolean(false);

    public JSONBinder()
    {
        super(new CoreObjectMapper());
    }

    @Override
    public IBinder setStrict()
    {
        if (m_strict.compareAndSet(false, true))
        {
            Modules.withStrict(copy());
        }
        return this;
    }

    @Override
    public <T> T convert(final Object object, final Class<T> claz) throws ParserException
    {
        if (claz.isAssignableFrom(object.getClass()))
        {
            return claz.cast(object);
        }
        if (String.class.equals(claz))
        {
            return CommonOps.cast(toJSONString(object));
        }
        try
        {
            return getMapper().convertValue(object, claz);
        }
        catch (final IllegalArgumentException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public BinderType getType()
    {
        return BinderType.JSON;
    }

    public static class CoreObjectMapper extends ObjectMapper implements ICoreObjectMapper
    {
        private static final long                   serialVersionUID = 1L;

        public static final JsonGenerator.Feature[] OUTPUT_ENABLED   = { JsonGenerator.Feature.ESCAPE_NON_ASCII };

        public static final JsonParser.Feature[]    PARSER_ENABLED   = { JsonParser.Feature.ALLOW_COMMENTS, JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES };

        public static final DefaultPrettyPrinter    PRETTY_PRINTER   = buildPrettyPrinter(4);

        public static final DefaultPrettyPrinter buildPrettyPrinter(final int repeat)
        {
            final String indent = StringOps.repeat(StringOps.SPACE_STRING, repeat);

            return new DefaultPrettyPrinter().withArrayIndenter(new DefaultIndenter().withIndent(indent)).withObjectIndenter(new DefaultIndenter().withIndent(indent));
        }

        public CoreObjectMapper()
        {
            withDefaults(this);
        }

        protected CoreObjectMapper(final CoreObjectMapper parent)
        {
            super(parent);
        }

        @Override
        public <M extends ObjectMapper> M withDefaults(final M mapper)
        {
            withExtendedModules(this).enable(PARSER_ENABLED).enable(OUTPUT_ENABLED).setDefaultPrettyPrinter(PRETTY_PRINTER);

            return mapper;
        }

        @Override
        public CoreObjectMapper copy()
        {
            _checkInvalidCopy(CoreObjectMapper.class);

            return new CoreObjectMapper(this);
        }
    }
}
