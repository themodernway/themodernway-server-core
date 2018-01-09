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

import java.util.Arrays;
import java.util.List;

public class BindeListPOJO
{
    private String       m_name = "";

    private List<String> m_scrp = Arrays.asList("a", "b");

    private double       m_cost = 0d;

    private long         m_time = System.currentTimeMillis();

    public BindeListPOJO()
    {
    }

    public BindeListPOJO(final String name)
    {
        m_name = name;
    }

    public BindeListPOJO(final String name, final double cost)
    {
        m_name = name;

        m_cost = cost;
    }

    public List<String> getValue()
    {
        return m_scrp;
    }

    public void setValue(final List<String> scrp)
    {
        m_scrp = scrp;
    }

    public String getName()
    {
        return m_name;
    }

    public void setName(final String name)
    {
        m_name = name;
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
