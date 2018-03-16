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

package com.themodernway.server.core.json.binder;

import java.io.IOException;
import java.util.Properties;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.PropertiesBinder.CorePropertiesMapper;

public class PropertiesBinder extends AbstractDataBinder<CorePropertiesMapper>
{
    public PropertiesBinder()
    {
        super(new CorePropertiesMapper());
    }

    @Override
    public <T> T bind(final Properties properties, final Class<T> claz) throws ParserException
    {
        try
        {
            return getMapper().readPropertiesAs(properties, claz);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public BinderType getType()
    {
        return BinderType.PROPERTIES;
    }

    public static class CorePropertiesMapper extends JavaPropsMapper implements ICoreObjectMapper
    {
        private static final long serialVersionUID = 1L;

        public CorePropertiesMapper()
        {
            withDefaults(this);
        }

        protected CorePropertiesMapper(final CorePropertiesMapper parent)
        {
            super(parent);
        }

        @Override
        public CorePropertiesMapper copy()
        {
            _checkInvalidCopy(CorePropertiesMapper.class);

            return new CorePropertiesMapper(this);
        }
    }
}
