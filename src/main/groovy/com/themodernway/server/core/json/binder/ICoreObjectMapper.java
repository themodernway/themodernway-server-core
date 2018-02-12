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
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.themodernway.common.api.java.util.CommonOps;

public interface ICoreObjectMapper
{
    default public <M extends ObjectMapper> M withExtendedModules(final M mapper)
    {
        return Modules.withExtendedModules(mapper);
    }

    default public <M extends ObjectMapper> M withExtendedModules(final M mapper, final List<Supplier<Module>> list)
    {
        return Modules.withExtendedModules(mapper, list);
    }

    public static final class Modules
    {
        public static final List<Supplier<Module>> EXTENDED_MODULES_LIST = CommonOps.toList(ParameterNamesModule::new, Jdk8Module::new, JavaTimeModule::new);

        private Modules()
        {
        }

        public static final <M extends ObjectMapper> M withExtendedModules(final M mapper)
        {
            return withExtendedModules(mapper, EXTENDED_MODULES_LIST);
        }

        public static final <M extends ObjectMapper> M withExtendedModules(final M mapper, final List<Supplier<Module>> list)
        {
            list.forEach((final Supplier<Module> fact) -> mapper.registerModule(fact.get()));

            return mapper;
        }
    }
}
