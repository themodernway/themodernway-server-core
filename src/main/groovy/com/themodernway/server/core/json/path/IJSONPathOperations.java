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

public interface IJSONPathOperations
{
    public ICompiledPath compile(CharSequence target);

    public IEvaluationContext parse(Object target);

    public IEvaluationContext parse(CharSequence target) throws ParserException;

    public IEvaluationContext parse(URL target) throws ParserException;

    public IEvaluationContext parse(InputStream target) throws ParserException;

    public IEvaluationContext parse(Reader target) throws ParserException;

    public IEvaluationContext parse(File target) throws ParserException;

    public IEvaluationContext parse(Path target) throws ParserException;

    public IEvaluationContext parse(IFileItem target) throws ParserException;

    public IEvaluationContext parse(Resource target) throws ParserException;

    public IEvaluationContext parse(Properties target) throws ParserException;
}
