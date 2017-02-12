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

import java.util.List;

public interface IAuthorizer
{
    public static final int E_IS_VALIDATED = 0;

    public static final int E_DEAD_SESSION = 1;

    public static final int E_NO_USER_INFO = 2;

    public static final int E_NOT_ADMIN_ID = 3;

    public static final int E_NO_ANONYMOUS = 4;

    public static final int E_NO_VALIDROLE = 5;

    public static final int E_SERVER_ERROR = 500;

    public AuthorizationResult isAuthorized(Object target, List<String> roles);
}
