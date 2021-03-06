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

package com.themodernway.server.core;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.google.common.util.concurrent.RateLimiter;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.TaggingValues;
import com.themodernway.server.core.annotation.Annotations;
import com.themodernway.server.core.limiting.IRateLimited.RateLimiterFactory;
import com.themodernway.server.core.support.spring.IPropertiesResolver;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public interface ICoreCommon extends ICoreBase, IPropertiesResolver
{
    public static List<String> toTaggingValues(final Object target)
    {
        final TaggingValues tagging = Annotations.getAnnotation(target, TaggingValues.class);

        if (null == tagging)
        {
            return CommonOps.emptyList();
        }
        return StringOps.toUnique(tagging.value());
    }

    default String uuid()
    {
        return getServerContext().uuid();
    }

    default RateLimiter limiter(final double rate)
    {
        return RateLimiterFactory.create(rate);
    }

    default RateLimiter limiter(final Class<?> claz)
    {
        return RateLimiterFactory.create(claz);
    }

    default RateLimiter limiter(final double rate, final long warm, final TimeUnit unit)
    {
        return RateLimiterFactory.create(rate, warm, unit);
    }

    default String getOriginalBeanName(final String name)
    {
        return getServerContext().getOriginalBeanName(name);
    }

    @Override
    default String getPropertyByName(final String name)
    {
        return getServerContext().getPropertyByName(name);
    }

    @Override
    default String getPropertyByName(final String name, final String otherwise)
    {
        return getServerContext().getPropertyByName(name, otherwise);
    }

    @Override
    default String getPropertyByName(final String name, final Supplier<String> otherwise)
    {
        return getServerContext().getPropertyByName(name, otherwise);
    }

    @Override
    default String getResolvedExpression(final String expr)
    {
        return getServerContext().getResolvedExpression(expr);
    }

    @Override
    default String getResolvedExpression(final String expr, final String otherwise)
    {
        return getServerContext().getResolvedExpression(expr, otherwise);
    }

    @Override
    default String getResolvedExpression(final String expr, final Supplier<String> otherwise)
    {
        return getServerContext().getResolvedExpression(expr, otherwise);
    }

    default IServerContext getServerContext()
    {
        return ServerContextInstance.getServerContextInstance();
    }
}
