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

public class JSONSchemaValidatorConfiguration
{
    private boolean m_terminating_on_first_error = true;

    public JSONSchemaValidatorConfiguration()
    {
    }

    public boolean isTerminatingOnFirstError()
    {
        return m_terminating_on_first_error;
    }

    public JSONSchemaValidatorConfiguration setTerminationOnFirstError(final boolean terminating_on_first_error)
    {
        m_terminating_on_first_error = terminating_on_first_error;

        return this;
    }
}
