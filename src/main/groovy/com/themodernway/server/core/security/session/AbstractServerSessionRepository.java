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

package com.themodernway.server.core.security.session;

import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;

@SuppressWarnings("serial")
public abstract class AbstractServerSessionRepository implements IServerSessionRepository
{
    private transient IServerSessionHelper m_helper;

    protected AbstractServerSessionRepository()
    {
        this(IServerSessionHelper.SP_DEFAULT_HELPER_INSTANCE);
    }

    protected AbstractServerSessionRepository(final IServerSessionHelper helper)
    {
        m_helper = CommonOps.requireNonNull(helper);
    }

    @Override
    public String getRealm()
    {
        return getHelper().getDefaultRealm();
    }

    @Override
    public boolean touch(final String id)
    {
        final IServerSession session = findById(id);

        if (null != session)
        {
            return session.touch();
        }
        return false;
    }

    @Override
    public boolean touch(final IServerSession session)
    {
        if (null != session)
        {
            return touch(session.getId());
        }
        return false;
    }

    @Override
    public boolean delete(final IServerSession session)
    {
        if (null != session)
        {
            deleteById(session.getId());

            return true;
        }
        return false;
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
