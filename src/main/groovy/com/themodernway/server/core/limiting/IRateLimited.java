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

import com.google.common.util.concurrent.RateLimiter;

@FunctionalInterface
public interface IRateLimited
{
    public static final double MAX_RATE_LIMIT = 10000000.0;

    public static final double MIN_RATE_LIMIT = 0.00000001;

    public void acquire();

    public static final class RateLimiterFactory
    {
        public static final RateLimiter create(final double rate)
        {
            if (rate <= 0.0)
            {
                return null;
            }
            return RateLimiter.create(Math.min(Math.max(rate, MIN_RATE_LIMIT), MAX_RATE_LIMIT));
        }

        public static final RateLimiter create(final Class<?> claz)
        {
            if (claz.isAnnotationPresent(RateLimit.class))
            {
                return create(claz.getAnnotation(RateLimit.class).value());
            }
            return null;
        }
    }
}
