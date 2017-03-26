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

import com.themodernway.server.core.ThreadLocalDateFormat;
import com.themodernway.server.core.ThreadLocalNumberFormat;

public class JSONContext implements IJSONContext
{
    private IJSONObjectReplacer     m_obreplacer;

    private IJSONArrayReplacer      m_arreplacer;

    private ThreadLocalDateFormat   m_dformatter;

    private ThreadLocalNumberFormat m_nformatter;

    public JSONContext()
    {
    }

    public JSONContext(final JSONContext context)
    {
        setObjectReplacer(context.getObjectReplacer());

        setArrayReplacer(context.getArrayReplacer());

        setDateFormatter(context.getDateFormatter());

        setNumberFormatter(context.getNumberFormatter());
    }

    public JSONContext setObjectReplacer(final IJSONObjectReplacer replacer)
    {
        m_obreplacer = replacer;

        return this;
    }

    @Override
    public IJSONObjectReplacer getObjectReplacer()
    {
        return m_obreplacer;
    }

    public JSONContext setArrayReplacer(final IJSONArrayReplacer replacer)
    {
        m_arreplacer = replacer;

        return this;
    }

    @Override
    public IJSONArrayReplacer getArrayReplacer()
    {
        return m_arreplacer;
    }

    @Override
    public ThreadLocalDateFormat getDateFormatter()
    {
        return m_dformatter;
    }

    public JSONContext setDateFormatter(final ThreadLocalDateFormat formatter)
    {
        m_dformatter = formatter;

        return this;
    }

    @Override
    public ThreadLocalNumberFormat getNumberFormatter()
    {
        return m_nformatter;
    }

    public JSONContext setNumberFormatter(final ThreadLocalNumberFormat formatter)
    {
        m_nformatter = formatter;

        return this;
    }
}
