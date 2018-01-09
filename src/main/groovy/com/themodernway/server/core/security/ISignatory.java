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

package com.themodernway.server.core.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import com.themodernway.common.api.types.INamed;

public interface ISignatory extends INamed
{
    public Map<String, Object> getProperties();

    public byte[] sign(byte[] src) throws Exception;

    public byte[] sign(Reader src) throws Exception;

    public byte[] sign(InputStream src) throws Exception;

    public void sign(Reader src, Writer dst) throws Exception;

    public void sign(InputStream src, OutputStream dst) throws Exception;

    public byte[] sign(String src) throws Exception;

    public byte[] sign(String src, String charset) throws Exception;

    public byte[] sign(String src, Charset charset) throws Exception;

    public String sign(String src, ISignatoryEncoder encoder) throws Exception;

    public String sign(String src, String charset, ISignatoryEncoder encoder) throws Exception;

    public String sign(String src, Charset charset, ISignatoryEncoder encoder) throws Exception;
}
