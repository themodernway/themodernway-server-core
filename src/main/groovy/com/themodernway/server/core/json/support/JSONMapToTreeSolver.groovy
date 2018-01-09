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

package com.themodernway.server.core.json.support

import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.server.core.json.JSONObject

import groovy.transform.CompileStatic

@CompileStatic
public class JSONMapToTreeSolver implements JSONTrait
{
    private Set<String>         m_keys

    private List<String>        m_incl

    private List<String>        m_excl

    private List<Map>           m_rows = []

    private Map<String, Map>    m_spec = [:]

    public JSONMapToTreeSolver(final Map spec)
    {
        this([CommonOps.requireNonNull(spec)])
    }

    public JSONMapToTreeSolver(final List<Map> spec)
    {
        CommonOps.requireNonNull(spec)

        spec.each { Map cols ->

            def parent = cols['parent'] as String

            if (null == m_spec[parent])
            {
                m_spec[parent] = [linked: cols['linked'] as String, column: cols['column'] as String, values: []]
            }
        }
        m_keys = m_spec.keySet()
    }

    public List<String> getIncluded()
    {
        m_incl
    }

    public JSONMapToTreeSolver setIncluded(final List<String> incl)
    {
        m_incl = incl

        this
    }

    public List<String> getExcluded()
    {
        m_excl
    }

    public JSONMapToTreeSolver setExcluded(final List<String> excl)
    {
        m_excl = excl

        this
    }

    public List<JSONObject> solve(Closure c = null)
    {
        List list = []

        List incl = getIncluded()

        if ((incl) && (incl.size() < 1))
        {
            incl = null
        }
        List excl = getExcluded()

        if ((excl) && (excl.size() < 1))
        {
            excl = null
        }
        m_rows.each { Map jrow ->

            if (jrow)
            {
                if (incl)
                {
                    jrow = jrow.subMap(incl)
                }
                if (excl)
                {
                    jrow.keySet().removeAll(excl)
                }
                if (jrow.size() > 0)
                {
                    def good = true

                    if (c)
                    {
                        def resp = c.call(jrow)

                        if (resp instanceof Boolean)
                        {
                            good = resp
                        }
                    }
                    if ((good) && (jrow.size() > 0))
                    {
                        list << json(jrow)
                    }
                }
            }
        }
        m_rows.clear()

        list
    }

    public JSONObject solve(final String name, Closure c = null)
    {
        json(CommonOps.requireNonNull(name), solve(c))
    }

    public void add(final Map jrow)
    {
        if ((jrow) && (jrow.size() > 0))
        {
            m_rows << jrow

            m_keys.each { pkey ->

                if (keys(pkey, jrow))
                {
                    return
                }
            }
        }
    }

    private boolean keys(final String pkey, final Map jrow)
    {
        def look = m_spec[pkey]

        if (look)
        {
            def valu = jrow[pkey]

            def list = look['values'] as List

            if (false == list.contains(valu))
            {
                list << valu

                return true
            }
        }
        false
    }

    public void leftShift(final Map jrow)
    {
        add(jrow)
    }
}
