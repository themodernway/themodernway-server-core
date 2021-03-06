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

import ch.qos.logback.classic.Level;

@FunctionalInterface
public interface IHasLogging
{
    public Logger logger();

    default Level getLoggingLevel()
    {
        return getLoggingLevel(logger());
    }

    default Level getLoggingLevel(final Logger logger)
    {
        return LoggingOps.getLevel(logger);
    }

    default Logger setLoggingLevel(final Level level)
    {
        return setLoggingLevel(logger(), level);
    }

    default Logger setLoggingLevel(final String level)
    {
        return setLoggingLevel(logger(), level);
    }

    default Logger setLoggingLevel(final Logger logger, final Level level)
    {
        return LoggingOps.setLevel(logger, level);
    }

    default Logger setLoggingLevel(final Logger logger, final String level)
    {
        return LoggingOps.setLevel(logger, level);
    }
}
