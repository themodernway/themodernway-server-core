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

package com.themodernway.server.core.json;

import java.util.concurrent.atomic.AtomicBoolean;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;

public final class JSONPath
{
    private static final Configuration CONFIGURATION = Configuration.builder().mappingProvider(new JacksonMappingProvider(new CoreObjectMapper())).build();

    private JSONPath()
    {
    }

    public static final IEvaluationContext parse(final Object object)
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(object), CONFIGURATION));
    }

    private static final class InternalEvaluationContext implements IEvaluationContext
    {
        private ReadContext m_ctxt;

        public InternalEvaluationContext(final ReadContext ctxt)
        {
            m_ctxt = CommonOps.requireNonNull(ctxt);
        }

        @Override
        public final <T> T eval(final String path)
        {
            return m_ctxt.read(JSONPathStatics.PATH(path));
        }

        @Override
        public final <T> T eval(final String path, final Class<T> type)
        {
            return m_ctxt.read(JSONPathStatics.PATH(path), CommonOps.requireNonNull(type));
        }

        @Override
        public final <T> T eval(final String path, final TypeRef<T> type)
        {
            return m_ctxt.read(JSONPathStatics.PATH(path), CommonOps.requireNonNull(type));
        }

        @Override
        public final IEvaluationContext limit(final int size)
        {
            m_ctxt = m_ctxt.limit(Math.max(size, 0));

            return this;
        }
    }

    public static final class JSONPathStatics
    {
        private static final String        MARK = "$";

        private static final String        HASH = "#";

        private static final AtomicBoolean SUBT = new AtomicBoolean(false);

        private JSONPathStatics()
        {
        }

        public static final boolean usehash(final boolean flag)
        {
            return SUBT.getAndSet(flag);
        }

        static final String PATH(final String path)
        {
            if (SUBT.get())
            {
                return StringOps.requireTrimOrNull(path).replaceAll(HASH, MARK);
            }
            return StringOps.requireTrimOrNull(path);
        }
    }
}
