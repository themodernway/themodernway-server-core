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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;

import org.springframework.core.io.Resource;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.io.ReaderProxyInputStream;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;

public final class JSONPath
{
    private static final Option[]      CONFIGOPTIONS = { Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS };

    private static final Configuration CONFIGURATION = Configuration.builder().mappingProvider(new JacksonMappingProvider(new CoreObjectMapper())).options(CONFIGOPTIONS).build();

    private JSONPath()
    {
    }

    public static final CompiledPath compile(final CharSequence path, final Predicate... filters)
    {
        return new CompiledPath(JsonPath.compile(CommonOps.requireNonNull(path.toString()), filters));
    }

    public static final IEvaluationContext parse(final CharSequence json)
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(json.toString()), CONFIGURATION));
    }

    public static final IEvaluationContext parse(final JSONObject object)
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(object), CONFIGURATION));
    }

    public static final IEvaluationContext parse(final URL url) throws IOException
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(url), CONFIGURATION));
    }

    public static final IEvaluationContext parse(final InputStream stream) throws IOException
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(stream), CONFIGURATION));
    }

    public static final IEvaluationContext parse(final Reader reader) throws IOException
    {
        return parse(new ReaderProxyInputStream(reader));
    }

    public static final IEvaluationContext parse(final File file) throws IOException
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(file), CONFIGURATION));
    }

    public static final IEvaluationContext parse(final Path path) throws IOException
    {
        return parse(path.toFile());
    }

    public static final IEvaluationContext parse(final IFileItem file) throws IOException
    {
        try (InputStream stream = file.getInputStream())
        {
            return parse(stream);
        }
    }

    public static final IEvaluationContext parse(final Resource resource) throws IOException
    {
        try (InputStream stream = resource.getInputStream())
        {
            return parse(stream);
        }
    }

    private static final class InternalEvaluationContext implements IEvaluationContext
    {
        private ReadContext m_ctxt;

        private InternalEvaluationContext(final ReadContext ctxt)
        {
            m_ctxt = CommonOps.requireNonNull(ctxt);
        }

        @Override
        public final <T> T eval(final CharSequence path, final Predicate... filters)
        {
            return m_ctxt.read(CommonOps.requireNonNull(path.toString()), filters);
        }

        @Override
        public final <T> T eval(final CharSequence path, final Class<T> type, final Predicate... filters)
        {
            return m_ctxt.read(CommonOps.requireNonNull(path.toString()), CommonOps.requireNonNull(type), filters);
        }

        @Override
        public final <T> T eval(final CompiledPath path)
        {
            return m_ctxt.read(path.getJsonPath());
        }

        @Override
        public final <T> T eval(final CompiledPath path, final Class<T> type)
        {
            return m_ctxt.read(path.getJsonPath(), CommonOps.requireNonNull(type));
        }

        @Override
        public final <T> T eval(final CharSequence path, final TypeRef<T> type)
        {
            return m_ctxt.read(CommonOps.requireNonNull(path.toString()), CommonOps.requireNonNull(type));
        }

        @Override
        public final <T> T eval(final CompiledPath path, final TypeRef<T> type)
        {
            return m_ctxt.read(path.getJsonPath(), CommonOps.requireNonNull(type));
        }

        @Override
        public final IEvaluationContext limit(final int size)
        {
            m_ctxt = m_ctxt.limit(Math.max(size, 0));

            return this;
        }
    }
}
