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

import static com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.util.List;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;

public interface ICoreObjectMapper
{
    default <M extends ObjectMapper> M withDefaults(final M mapper)
    {
        return withExtendedModules(mapper);
    }

    default <M extends ObjectMapper> M withExtendedModules(final M mapper)
    {
        return Modules.withExtendedModules(mapper);
    }

    default <M extends ObjectMapper> M withExtendedModules(final M mapper, final List<Module> list)
    {
        return Modules.withModules(mapper, list);
    }

    public static final class TypedFor
    {
        private TypedFor()
        {
        }

        public static final ObjectReader reader(final ObjectMapper mapper)
        {
            return reader(mapper, JSONObject.class);
        }

        public static final ObjectWriter sender(final ObjectMapper mapper)
        {
            return sender(mapper, JSONObject.class);
        }

        public static final ObjectReader reader(final ObjectMapper mapper, final Class<?> type)
        {
            return mapper.readerFor(type);
        }

        public static final ObjectWriter sender(final ObjectMapper mapper, final Class<?> type)
        {
            return mapper.writerFor(type);
        }
    }

    public static final class Modules
    {
        private static final List<Module> STRICT_BINDER_MODULES = CommonOps.toList(new CoreStrictBinderModule());

        private static final List<Module> EXTENDED_MODULES_LIST = CommonOps.toList(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule());

        private Modules()
        {
        }

        public static final ObjectReader reader(final ObjectMapper mapper)
        {
            return mapper.readerFor(JSONObject.class);
        }

        public static final ObjectWriter sender(final ObjectMapper mapper)
        {
            return mapper.writerFor(JSONObject.class);
        }

        public static final <M extends ObjectMapper> M withStrict(final M mapper)
        {
            return withModules(mapper, STRICT_BINDER_MODULES);
        }

        public static final <M extends ObjectMapper> M withExtendedModules(final M mapper)
        {
            return withModules(mapper, EXTENDED_MODULES_LIST);
        }

        public static final <M extends ObjectMapper> M withModules(final M mapper, final List<Module> list)
        {
            mapper.registerModules(list).disable(AUTO_CLOSE_SOURCE).disable(AUTO_CLOSE_TARGET).disable(FAIL_ON_UNKNOWN_PROPERTIES).enable(WRITE_BIGDECIMAL_AS_PLAIN);

            return mapper;
        }
    }
}
