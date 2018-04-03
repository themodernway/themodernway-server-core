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
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.EvaluationListener;
import com.jayway.jsonpath.EvaluationListener.EvaluationContinuation;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.ParserException;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;

public final class JSONPath
{
    private static final IBinder       BINDER = BinderType.JSON.getBinder();

    private static final Configuration CONFIG = builder(BINDER.getMapper());

    private static final Configuration builder(final ObjectMapper mapper)
    {
        return Configuration.builder().jsonProvider(new JacksonJsonProvider(mapper)).mappingProvider(new JacksonMappingProvider(mapper)).options(Option.SUPPRESS_EXCEPTIONS).build();
    }

    private static final String cast(final CharSequence valu)
    {
        return CommonOps.requireNonNull(CommonOps.requireNonNull(valu).toString());
    }

    private JSONPath()
    {
    }

    public static final ICompiledPath compile(final CharSequence path, final Predicate... filters)
    {
        return new InternalCompiledPath(JsonPath.compile(cast(path), filters));
    }

    public static final IEvaluationContext parse(final JSONObject object)
    {
        return new InternalEvaluationContext(JsonPath.parse(CommonOps.requireNonNull(object), CONFIG));
    }

    public static final IEvaluationContext parse(final CharSequence json) throws ParserException
    {
        return parse(BINDER.bindJSON(json));
    }

    public static final IEvaluationContext parse(final URL url) throws ParserException
    {
        return parse(BINDER.bindJSON(url));
    }

    public static final IEvaluationContext parse(final InputStream stream) throws ParserException
    {
        return parse(BINDER.bindJSON(stream));
    }

    public static final IEvaluationContext parse(final Reader reader) throws ParserException
    {
        return parse(BINDER.bindJSON(reader));
    }

    public static final IEvaluationContext parse(final File file) throws ParserException
    {
        return parse(BINDER.bindJSON(file));
    }

    public static final IEvaluationContext parse(final Path path) throws ParserException
    {
        return parse(BINDER.bindJSON(path));
    }

    public static final IEvaluationContext parse(final IFileItem file) throws ParserException
    {
        return parse(BINDER.bindJSON(file));
    }

    public static final IEvaluationContext parse(final Resource resource) throws ParserException
    {
        return parse(BINDER.bindJSON(resource));
    }

    public static final IEvaluationContext parse(final Properties properties) throws ParserException
    {
        return parse(BINDER.bindJSON(properties));
    }

    private static final class InternalCompiledPath implements ICompiledPath
    {
        private final JsonPath m_path;

        private InternalCompiledPath(final JsonPath path)
        {
            m_path = CommonOps.requireNonNull(path);
        }

        @Override
        public final String toString()
        {
            return m_path.getPath();
        }

        @Override
        public final boolean isDefinite()
        {
            return m_path.isDefinite();
        }

        @Override
        public final <T> T getCompiledPath()
        {
            return CommonOps.CAST(m_path);
        }
    }

    private static final class InternalEvaluationContext implements IEvaluationContext
    {
        private DocumentContext m_ctxt;

        private static final JsonPath cast(final ICompiledPath path)
        {
            return CommonOps.CAST(CommonOps.requireNonNull(CommonOps.requireNonNull(path).getCompiledPath()));
        }

        private static final String cast(final CharSequence valu)
        {
            return CommonOps.requireNonNull(CommonOps.requireNonNull(valu).toString());
        }

        private InternalEvaluationContext(final DocumentContext ctxt)
        {
            m_ctxt = CommonOps.requireNonNull(ctxt);
        }

        @Override
        public final String json()
        {
            return m_ctxt.jsonString();
        }

        @Override
        public final <T> T model()
        {
            return m_ctxt.json();
        }

        @Override
        public final <T> T eval(final CharSequence path, final Predicate... filters)
        {
            return m_ctxt.read(cast(path), filters);
        }

        @Override
        public final <T> T eval(final CharSequence path, final Class<T> type, final Predicate... filters)
        {
            return m_ctxt.read(cast(path), CommonOps.requireNonNull(type), filters);
        }

