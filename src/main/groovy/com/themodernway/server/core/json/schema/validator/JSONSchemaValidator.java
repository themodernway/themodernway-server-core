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

package com.themodernway.server.core.json.schema.validator;

import com.themodernway.server.core.json.schema.JSONSchema;

public class JSONSchemaValidator
{
    private final JSONSchemaValidatorConfiguration m_config;

    public JSONSchemaValidator()
    {
        this(new JSONSchemaValidatorConfiguration());
    }

    public JSONSchemaValidator(final JSONSchemaValidatorConfiguration config)
    {
        m_config = config;
    }

    public JSONSchemaValidatorConfiguration getConfiguration()
    {
        return m_config;
    }

    public boolean validate(final Object object, final JSONSchema schema, final JSONSchemaValidatorContext context)
    {
        return true;
    }

    public boolean validate(final Object object, final JSONSchema schema)
    {
        return validate(object, schema, new JSONSchemaValidatorContext());
    }
}
