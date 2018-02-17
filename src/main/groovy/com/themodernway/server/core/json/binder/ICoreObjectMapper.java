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

import java.util.List;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.themodernway.common.api.java.util.CommonOps;

public interface ICoreObjectMapper
{
    default public <M extends ObjectMapper> M withDefaults(final M mapper)
    {
        return withExtendedModules(mapper);
    }

    default public <M extends ObjectMapper> M withExtendedModules(final M mapper)
    {
        return Modules.withExtendedModules(mapper);
    }

    default public <M extends ObjectMapper> M withExtendedModules(final M mapper, final List<Module> list)
    {
        return Modules.withModules(mapper, list);
    }

    public static final class Modules
    {
        private static final List<Module> STRICT_BINDER_MODULES = CommonOps.toList(new CoreStrictBinderModule());

        private static final List<Module> EXTENDED_MODULES_LIST = CommonOps.toList(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule());

        private Modules()
        {
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
            mapper.registerModules(list);

            return mapper;
        }
    }
}
