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

public interface IMutableValidationContext extends IValidationContext
{
    public void pop();

    public void push(int place);

    public void push(String place);

    public void addValidationError(String error);

    public void addValidationError(IValidationError error);

    public void addTypeValidationError(String type);

    public void addRequiredAttributeValidationError(String attr);

    public void addInvalidAttributeValidationError(String attr, String type);
}
