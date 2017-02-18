/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
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

package com.themodernway.server.core.security;

import com.themodernway.common.api.java.util.StringOps;

public class AuthorizationResult
{
    private final boolean m_authd;

    private final boolean m_admin;

    private final int     m_ecode;

    private final String  m_etext;

    public AuthorizationResult(final boolean authd, final boolean admin, final int ecode, final String etext)
    {
        m_authd = authd;

        m_admin = admin;

        m_ecode = ecode;

        m_etext = doFixText(etext);
    }

    private final String doFixText(String etext)
    {
        etext = StringOps.toTrimOrNull(etext);

        if (null != etext)
        {
            return etext;
        }
        return "unknown";
    }

    public boolean isAuthorized()
    {
        return m_authd;
    }

    public boolean isAdmin()
    {
        return m_admin;
    }

    public int getCode()
    {
        return m_ecode;
    }

    public String getText()
    {
        return m_etext;
    }
}
