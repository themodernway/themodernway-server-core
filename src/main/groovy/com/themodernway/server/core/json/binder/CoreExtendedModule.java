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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import groovy.lang.GString;

public class CoreExtendedModule extends SimpleModule
{
    private static final long serialVersionUID = 6874680640730996866L;

    public CoreExtendedModule()
    {
        super("CoreExtendedModule", PackageVersion.VERSION);

        addSerializer(GString.class, GStringSerializer.INSTANCE);
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(final Object o)
    {
        return this == o;
    }

    private static final class GStringSerializer extends StdSerializer<GString>
    {
        private static final long              serialVersionUID = 3729155567264269103L;

        private static final GStringSerializer INSTANCE         = new GStringSerializer();

        private GStringSerializer()
        {
            super(GString.class);
        }

        @Override
        public void serialize(final GString value, final JsonGenerator g, final SerializerProvider provider) throws IOException
        {
            if (null == value)
            {
                g.writeNull();
            }
            else
            {
                g.writeString(value.toString());
            }
        }
    }
}
