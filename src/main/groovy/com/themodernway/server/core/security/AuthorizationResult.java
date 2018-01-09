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

package com.themodernway.server.core.security;

import com.themodernway.common.api.java.util.StringOps;

public class AuthorizationResult implements IAuthorizationResult
{
    private final boolean m_authd;

    private final int     m_ecode;

    private final String  m_etext;

    public AuthorizationResult(final boolean authd, final int ecode, final String etext)
    {
        m_authd = authd;

        m_ecode = ecode;

        m_etext = StringOps.toTrimOrElse(etext, "unknown");
    }

    @Override
    public boolean isAuthorized()
    {
        return m_authd;
    }

    @Override
    public int getCode()
    {
        return m_ecode;
    }

    @Override
    public String getText()
    {
        return m_etext;
    }

    @Override
    public String toString()
    {
        return String.format("(%b, %d, '%s')", isAuthorized(), getCode(), StringOps.toTrimOrElse(getText(), "unknown"));
    }
}
