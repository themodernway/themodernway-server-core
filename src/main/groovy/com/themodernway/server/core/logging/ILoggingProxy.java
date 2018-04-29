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

import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;

import ch.qos.logback.classic.Level;

@FunctionalInterface
public interface ILoggingProxy extends IHasLogging
{
    default boolean isInfoEnabled()
    {
        return logger().isInfoEnabled();
    }

    default boolean isWarnEnabled()
    {
        return logger().isWarnEnabled();
    }

    default boolean isDebugEnabled()
    {
        return logger().isDebugEnabled();
    }

    default boolean isErrorEnabled()
    {
        return logger().isErrorEnabled();
    }

    default Level getLevel()
    {
        return LoggingOps.getLevel(logger());
    }

    default void setLevel(final Level level)
    {
        LoggingOps.setLevel(logger(), level);
    }

    default void setLevel(final String level)
    {
        LoggingOps.setLevel(logger(), level);
    }

    default void info(final String message, final Object... args)
    {
        if (isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), args));
        }
    }

    default void info(final String message, final Throwable oops)
    {
        if (isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, CommonOps.requireNonNull(message), oops);
        }
    }

    default void info(final String message, final List<?> args, final Throwable oops)
    {
        if (isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), CommonOps.requireNonNullOrElse(args, () -> CommonOps.emptyList()).toArray()), oops);
        }
    }

    default void warn(final String message, final Object... args)
    {
        if (isWarnEnabled())
        {
            logger().warn(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), args));
        }
    }

    default void warn(final String message, final Throwable oops)
    {
        if (isWarnEnabled())
        {
            logger().warn(LoggingOps.THE_MODERN_WAY_MARKER, CommonOps.requireNonNull(message), oops);
        }
    }

    default void warn(final String message, final List<?> args, final Throwable oops)
    {
        if (isWarnEnabled())
        {
            logger().warn(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), CommonOps.requireNonNullOrElse(args, () -> CommonOps.emptyList()).toArray()), oops);
        }
    }

    default void debug(final String message, final Object... args)
    {
        if (isDebugEnabled())
        {
            logger().debug(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), args));
        }
    }

    default void debug(final String message, final Throwable oops)
    {
        if (isDebugEnabled())
        {
            logger().debug(LoggingOps.THE_MODERN_WAY_MARKER, CommonOps.requireNonNull(message), oops);
        }
    }

    default void debug(final String message, final List<?> args, final Throwable oops)
    {
        if (isDebugEnabled())
        {
            logger().debug(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), CommonOps.requireNonNullOrElse(args, () -> CommonOps.emptyList()).toArray()), oops);
        }
    }

    default void error(final String message, final Object... args)
    {
        if (isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), args));
        }
    }

    default void error(final String message, final Throwable oops)
    {
        if (isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, CommonOps.requireNonNull(message), oops);
        }
    }

    default void error(final String message, final List<?> args, final Throwable oops)
    {
        if (isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format(CommonOps.requireNonNull(message), CommonOps.requireNonNullOrElse(args, () -> CommonOps.emptyList()).toArray()), oops);
        }
    }
}
