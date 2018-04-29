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

package com.themodernway.server.core.test.util;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.ITimeSupplier;

public class BinderJava8POJO
{
    private String               m_name = "";

    private double               m_cost = 0d;

    private long                 m_time = ITimeSupplier.now();

    private Instant              m_inst = Instant.now();

    private Duration             m_dist = Duration.ofSeconds(60L);

    private Optional<BigInteger> m_valu = CommonOps.toOptional(BigInteger.valueOf(Long.MAX_VALUE));

    public BinderJava8POJO()
    {
    }

    public BinderJava8POJO(final String name)
    {
        m_name = name;
    }

    public BinderJava8POJO(final String name, final double cost)
    {
        m_name = name;

        m_cost = cost;
    }

    public Optional<BigInteger> getOptionalValue()
    {
        return m_valu;
    }

    public void setOptionalValue(final Optional<BigInteger> valu)
    {
        m_valu = valu;
    }

    public String getName()
    {
        return m_name;
    }

    public void setName(final String name)
    {
        m_name = name;
    }

    public Instant getInstant()
    {
        return m_inst;
    }

    public void setInstant(final Instant inst)
    {
        m_inst = inst;
    }

    public Duration getDuration()
    {
        return m_dist;
    }

    public void setDuration(final Duration dist)
    {
        m_dist = dist;
    }

    public double getCost()
    {
        return m_cost;
    }

    public void setCost(final double cost)
    {
        m_cost = cost;
    }

    public void setTime(final long time)
    {
        m_time = time;
    }

    public long getTime()
    {
        return m_time;
    }
}
