/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;

public class JSONBinder extends AbstractDataBinder<CoreObjectMapper>
{
    public JSONBinder()
    {
        super(new CoreObjectMapper());
    }

    @Override
    public void send(final File file, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            if ((isStrict()) && (object instanceof JSONObject))
            {
                final OutputStream stream = IO.toOutputStream(file);

                try
                {
                    ((JSONObject) object).writeJSONString(stream, isStrict());
                }
                finally
                {
                    IO.close(stream);
                }
            }
            else
            {
                super.send(file, object);
            }
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public void send(final OutputStream stream, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            if ((isStrict()) && (object instanceof JSONObject))
            {
                ((JSONObject) object).writeJSONString(stream, isStrict());
            }
            else
            {
                super.send(stream, object);
            }
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public void send(final Writer writer, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            if ((isStrict()) && (object instanceof JSONObject))
            {
                ((JSONObject) object).writeJSONString(writer, isStrict());
            }
            else
            {
                super.send(writer, object);
            }
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public BinderType getType()
    {
        return BinderType.JSON;
    }

    public static class CoreObjectMapper extends ObjectMapper
    {
        private static final long                 serialVersionUID = 1L;

        private static final DefaultPrettyPrinter PRETTY           = PRETTY(4);

        public static final DefaultPrettyPrinter PRETTY(final String indent)
        {
            return new DefaultPrettyPrinter().withArrayIndenter(new DefaultIndenter().withIndent(indent)).withObjectIndenter(new DefaultIndenter().withIndent(indent));
        }

        public static final DefaultPrettyPrinter PRETTY(final int indent)
        {
            String buffer = " ";

            for (int i = 1; i < indent; i++)
            {
                buffer = buffer + " ";
            }
            return PRETTY(buffer);
        }

        public CoreObjectMapper()
        {
            setDefaultPrettyPrinter(PRETTY);
        }

        public CoreObjectMapper(final CoreObjectMapper parent)
        {
            super(parent);

            setDefaultPrettyPrinter(PRETTY);
        }

        @Override
        public CoreObjectMapper copy()
        {
            _checkInvalidCopy(CoreObjectMapper.class);

            return new CoreObjectMapper(this);
        }
    }
}
