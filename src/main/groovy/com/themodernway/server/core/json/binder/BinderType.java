/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
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

package com.themodernway.server.core.json.binder;

import java.util.function.Supplier;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.IStringValued;
import com.themodernway.server.core.servlet.ICoreServletConstants;

public enum BinderType implements IBinderFactory, IStringValued
{
    JSON(JSONBinder::new), YAML(YAMLBinder::new), XML(XMLBinder::new), PROPERTIES(PropertiesBinder::new);

    private final Supplier<IBinder>    m_maker;

    private final ThreadLocal<IBinder> m_local;

    private BinderType(final Supplier<IBinder> binder)
    {
        m_maker = CommonOps.requireNonNull(binder);

        m_local = ThreadLocal.withInitial(m_maker);
    }

    @Override
    public final String getValue()
    {
        return toString();
    }

    @Override
    public final IBinder getBinder()
    {
        return m_maker.get();
    }

    @Override
    public final IBinder getBinderForThread()
    {
        return m_local.get();
    }

    public static final BinderType forContentType(final String type)
    {
        return BinderTypeOp.forContentType(type);
    }

    public static final BinderType forContentType(final Supplier<String> type)
    {
        return BinderTypeOp.forContentType(type.get());
    }

    public static final BinderType forName(final String name)
    {
        return BinderTypeOp.forName(name);
    }

    public static final BinderType forName(final Supplier<String> name)
    {
        return BinderTypeOp.forName(name.get());
    }

    static final class BinderTypeOp implements ICoreServletConstants
    {
        private BinderTypeOp()
        {
        }

        static final BinderType forContentType(final String type)
        {
            final String cont = StringOps.toTrimOrElse(type, CONTENT_TYPE_APPLICATION_JSON).toLowerCase();

            if (cont.contains(CONTENT_TYPE_APPLICATION_JSON))
            {
                return BinderType.JSON;
            }
            else if ((cont.contains(CONTENT_TYPE_TEXT_XML)) || (cont.contains(CONTENT_TYPE_APPLICATION_XML)))
            {
                return BinderType.XML;
            }
            else if ((cont.contains(CONTENT_TYPE_TEXT_YAML)) || (cont.contains(CONTENT_TYPE_APPLICATION_YAML)))
            {
                return BinderType.YAML;
            }
            else if (cont.contains(CONTENT_TYPE_TEXT_PROPERTIES))
            {
                return BinderType.PROPERTIES;
            }
            else
            {
                return BinderType.JSON;
            }
        }

        static final BinderType forName(final String name)
        {
            final String cont = StringOps.toTrimOrElse(name, BinderType.JSON.getValue()).toUpperCase();

            if (cont.equals(BinderType.JSON.getValue()))
            {
                return BinderType.JSON;
            }
            else if (cont.equals(BinderType.XML.getValue()))
            {
                return BinderType.XML;
            }
            else if (cont.equals(BinderType.YAML.getValue()))
            {
                return BinderType.YAML;
            }
            else if (cont.equals(BinderType.PROPERTIES.getValue()))
            {
                return BinderType.PROPERTIES;
            }
            else
            {
                return BinderType.JSON;
            }
        }
    }
}
