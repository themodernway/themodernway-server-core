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

package com.themodernway.server.core.json.validation;

import com.themodernway.common.api.java.util.StringOps;

public class StringRegexValidator extends AbstractAttributeTypeValidator
{
    private final String m_regex;

    public StringRegexValidator(final String regex)
    {
        super("StringRegex");

        m_regex = StringOps.requireTrimOrNull(regex);
    }

    public String getRegex()
    {
        return m_regex;
    }

    @Override
    public void validate(final JSONValue json, final ValidationContext ctx)
    {
        if (null == json)
        {
            ctx.addBadTypeError(getType());

            return;
        }
        final String value = json.getAsString();

        if (null == value)
        {
            ctx.addBadTypeError(getType());
        }
        if (false == value.matches(getRegex()))
        {
            ctx.addError(String.format("String doesn't match %s", getRegex()));
        }
    }
}
