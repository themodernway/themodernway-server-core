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

package com.themodernway.server.core.mail;

import java.util.List;

import org.springframework.mail.MailMessage;

import com.themodernway.common.api.types.json.JSONStringify;

public interface ICoreSimpleMailMessage extends ISmartMailMessage, MailMessage, JSONStringify
{
    public List<String> getMailBccList();

    public List<String> getMailCcList();

    public List<String> getMailToList();

    public void setMailBccList(List<String> list);

    public void setMailBccList(String list);

    public void setMailCcList(List<String> list);

    public void setMailCcList(String list);

    public void setMailToList(List<String> list);

    public void setMailToList(String list);
}