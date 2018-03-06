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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.themodernway.server.core.json.binder.XMLBinder.CoreXMLMapper;

public class XMLBinder extends AbstractDataBinder<CoreXMLMapper>
{
    public XMLBinder()
    {
        super(new CoreXMLMapper());
    }

    @Override
    public BinderType getType()
    {
        return BinderType.XML;
    }

    public static class CoreXMLMapper extends XmlMapper implements ICoreObjectMapper
    {
        private static final long                   serialVersionUID = 1L;

        public static final JsonGenerator.Feature[] OUTPUT_ENABLED   = {};

        public static final JsonParser.Feature[]    PARSER_ENABLED   = {};

        public CoreXMLMapper()
        {
            withDefaults(this).enable(PARSER_ENABLED).enable(OUTPUT_ENABLED);
        }

        protected CoreXMLMapper(final CoreXMLMapper parent)
        {
            super(parent);
        }

        @Override
        public CoreXMLMapper copy()
        {
            _checkInvalidCopy(CoreXMLMapper.class);

            return new CoreXMLMapper(this);
        }
    }
}
