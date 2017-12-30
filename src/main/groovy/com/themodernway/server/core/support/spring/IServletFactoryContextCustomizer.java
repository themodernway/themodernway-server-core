/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

package com.themodernway.server.core.support.spring;

import java.util.List;

import com.themodernway.server.core.servlet.IServletResponseErrorCodeManager;
import com.themodernway.server.core.servlet.ISessionIDFromRequestExtractor;

public interface IServletFactoryContextCustomizer extends IServletContextCustomizer
{
    public void setServletFactory(IServletFactory fact);

    public void setRateLimit(double rate);

    public double getRateLimit();

    public void setLoadOnStartup(int load);

    public int getLoadOnStartup();

    public String getServletName();

    public String[] getMappings();

    public List<String> getRequiredRoles();

    public void setRequiredRoles(String roles);

    public void setRequiredRoles(List<String> roles);

    public ISessionIDFromRequestExtractor getSessionIDFromRequestExtractor();

    public void setSessionIDFromRequestExtractor(ISessionIDFromRequestExtractor extractor);

    public void setServletResponseErrorCodeManager(IServletResponseErrorCodeManager manager);

    public IServletResponseErrorCodeManager getServletResponseErrorCodeManager();
}