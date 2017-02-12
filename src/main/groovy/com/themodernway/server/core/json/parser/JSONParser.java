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

package com.themodernway.server.core.json.parser;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.springframework.core.io.Resource;

import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.JSONBinder;

public final class JSONParser extends JSONBinder implements IJSONParser
{
    public JSONParser()
    {
    }

    @Override
    public JSONObject parse(final String in) throws ParserException
    {
        return bindJSON(in);
    }

    @Override
    public JSONObject parse(final InputStream in) throws ParserException
    {
        return bindJSON(in);
    }

    @Override
    public JSONObject parse(final Reader in) throws ParserException
    {
        return bindJSON(in);
    }

    @Override
    public JSONObject parse(final Resource in) throws ParserException
    {
        return bindJSON(in);
    }

    @Override
    public JSONObject parse(final File in) throws ParserException
    {
        return bindJSON(in);
    }

    @Override
    public JSONObject parse(final URL in) throws ParserException
    {
        return bindJSON(in);
    }
}
