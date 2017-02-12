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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSONSchemaValidatorContext
{
    private final ArrayList<String> m_messages = new ArrayList<String>();

    public JSONSchemaValidatorContext()
    {
    }

    public JSONSchemaValidatorContext addMessage(final String message)
    {
        if (null != message)
        {
            m_messages.add(message);
        }
        return this;
    }

    public List<String> getMessages()
    {
        return Collections.unmodifiableList(m_messages);
    }
}
