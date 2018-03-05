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

import java.util.ArrayList;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.INamed;

public interface IAttributeTypeValidator extends INamed
{
    public boolean isIgnored();

    public void validate(IJSONValue json, ValidationContext ctx);

    public static List<IAttributeTypeValidator> concat(final IAttributeTypeValidator type, final IAttributeTypeValidator... list)
    {
        final ArrayList<IAttributeTypeValidator> temp = CommonOps.arrayList(CommonOps.requireNonNull(list));

        temp.add(0, CommonOps.requireNonNull(type));

        return repair(temp);
    }

    public static List<IAttributeTypeValidator> repair(final List<IAttributeTypeValidator> list)
    {
        final ArrayList<IAttributeTypeValidator> temp = CommonOps.arrayList(CommonOps.requireNonNull(list));

        temp.removeIf(type -> null == type);

        return CommonOps.toUnmodifiableList(temp);
    }
}
