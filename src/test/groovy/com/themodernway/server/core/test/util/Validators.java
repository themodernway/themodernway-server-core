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

package com.themodernway.server.core.test.util;

import com.themodernway.server.core.json.validation.AllMatchMultiTypeValidator;
import com.themodernway.server.core.json.validation.AnyMatchMultiTypeValidator;
import com.themodernway.server.core.json.validation.BooleanValidator;
import com.themodernway.server.core.json.validation.DoublePredicateValidator;
import com.themodernway.server.core.json.validation.IJSONValidator;
import com.themodernway.server.core.json.validation.IntegerValidator;
import com.themodernway.server.core.json.validation.JSONArrayValidator;
import com.themodernway.server.core.json.validation.JSONObjectValidator;
import com.themodernway.server.core.json.validation.StringNotEmptyValidator;

public final class Validators
{
    private Validators()
    {
    }

    public static final IJSONValidator getSimpleValidator()
    {
        return make().add("name", new StringNotEmptyValidator());
    }

    public static final IJSONValidator getComplexValidator()
    {
        return make().add("name", new StringNotEmptyValidator()).add("list", new JSONArrayValidator(new AnyMatchMultiTypeValidator("StringOrInteger", new StringNotEmptyValidator(), new IntegerValidator())));
    }

    public static final IJSONValidator getComplexAnyMatchMultiValidator()
    {
        return make().add("name", new StringNotEmptyValidator()).add("flag", new BooleanValidator(), false).add("list", new JSONArrayValidator(new AnyMatchMultiTypeValidator("ComplexAny", new StringNotEmptyValidator(), new IntegerValidator(), new BooleanValidator(), new DoublePredicateValidator((v) -> v > 2d))));
    }

    public static final IJSONValidator getComplexAllMatchMultiValidator()
    {
        return make().add("name", new StringNotEmptyValidator()).add("flag", new BooleanValidator(), false).add("list", new JSONArrayValidator(new AllMatchMultiTypeValidator("ComplexAll", new DoublePredicateValidator((v) -> v > 2d), new DoublePredicateValidator((v) -> v < 20d))));
    }

    public static final JSONObjectValidator make()
    {
        return new JSONObjectValidator();
    }
}
