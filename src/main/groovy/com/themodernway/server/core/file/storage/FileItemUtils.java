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

package com.themodernway.server.core.file.storage;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import com.themodernway.common.api.java.util.StringOps;

public final class FileItemUtils
{
    private FileItemUtils()
    {
    }

    public static final String patch(String path)
    {
        return path.trim().replaceAll("//", "/").trim();
    }

    public static final String trunk(String path)
    {
        path = patch(path);

        while (path.startsWith("/"))
        {
            path = path.substring(1).trim();
        }
        return path;
    }

    public static final String parent(String path)
    {
        path = normalize(path);

        if (null != path)
        {
            Path look = Paths.get(path);

            if (null != look)
            {
                look = look.getParent();

                if (null != look)
                {
                    return look.toString();
                }
            }
        }
        return null;
    }

    public static final String normalize(String path)
    {
        path = StringOps.toTrimOrNull(path);

        if (null != path)
        {
            path = StringOps.toTrimOrNull(FilenameUtils.normalizeNoEndSeparator(patch(path)));

            if ((null != path) && (path.startsWith("~/")))
            {
                return normalize(path.substring(2));
            }
        }
        return path;
    }

    public static final String concat(String path, String last)
    {
        return normalize(FilenameUtils.concat(normalize(path), trunk(last)));
    }
}
