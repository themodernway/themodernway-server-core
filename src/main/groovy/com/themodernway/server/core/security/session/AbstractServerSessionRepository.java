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
import com.themodernway.common.api.types.Activatable;

@SuppressWarnings("serial")
public abstract class AbstractServerSessionRepository extends Activatable implements IServerSessionRepository
{
    private final IServerSessionHelper m_helper;

    protected AbstractServerSessionRepository()
    {
        this(IServerSessionHelper.SP_DEFAULT_HELPER_INSTANCE);
    }

    protected AbstractServerSessionRepository(final IServerSessionHelper helper)
    {
        m_helper = CommonOps.requireNonNull(helper);
    }

    @Override
    public String getDomain()
    {
        return getHelper().getDefaultDomain();
    }

    @Override
    public void touch(final String id)
    {
        final IServerSession session = findById(id);

        if (null != session)
        {
            session.touch();
        }
    }

    @Override
    public void touch(final IServerSession session)
    {
        touch(session.getId());
    }

    @Override
    public void delete(final IServerSession session)
    {
        deleteById(session.getId());
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