        @Override
        public final <T> T eval(final ICompiledPath path)
        {
            return m_ctxt.read(cast(path));
        }

        @Override
        public final <T> T eval(final ICompiledPath path, final Class<T> type)
        {
            return m_ctxt.read(cast(path), CommonOps.requireNonNull(type));
        }

        @Override
        public final <T> T eval(final CharSequence path, final TypeRef<T> type)
        {
            return m_ctxt.read(cast(path), CommonOps.requireNonNull(type));
        }

        @Override
        public final <T> T eval(final ICompiledPath path, final TypeRef<T> type)
        {
            return m_ctxt.read(cast(path), CommonOps.requireNonNull(type));
        }

        @Override
        public final IEvaluationContext set(final CharSequence path, final Object valu, final Predicate... filters)
        {
            m_ctxt = m_ctxt.set(cast(path), valu, filters);

            return this;
        }

        @Override
        public final IEvaluationContext set(final ICompiledPath path, final Object valu)
        {
            m_ctxt = m_ctxt.set(cast(path), valu);

            return this;
        }

        @Override
        public final IEvaluationContext put(final CharSequence path, final CharSequence pkey, final Object valu, final Predicate... filters)
        {
            m_ctxt = m_ctxt.put(cast(path), cast(pkey), valu, filters);

            return this;
        }

        @Override
        public final IEvaluationContext put(final ICompiledPath path, final CharSequence pkey, final Object valu)
        {
            m_ctxt = m_ctxt.put(cast(path), cast(pkey), valu);

            return this;
        }

        @Override
        public final IEvaluationContext add(final CharSequence path, final Object valu, final Predicate... filters)
        {
            m_ctxt = m_ctxt.add(cast(path), valu, filters);

            return this;
        }

        @Override
        public final IEvaluationContext add(final ICompiledPath path, final Object valu)
        {
            m_ctxt = m_ctxt.add(cast(path), valu);

            return this;
        }

        @Override
        public final IEvaluationContext delete(final CharSequence path, final Predicate... filters)
        {
            m_ctxt = m_ctxt.delete(cast(path), filters);

            return this;
        }

        @Override
        public final IEvaluationContext delete(final ICompiledPath path)
        {
            m_ctxt = m_ctxt.delete(cast(path));

            return this;
        }

        @Override
        public final IEvaluationContext map(final CharSequence path, final IMappingFunction func, final Predicate... filters)
        {
            CommonOps.requireNonNull(func);

            m_ctxt = m_ctxt.map(cast(path), (valu, configuration) -> func.apply(valu, this), filters);

            return this;
        }

        @Override
        public final IEvaluationContext map(final ICompiledPath path, final IMappingFunction func)
        {
            CommonOps.requireNonNull(func);

            m_ctxt = m_ctxt.map(cast(path), (valu, configuration) -> func.apply(valu, this));

            return this;
        }

        @Override
        public final IEvaluationContext limit(final int size)
        {
            m_ctxt = CommonOps.CAST(m_ctxt.limit(Math.max(size, 0)));

            return this;
        }

        @Override
        public final IEvaluationContext listen(final IEvaluationListener... listeners)
        {
            if ((null == listeners) || (listeners.length < 1))
            {
                return this;
            }
            final int size = listeners.length;

            final EvaluationListener[] list = new EvaluationListener[size];

            for (int i = 0; i < size; i++)
            {
                final IEvaluationListener eval = listeners[i];

                list[i] = found -> {

                    final IEvaluationListenerResult result = new IEvaluationListenerResult()
                    {
                        @Override
                        public final int index()
                        {
                            return found.index();
                        }

                        @Override
                        public final String path()
                        {
                            return found.path();
                        }

                        @Override
                        public final Object result()
                        {
                            return found.result();
                        }
                    };
                    return eval.test(result) ? EvaluationContinuation.CONTINUE : EvaluationContinuation.ABORT;
                };
            }
            m_ctxt = CommonOps.CAST(m_ctxt.withListeners(list));

            return this;
        }
    }
}
