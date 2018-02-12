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

import java.time.Instant;

public class BinderJava8POJO
{
    private String  m_name = "";

    private double  m_cost = 0d;

    private long    m_time = System.currentTimeMillis();

    private Instant m_inst = Instant.now();

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
