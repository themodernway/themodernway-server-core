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

package com.themodernway.server.core.logging;

import org.slf4j.Logger;

import com.themodernway.common.api.java.util.StringOps;

public final class MDC
{
    private static final Logger logger = LoggingOps.getLogger(MDC.class);

    private MDC()
    {
    }

    public static final void clear()
    {
        try
        {
            org.slf4j.MDC.clear();
        }
        catch (final Exception e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "clear()", e);
            }
        }
    }

    public static final void put(final String key, final String val)
    {
        StringOps.requireTrimOrNull(key);

        try
        {
            org.slf4j.MDC.put(key, val);
        }
        catch (final Exception e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "put()", e);
            }
        }
    }

    public static final void remove(final String key)
    {
        StringOps.requireTrimOrNull(key);

        try
        {
            org.slf4j.MDC.remove(key);
        }
        catch (final Exception e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "remove()", e);
            }
        }
    }

    public static final String get(final String key)
    {
        StringOps.requireTrimOrNull(key);

        try
        {
            return org.slf4j.MDC.get(key);
        }
        catch (final Exception e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error(LoggingOps.THE_MODERN_WAY_MARKER, "get()", e);
            }
        }
        return null;
    }
}
