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

package com.themodernway.server.core.json.schema;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.themodernway.server.core.json.JSONObject;

@JacksonXmlRootElement(localName = "schema")
public class JSONSchema extends JSONObject
{
    private static final long serialVersionUID = 9136364739594699014L;

    private JSONSchema()
    {
    }

    public JSONSchema(final String title, final String description)
    {
        set("$schema", "http://json-schema.org/draft-04/schema#").set("title", Objects.requireNonNull(title)).set("description", Objects.requireNonNull(description)).set("type", "object");
    }

    public JSONSchema fields()
    {
        return this;
    }

    public JSONSchema required(final List<?> required)
    {
        set("required", Objects.requireNonNull(required));

        return this;
    }

    public static final JSONSchema cast(final JSONObject object)
    {
        final JSONSchema schema = new JSONSchema();

        schema.putAll(Objects.requireNonNull(object));

        return schema;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object other)
    {
        if (null == other)
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        if (other instanceof JSONSchema)
        {
            return toString().equals(other.toString());
        }
        return super.equals(other);
    }
}
