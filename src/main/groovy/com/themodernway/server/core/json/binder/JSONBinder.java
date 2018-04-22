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
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.binder.ICoreObjectMapper.Modules;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;

public final class JSONBinder extends AbstractDataBinder<CoreObjectMapper>
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
    public BinderType getType()
    {
        return BinderType.JSON;
    }

    public static final class CoreObjectMapper extends ObjectMapper implements ICoreObjectMapper
    {
        private static final long                 serialVersionUID = 1L;

        private static final DefaultPrettyPrinter PRETTY_PRINTER   = pretty(StringOps.repeat(StringOps.SPACE_STRING, 4));

        private static final DefaultPrettyPrinter pretty(final String indent)
        {
            return new DefaultPrettyPrinter().withArrayIndenter(new DefaultIndenter().withIndent(indent)).withObjectIndenter(new DefaultIndenter().withIndent(indent));
        }

        public CoreObjectMapper()
        {
            withDefaults(this);
        }

        private CoreObjectMapper(final CoreObjectMapper parent)
        {
            super(parent);
        }

        @Override
        public <M extends ObjectMapper> M withDefaults(final M mapper)
        {
            withExtendedModules(this).enable(JsonParser.Feature.ALLOW_COMMENTS).enable(JsonGenerator.Feature.ESCAPE_NON_ASCII).setDefaultPrettyPrinter(PRETTY_PRINTER);

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
