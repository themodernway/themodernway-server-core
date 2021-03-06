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

import java.util.Date;
import java.util.List;

import com.themodernway.common.api.types.IBuilder;

public interface ISimpleMailMessageBuilder extends IBuilder<CoreSimpleMailMessage>
{
    public ISimpleMailMessageBuilder to(List<String> list);

    public ISimpleMailMessageBuilder cc(List<String> list);

    public ISimpleMailMessageBuilder bcc(List<String> list);

    public ISimpleMailMessageBuilder date(Date valu);

    public ISimpleMailMessageBuilder from(String valu);

    public ISimpleMailMessageBuilder text(String valu);

    public ISimpleMailMessageBuilder reply(String valu);

    public ISimpleMailMessageBuilder subject(String valu);
}
