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

package com.themodernway.server.core.json;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.TypeRef;

public interface IEvaluationContext
{
    public String json();

    public <T> T model();

    public <T> T eval(CharSequence path, Predicate... filters);

    public <T> T eval(CharSequence path, Class<T> type, Predicate... filters);

    public <T> T eval(CharSequence path, TypeRef<T> type);

    public <T> T eval(ICompiledPath path);

    public <T> T eval(ICompiledPath path, Class<T> type);

    public <T> T eval(ICompiledPath path, TypeRef<T> type);

    public IEvaluationContext set(CharSequence path, Object valu, Predicate... filters);

    public IEvaluationContext set(ICompiledPath path, Object valu);

    public IEvaluationContext put(CharSequence path, CharSequence pkey, Object valu, Predicate... filters);

    public IEvaluationContext put(ICompiledPath path, CharSequence pkey, Object valu);

    public IEvaluationContext add(CharSequence path, Object valu, Predicate... filters);

    public IEvaluationContext add(ICompiledPath path, Object valu);

    public IEvaluationContext map(CharSequence path, IMappingFunction func, Predicate... filters);

    public IEvaluationContext map(ICompiledPath path, IMappingFunction func);

    public IEvaluationContext delete(CharSequence path, Predicate... filters);

    public IEvaluationContext delete(ICompiledPath path);

    public IEvaluationContext limit(int size);

    public IEvaluationContext listen(IEvaluationListener... listeners);
}
