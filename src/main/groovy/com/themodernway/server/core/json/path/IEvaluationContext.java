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

package com.themodernway.server.core.json.path;

import com.jayway.jsonpath.TypeRef;

public interface IEvaluationContext
{
    public String json();

    public <T> T model();

    public <T> T eval(String path);

    public <T> T eval(String path, ICriteria criteria);

    public <T> T eval(String path, ICriteria... criteria);

    public <T> T eval(String path, ICriteriaBuilder builder);

    public <T> T eval(String path, Class<T> type);

    public <T> T eval(String path, Class<T> type, ICriteria criteria);

    public <T> T eval(String path, Class<T> type, ICriteria... criteria);

    public <T> T eval(String path, Class<T> type, ICriteriaBuilder builder);

    public <T> T eval(String path, TypeRef<T> type);

    public <T> T eval(ICompiledPath path);

    public <T> T eval(ICompiledPath path, Class<T> type);

    public <T> T eval(ICompiledPath path, TypeRef<T> type);

    public IEvaluationContext set(String path, Object valu, ICriteria... criteria);

    public IEvaluationContext set(ICompiledPath path, Object valu);

    public IEvaluationContext put(String path, String pkey, Object valu, ICriteria... criteria);

    public IEvaluationContext put(ICompiledPath path, String pkey, Object valu);

    public IEvaluationContext add(String path, Object valu, ICriteria... criteria);

    public IEvaluationContext add(ICompiledPath path, Object valu);

    public IEvaluationContext map(String path, IMappingFunction func, ICriteria... criteria);

    public IEvaluationContext map(ICompiledPath path, IMappingFunction func);

    public IEvaluationContext delete(String path, ICriteria... criteria);

    public IEvaluationContext delete(ICompiledPath path);

    public IEvaluationContext limit(int size);

    public IEvaluationContext listen(IEvaluationListener... listeners);
}
