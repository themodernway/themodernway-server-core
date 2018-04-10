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

package com.themodernway.server.core.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface JSONStreamAware extends IJSONStreamAware
{
    @Override
    default void writeJSONString(final Writer out) throws IOException
    {
        JSONUtils.writeObjectAsJSON(out, this);
    }

    @Override
    default void writeJSONString(final Writer out, final boolean strict) throws IOException
    {
        if (false == strict)
        {
            JSONUtils.writeObjectAsJSON(out, this);
        }
        else
        {
            JSONUtils.writeObjectAsJSON(out, this, true);
        }
    }

    @Override
    default void writeJSONString(final OutputStream out) throws IOException
    {
        JSONUtils.writeObjectAsJSON(out, this);
    }

    @Override
    default void writeJSONString(final OutputStream out, final boolean strict) throws IOException
    {
        if (false == strict)
        {
            JSONUtils.writeObjectAsJSON(out, this);
        }
        else
        {
            JSONUtils.writeObjectAsJSON(out, this, true);
        }
    }
}
