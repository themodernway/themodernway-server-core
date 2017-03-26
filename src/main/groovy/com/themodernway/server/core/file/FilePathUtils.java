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

package com.themodernway.server.core.file;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import com.themodernway.common.api.java.util.StringOps;

public final class FilePathUtils
{
    public static final String SINGLE_SLASH = "/";

    public static final String SINGLE_TILDE = "~";

    public static final String DOUBLE_SLASH = SINGLE_SLASH + SINGLE_SLASH;

    public static final String SLASHY_TILDE = SINGLE_SLASH + SINGLE_TILDE;

    public static final String TILDE_SLASHY = SINGLE_TILDE + SINGLE_SLASH;

    private FilePathUtils()
    {
    }

    public static final String patch(String path)
    {
        path = path.trim();

        while (path.contains(DOUBLE_SLASH))
        {
            path = path.replaceAll(DOUBLE_SLASH, SINGLE_SLASH).trim();
        }
        return path;
    }

    public static final String path(String path)
    {
        Path look = Paths.get(path);

        if (null != look)
        {
            look = look.normalize();

            if (null != look)
            {
                return (look.toString());
            }
        }
        return null;
    }

    public static final String trunk(String path)
    {
        path = patch(path);

        while (path.startsWith(SINGLE_SLASH))
        {
            path = path.substring(1).trim();
        }
        while (path.startsWith(TILDE_SLASHY))
        {
            path = path.substring(2).trim();
        }
        while (path.startsWith(SLASHY_TILDE))
        {
            path = SINGLE_SLASH + path.substring(2).trim();
        }
        while (path.startsWith(SINGLE_TILDE))
        {
            path = path.substring(1).trim();
        }
        int prfx = FilenameUtils.getPrefixLength(path);

        if (prfx > 0)
        {
            path = trunk(path.substring(prfx).trim());
        }
        return path(path);
    }

    public static final String normalize(String path)
    {
        path = StringOps.toTrimOrNull(path);

        if (null != path)
        {
            path = StringOps.toTrimOrNull(FilenameUtils.normalizeNoEndSeparator(patch(path)));

            if (null != path)
            {
                if (path.startsWith(TILDE_SLASHY))
                {
                    return normalize(path.substring(2));
                }
                if (path.startsWith(SLASHY_TILDE))
                {
                    return normalize(SINGLE_SLASH + path.substring(2));
                }
                if (path.startsWith(SINGLE_TILDE))
                {
                    return normalize(path.substring(1));
                }
                return path(path);
            }
        }
        return path;
    }

    public static final String concat(String path, String last)
    {
        return normalize(FilenameUtils.concat(normalize(path), normalize(trunk(last))));
    }
}
