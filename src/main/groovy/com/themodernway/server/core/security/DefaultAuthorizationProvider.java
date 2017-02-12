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

import java.io.IOException;
import java.util.List;

public class DefaultAuthorizationProvider implements IAuthorizationProvider
{
    @Override
    public AuthorizationResult isAuthorized(final Object target, final List<String> roles)
    {
        if (null == target)
        {
            return new AuthorizationResult(false, false, E_SERVER_ERROR, "null target");
        }
        if (null == roles)
        {
            return new AuthorizationResult(false, false, E_SERVER_ERROR, "null roles");
        }
        if (roles.isEmpty())
        {
            return new AuthorizationResult(false, false, E_SERVER_ERROR, "empty roles");
        }
        if (target instanceof IAuthorizedObject)
        {
            return ((IAuthorizedObject) target).isAuthorized(roles);
        }
        final Authorized authorized = target.getClass().getAnnotation(Authorized.class);

        if (null == authorized)
        {
            return new AuthorizationResult(true, false, E_IS_VALIDATED, "valid");
        }
        for (String type : authorized.value())
        {
            if (false == roles.contains(type))
            {
                return new AuthorizationResult(false, false, E_NO_VALIDROLE, type);
            }
        }
        return new AuthorizationResult(true, false, E_IS_VALIDATED, "valid");
    }

    @Override
    public void close() throws IOException
    {
    }
}
