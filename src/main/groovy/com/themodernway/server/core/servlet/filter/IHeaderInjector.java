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

package com.themodernway.server.core.servlet.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.servlet.IServletCommonOperations;

public interface IHeaderInjector extends IServletCommonOperations
{
    @Override
    public default String getName()
    {
        return getClass().getName();
    }

    @Override
    public default String getConfigurationParameter(final String name)
    {
        return getHeaderInjectorFilter().getConfigurationParameter(name);
    }

    @Override
    public default List<String> getConfigurationParameterNames()
    {
        return CommonOps.toUnmodifiableList(getHeaderInjectorFilter().getConfigurationParameterNames());
    }

    public default void config(final JSONObject config)
    {
    }

    public IHeaderInjectorFilter getHeaderInjectorFilter();

    public void setHeaderInjectorFilter(final IHeaderInjectorFilter filter);

    public int inject(HttpServletRequest request, HttpServletResponse response);
}
