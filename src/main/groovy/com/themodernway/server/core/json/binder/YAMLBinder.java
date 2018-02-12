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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.themodernway.server.core.json.binder.YAMLBinder.CoreYAMLMapper;

public class YAMLBinder extends AbstractDataBinder<CoreYAMLMapper>
{
    public YAMLBinder()
    {
        super(new CoreYAMLMapper());
    }

    @Override
    public BinderType getType()
    {
        return BinderType.YAML;
    }

    public static class CoreYAMLMapper extends YAMLMapper implements ICoreObjectMapper
    {
        private static final long serialVersionUID = 1L;

        public CoreYAMLMapper()
        {
            withExtendedModules(this).enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        }

        public CoreYAMLMapper(final CoreYAMLMapper parent)
        {
            super(parent);

            withExtendedModules(this).enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        }

        @Override
        public CoreYAMLMapper copy()
        {
            _checkInvalidCopy(CoreYAMLMapper.class);

            return new CoreYAMLMapper(this);
        }
    }
}
