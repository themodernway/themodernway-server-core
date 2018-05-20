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

import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.MatchingFilter;
import ch.qos.logback.core.spi.FilterReply;

public class CoreMarkerExcludeFilter extends MatchingFilter
{
    public CoreMarkerExcludeFilter()
    {
        setName(LoggingOps.THE_MODERN_WAY_MARKER.getName() + " exclude filter.");
    }

    @Override
    public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format, final Object[] params, final Throwable t)
    {
        return ((isStarted()) && (null != marker) && (marker.contains(LoggingOps.THE_MODERN_WAY_MARKER))) ? FilterReply.DENY : FilterReply.NEUTRAL;
    }
}
