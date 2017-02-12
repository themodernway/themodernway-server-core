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

package com.themodernway.server.core.security.session;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("serial")
public abstract class AbstractServerSessionRepository implements IServerSessionRepository
{
    private final IServerSessionHelper m_helper;

    protected AbstractServerSessionRepository()
    {
        this(IServerSessionHelper.SP_DEFAULT_HELPER_INSTANCE);
    }

    protected AbstractServerSessionRepository(final IServerSessionHelper helper)
    {
        m_helper = Objects.requireNonNull(helper);
    }

    @Override
    public IServerSession createSession()
    {
        return null;
    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public String getDomain()
    {
        return getHelper().getDefaultDomain();
    }

    @Override
    public void touch(final String id)
    {
        getSession(id).touch();
    }

    @Override
    public void touch(final IServerSession session)
    {
        touch(session.getId());
    }

    @Override
    public void delete(final IServerSession session)
    {
        delete(session.getId());
    }

    @Override
    public int getDefaultMaxInactiveIntervalInSeconds()
    {
        return getHelper().getDefaultMaxInactiveIntervalInSeconds();
    }

    @Override
    public List<String> getDefaultRoles()
    {
        return getHelper().getDefaultRoles();
    }

    @Override
    public IServerSessionHelper getHelper()
    {
        return m_helper;
    }
}
