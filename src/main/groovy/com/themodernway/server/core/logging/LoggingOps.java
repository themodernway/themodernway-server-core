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
 * limitations under the License. ThreadLocal.withInitial(supplier);
 */

package com.themodernway.server.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.themodernway.common.api.java.util.CommonOps;

public final class LoggingOps
{
    static
    {
        init();

        THE_MODERN_WAY_MARKER = MarkerFactory.getMarker("com.themodernway.server.core.logging.MARKER");
    }

    public static final Marker THE_MODERN_WAY_MARKER;

    private LoggingOps()
    {
    }

    public static final Logger getLogger(final String name)
    {
        return LoggerFactory.getLogger(CommonOps.requireNonNull(name));
    }

    public static final Logger getLogger(final Class<?> type)
    {
        return LoggerFactory.getLogger(CommonOps.requireNonNull(type));
    }

    public static final void init()
    {
        if (false == SLF4JBridgeHandler.isInstalled())
        {
            SLF4JBridgeHandler.install();
        }
    }
}
