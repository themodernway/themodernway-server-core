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

package com.themodernway.server.core.json.validation;

public class DateValidator extends AbstractAttributeTypeValidator
{
    public DateValidator()
    {
        super("Date");
    }

    @Override
    public void validate(final IJSONValue json, final ValidationContext ctx)
    {
        if ((null == json) || (null == json.getAsDate()))
        {
            ctx.addBadTypeError(getName());
        }
    }
}
