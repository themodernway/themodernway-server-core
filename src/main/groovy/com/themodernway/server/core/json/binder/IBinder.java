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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;

public interface IBinder
{
    public <T> T bind(Path path, Class<T> claz) throws ParserException;

    public <T> T bind(File file, Class<T> claz) throws ParserException;

    public <T> T bind(IFileItem file, Class<T> claz) throws ParserException;

    public <T> T bind(InputStream stream, Class<T> claz) throws ParserException;

    public <T> T bind(Reader reader, Class<T> claz) throws ParserException;

    public <T> T bind(Resource resource, Class<T> claz) throws ParserException;

    public <T> T bind(CharSequence text, Class<T> claz) throws ParserException;

    public <T> T bind(URL url, Class<T> claz) throws ParserException;

    public <T> T bind(Properties properties, Class<T> claz) throws ParserException;

    public <T> T convert(Object object, Class<T> claz) throws ParserException;

    public JSONObject bindJSON(Path path) throws ParserException;

    public JSONObject bindJSON(File file) throws ParserException;

    public JSONObject bindJSON(IFileItem file) throws ParserException;

    public JSONObject bindJSON(InputStream stream) throws ParserException;

    public JSONObject bindJSON(Reader reader) throws ParserException;

    public JSONObject bindJSON(Resource resource) throws ParserException;

    public JSONObject bindJSON(CharSequence text) throws ParserException;

    public JSONObject bindJSON(URL url) throws ParserException;

    public JSONObject bindJSON(Properties properties) throws ParserException;

    public IBinder pretty();

    public IBinder pretty(boolean enabled);

    public boolean isPretty();

    public IBinder configure(MapperFeature feature, boolean state);

    public IBinder configure(SerializationFeature feature, boolean state);

    public IBinder configure(DeserializationFeature feature, boolean state);

    public IBinder disable(MapperFeature... features);

    public IBinder disable(SerializationFeature... features);

    public IBinder disable(DeserializationFeature... features);

    public IBinder enable(MapperFeature... features);

    public IBinder enable(SerializationFeature... features);

    public IBinder enable(DeserializationFeature... features);

    public boolean isEnabled(MapperFeature feature);

    public boolean isEnabled(SerializationFeature feature);

    public boolean isEnabled(DeserializationFeature feature);

    public boolean isStrict();

    public void send(File file, Object object) throws ParserException;

    public void send(OutputStream stream, Object object) throws ParserException;

    public void send(Writer writer, Object object) throws ParserException;

    public IBinder setStrict(boolean strict);

    public String toString(Object object) throws ParserException;

    public BinderType getType();

    public JSONObject toJSONObject(Object object) throws ParserException;

    public String toJSONString(Object object) throws ParserException;

    public ObjectMapper getMapper();

    public boolean canSerializeType(Class<?> type);

    public boolean canSerializeObject(Object object);
}