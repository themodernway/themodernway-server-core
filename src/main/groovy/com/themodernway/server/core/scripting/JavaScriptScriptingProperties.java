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

package com.themodernway.server.core.scripting;

import java.util.Map;

import org.springframework.core.io.Resource;

import com.themodernway.common.api.java.util.CommonOps;

public class JavaScriptScriptingProperties extends AbstractScriptingProperties
{
    public JavaScriptScriptingProperties()
    {
        super(ScriptType.JAVASCRIPT);
    }

    public JavaScriptScriptingProperties(final Map<String, String> properties)
    {
        this();

        populate(CommonOps.requireNonNull(properties));
    }

    public JavaScriptScriptingProperties(final Resource resource) throws Exception
    {
        this();

        populate(CommonOps.requireNonNull(resource));
    }
}
