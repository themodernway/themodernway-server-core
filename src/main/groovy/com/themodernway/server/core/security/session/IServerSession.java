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

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

import org.springframework.session.Session;

import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONObjectSupplier;

public interface IServerSession extends Session, JSONObjectSupplier, Serializable
{
    public static final long     DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS  = 1800L;

    public static final Duration DEFAULT_MAX_INACTIVE_INTERVAL_DURATION = Duration.ofSeconds(DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS);

    public String getOriginalId();

    public String setOriginalId(String id);

    public String getRealm();

    public String getStatus();

    public String getUserId();

    public boolean save();

    public boolean touch();

    public boolean isPersisted();

    public boolean setPersisted(boolean persisted);

    public List<String> getRoles();

    public JSONObject getMetaData();

    public IServerSessionHelper getHelper();

    public IServerSessionRepository getRepository();
}
