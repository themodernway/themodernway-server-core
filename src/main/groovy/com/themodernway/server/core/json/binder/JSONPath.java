/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;

public final class JSONPath
{
    private JSONPath()
    {
    }

    public static void main(final String... args)
    {
        final String name = parse(new JSONObject("name", "Dean")).eval("$.name");

        System.out.println("" + name);
    }

    public static final EvaluationContext parse(final String string)
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(string), JSONPathStatics.CONF));
    }

    public static final EvaluationContext parse(final Object object)
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(object), JSONPathStatics.CONF));
    }

    public static interface EvaluationContext
    {
        public <T> T eval(String path);

        public <T> T eval(String path, Class<T> type);

        public <T> T eval(String path, TypeRef<T> type);

        public EvaluationContext limit(int size);
    }

    private static final class InternalEvaluationContext implements EvaluationContext
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
        public final EvaluationContext limit(final int size)
        {
            m_ctxt = m_ctxt.limit(Math.max(size, 0));

            return this;
        }
    }

    private static final class JSONPathStatics
    {
        static final String        MARK = "$";

        static final String        HASH = "#";

        static final Configuration CONF = Configuration.builder().mappingProvider(new JacksonMappingProvider(new CoreObjectMapper())).build();

        static final String PATH(final String path)
        {
            return StringOps.requireTrimOrNull(path).replaceAll(HASH, MARK);
        }
    }
}
