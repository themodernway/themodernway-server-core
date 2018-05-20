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

package com.themodernway.server.core.servlet;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractResponseAction implements IResponseAction
{
    private Supplier<Map<String, ?>> m_supplier = null;

    protected void setHeaders(final HttpServletResponse response)
    {
        if (null != m_supplier)
        {
            final Map<String, ?> maps = m_supplier.get();

            if ((null != maps) && (false == maps.isEmpty()))
            {
                maps.forEach((k, v) -> ResponseActionHelper.addHeader(response, k, v));
            }
        }
    }

    protected final IResponseAction withSupplier(final Supplier<Map<String, ?>> supplier)
    {
        m_supplier = supplier;

        return this;
    }

    @Override
    public IResponseAction withHeaders(final Map<String, ?> headers)
    {
        return withHeaders(() -> headers);
    }

    protected static class ResponseActionHelper
    {
        protected static void addHeader(final HttpServletResponse response, final String k, final Object v)
        {
            if (v instanceof Collection)
            {
                ((Collection<?>) v).forEach(m -> response.addHeader(k, m.toString()));
            }
            else
            {
                response.addHeader(k, v.toString());
            }
        }
    }
}
