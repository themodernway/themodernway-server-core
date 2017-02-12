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

package com.themodernway.server.core.json;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

public abstract class JSONNumberFormatter
{
    private ThreadLocal<NumberFormat> m_format;

    public JSONNumberFormatter()
    {
        if (null == m_format)
        {
            m_format = createThreadLocal();
        }
    }

    private final ThreadLocal<NumberFormat> createThreadLocal()
    {
        return new ThreadLocal<NumberFormat>()
        {
            @Override
            public NumberFormat get()
            {
                return super.get();
            }

            @Override
            protected NumberFormat initialValue()
            {
                return Objects.requireNonNull(makeNumberFormat());
            }

            @Override
            public void remove()
            {
                super.remove();
            }

            @Override
            public void set(final NumberFormat value)
            {
                super.set(Objects.requireNonNull(value));
            }
        };
    }

    public String format(final Number number)
    {
        return m_format.get().format(Objects.requireNonNull(number));
    }

    public Number parse(final String source) throws ParseException
    {
        return m_format.get().parse(Objects.requireNonNull(source));
    }

    protected abstract NumberFormat makeNumberFormat();

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();

        if (null == m_format)
        {
            m_format = createThreadLocal();
        }
    }
}
