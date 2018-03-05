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

package com.themodernway.server.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import com.themodernway.common.api.java.util.CommonOps;

public class NoCloseBufferedReader extends BufferedReader
{
    public NoCloseBufferedReader(final Reader reader)
    {
        super(CommonOps.requireNonNull(reader));
    }

    public NoCloseBufferedReader(final Reader reader, final int capacity)
    {
        super(CommonOps.requireNonNull(reader), capacity);
    }

    @Override
    public void close() throws IOException
    {
        // empty by design.
    }
}