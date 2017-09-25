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

package com.themodernway.server.core.test.util;

import com.themodernway.common.api.types.IIdentified;
import com.themodernway.server.core.security.Authorized;

@Authorized(all={"ADMIN", "USER"}, not="ANON")
public class AuthAllPOJO implements IIdentified
{
    private final String m_id;

    public AuthAllPOJO(final String id)
    {
        m_id = id;
    }

    @Override
    public String getId()
    {
        return m_id;
    }
}
