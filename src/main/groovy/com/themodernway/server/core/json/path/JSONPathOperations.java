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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.themodernway.common.api.types.ParserException;
import com.themodernway.server.core.file.vfs.IFileItem;

public class JSONPathOperations implements IJSONPathOperations
{
    @Override
    public ICompiledPath compile(final CharSequence target)
    {
        return JSONPath.compile(target);
    }

    @Override
    public IEvaluationContext parse(final Object target)
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final CharSequence target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final URL target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final InputStream target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final Reader target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final File target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final Path target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final IFileItem target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final Resource target) throws ParserException
    {
        return JSONPath.parse(target);
    }

    @Override
    public IEvaluationContext parse(final Properties target) throws ParserException
    {
        return JSONPath.parse(target);
    }
}
