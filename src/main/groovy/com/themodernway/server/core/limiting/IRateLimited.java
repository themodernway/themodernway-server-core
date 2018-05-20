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

package com.themodernway.server.core.limiting;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;
import com.themodernway.common.api.java.util.CommonOps;

@FunctionalInterface
public interface IRateLimited
{
    public static final long   NO_WARMUP_TIME = 0L;

    public static final double NO_RATE_LIMITS = 0.0d;

    public static final double MAX_RATE_LIMIT = 10000000.0d;

    public static final double MIN_RATE_LIMIT = 0.00000001d;

    default void acquire()
    {
        acquire(1);
    }

    public void acquire(int permits);

    public static final class RateLimiterFactory
    {
        private RateLimiterFactory()
        {
        }

        public static final double normalize(final double rate)
        {
            return CommonOps.box(rate, MIN_RATE_LIMIT, MAX_RATE_LIMIT);
        }

        public static final RateLimiter create(final double rate)
        {
            if (rate <= NO_RATE_LIMITS)
            {
                return null;
            }
            return RateLimiter.create(normalize(rate));
        }

        public static final RateLimiter create(final double rate, final long warm, final TimeUnit unit)
        {
            if (rate <= NO_RATE_LIMITS)
            {
                return null;
            }
            if (warm <= NO_WARMUP_TIME)
            {
                return create(rate);
            }
            return RateLimiter.create(normalize(rate), warm, CommonOps.requireNonNullOrElse(unit, TimeUnit.MILLISECONDS));
        }

        public static final RateLimiter create(final Class<?> claz)
        {
            if ((null != claz) && (claz.isAnnotationPresent(RateLimit.class)))
            {
                final RateLimit anno = claz.getAnnotation(RateLimit.class);

                if (null != anno)
                {
                    return create(anno.value(), anno.warmup(), anno.unit());
                }
            }
            return null;
        }
    }
}
