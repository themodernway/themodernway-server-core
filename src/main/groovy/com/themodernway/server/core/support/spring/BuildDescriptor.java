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

package com.themodernway.server.core.support.spring;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;

public final class BuildDescriptor implements IBuildDescriptor
{
    private static final String UNKNOWN                    = "unknown".intern();

    private final String        m_name_space;

    private String              m_build_user               = UNKNOWN;

    private String              m_build_date               = UNKNOWN;

    private String              m_java_version             = UNKNOWN;

    private String              m_build_server_host        = UNKNOWN;

    private String              m_build_server_build       = UNKNOWN;

    private String              m_build_module_version     = UNKNOWN;

    private String              m_build_git_commit_hash    = UNKNOWN;

    private String              m_build_git_commit_user    = UNKNOWN;

    private String              m_build_git_commit_message = UNKNOWN;

    protected static final String doValidatePropValue(final String valu)
    {
        if (null == StringOps.toTrimOrNull(valu))
        {
            return UNKNOWN;
        }
        if (valu.startsWith("@GRADLE"))
        {
            return valu;
        }
        return valu;
    }

    public BuildDescriptor(final String name_space)
    {
        m_name_space = StringOps.requireTrimOrNull(name_space);
    }

    @Override
    public String getNameSpace()
    {
        return m_name_space;
    }

    @Override
    public String getBuildUser()
    {
        return m_build_user;
    }

    public void setBuildUser(final String value)
    {
        m_build_user = doValidatePropValue(value);
    }

    @Override
    public String getBuildDate()
    {
        return m_build_date;
    }

    public void setBuildDate(final String value)
    {
        m_build_date = doValidatePropValue(value);
    }

    @Override
    public String getJavaVersion()
    {
        return m_java_version;
    }

    public void setJavaVersion(final String value)
    {
        m_java_version = doValidatePropValue(value);
    }

    @Override
    public String getBuildServerHost()
    {
        return m_build_server_host;
    }

    public void setBuildServerHost(final String value)
    {
        m_build_server_host = doValidatePropValue(value);
    }

    @Override
    public String getBuildServerBuild()
    {
        return m_build_server_build;
    }

    public void setBuildServerBuild(final String value)
    {
        m_build_server_build = doValidatePropValue(value);
    }

    @Override
    public String getBuildGITCommitHash()
    {
        return m_build_git_commit_hash;
    }

    public void setBuildGITCommitHash(final String value)
    {
        m_build_git_commit_hash = doValidatePropValue(value);
    }

    @Override
    public String getBuildGITCommitUser()
    {
        return m_build_git_commit_user;
    }

    public void setBuildGITCommitUser(final String value)
    {
        m_build_git_commit_user = doValidatePropValue(value);
    }

    @Override
    public String getBuildGITCommitMessage()
    {
        return m_build_git_commit_message;
    }

    public void setBuildGITCommitMessage(final String value)
    {
        m_build_git_commit_message = doValidatePropValue(value);
    }

    @Override
    public String getBuildModuleVersion()
    {
        return m_build_module_version;
    }

    public void setBuildModuleVersion(final String value)
    {
        m_build_module_version = doValidatePropValue(value);
    }

    @Override
    public JSONObject toJSONObject()
    {
        final JSONObject json = new JSONObject();

        json.put("name_space", getNameSpace());

        json.put("build_user", getBuildUser());

        json.put("build_date", getBuildDate());

        json.put("java_version", getJavaVersion());

        json.put("build_git_commit_hash", getBuildGITCommitHash());

        json.put("build_git_commit_user", getBuildGITCommitUser());

        json.put("build_git_commit_message", getBuildGITCommitMessage());

        json.put("build_server_host", getBuildServerHost());

        json.put("build_server_build", getBuildServerBuild());

        json.put("build_module_version", getBuildModuleVersion());

        return json;
    }
}
