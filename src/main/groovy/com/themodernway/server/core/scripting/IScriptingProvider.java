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

package com.themodernway.server.core.scripting;

import java.io.Closeable;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.core.io.Resource;

public interface IScriptingProvider extends Closeable
{
    public ScriptEngine engine(ScriptType type);

    public ScriptEngine engine(ScriptType type, Resource resource) throws Exception;

    public ScriptEngine engine(ScriptType type, Reader reader) throws Exception;

    public ScriptEngine engine(ScriptType type, InputStream stream) throws Exception;

    public ScriptEngine engine(ScriptType type, ClassLoader loader);

    public List<String> getScriptingLanguageNames();

    public List<String> getScriptingLanguageNames(ClassLoader loader);

    public List<ScriptType> getScriptingLanguageTypes();

    public List<ScriptType> getScriptingLanguageTypes(ClassLoader loader);

    public ScriptEngineManager getScriptEngineManager();

    public ScriptEngineManager getScriptEngineManager(ClassLoader loader);

    public ScriptingProxy proxy(ScriptType type, Resource resource) throws Exception;

    public ScriptingProxy proxy(ScriptType type, Reader reader) throws Exception;

    public ScriptingProxy proxy(ScriptType type, InputStream stream) throws Exception;
}
