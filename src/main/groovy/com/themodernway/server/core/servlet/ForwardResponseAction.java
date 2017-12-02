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

package com.themodernway.server.core.servlet;

import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;

public class ForwardResponseAction extends AbstractResponseAction
{
    private final String m_path;

    public ForwardResponseAction(final String path)
    {
        m_path = StringOps.requireTrimOrNull(path);
    }

    protected String getPath()
    {
        return m_path;
    }

    @Override
    public void call(final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        setHeaders(response);

        request.getRequestDispatcher(StringOps.requireTrimOrNull(getPath())).forward(request, response);
    }

    @Override
    public IResponseAction withHeaders(final Supplier<Map<String, ?>> headers)
    {
        return new ForwardResponseAction(getPath()).withSupplier(headers);
    }
}
