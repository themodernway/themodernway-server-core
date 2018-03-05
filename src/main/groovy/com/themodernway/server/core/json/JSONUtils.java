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

package com.themodernway.server.core.json;

import static com.themodernway.common.api.java.util.CommonOps.nulled;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.themodernway.common.api.json.JSONType;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;

public final class JSONUtils
{
    public static final BigDecimal BIG_DECIMAL_MAX = BigDecimal.valueOf(Double.MAX_VALUE);

    public static final BigDecimal BIG_DECIMAL_MIN = BigDecimal.valueOf(-Double.MAX_VALUE);

    public static final BigInteger BIG_INTEGER_MAX = BigInteger.valueOf(Integer.MAX_VALUE);

    public static final BigInteger BIG_INTEGER_MIN = BigInteger.valueOf(Integer.MIN_VALUE);

    public static final BigInteger BIG_INTLONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    public static final BigInteger BIG_INTLONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);

    public static final BigDecimal BIG_DEC_INT_MAX = BigDecimal.valueOf(Integer.MAX_VALUE);

    public static final BigDecimal BIG_DEC_INT_MIN = BigDecimal.valueOf(Integer.MIN_VALUE);

    public static final BigDecimal BIG_DEC_LONGMAX = BigDecimal.valueOf(Long.MAX_VALUE);

    public static final BigDecimal BIG_DEC_LONGMIN = BigDecimal.valueOf(Long.MIN_VALUE);

    public static final BigInteger BIG_INT_DEC_MAX = BIG_DECIMAL_MAX.toBigInteger();

    public static final BigInteger BIG_INT_DEC_MIN = BIG_DECIMAL_MIN.toBigInteger();

    private static final IBinder   OBJECT_BINDER   = BinderType.JSON.getBinder();

    private static final IBinder   STRICT_BINDER   = BinderType.JSON.getBinder().setStrict();

    private JSONUtils()
    {
    }

    public static final String toJSONString(final Object value, final boolean strict)
    {
        try
        {
            return (strict ? STRICT_BINDER : OBJECT_BINDER).toString(value);
        }
        catch (final ParserException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    static final void writeObjectAsJSON(final Writer out, final Object object) throws IOException
    {
        try
        {
            OBJECT_BINDER.send(out, object);
        }
        catch (final ParserException e)
        {
            throw new IOException(e);
        }
    }

    static final void writeObjectAsJSON(final Writer out, final Object object, final boolean strict) throws IOException
    {
        try
        {
            (strict ? STRICT_BINDER : OBJECT_BINDER).send(out, object);
        }
        catch (final ParserException e)
        {
            throw new IOException(e);
        }
    }

    static final void writeObjectAsJSON(final OutputStream out, final Object object) throws IOException
    {
        try
        {
            OBJECT_BINDER.send(out, object);
        }
        catch (final ParserException e)
        {
            throw new IOException(e);
        }
    }

    static final void writeObjectAsJSON(final OutputStream out, final Object object, final boolean strict) throws IOException
    {
        try
        {
            (strict ? STRICT_BINDER : OBJECT_BINDER).send(out, object);
        }
        catch (final ParserException e)
        {
            throw new IOException(e);
        }
    }

    public static final boolean isInteger(final Object object)
    {
        if (null == object)
        {
            return false;
        }
        if (object instanceof Integer)
        {
            return true;
        }
        if (object instanceof Long)
        {
            final long value = ((Long) object);

            return (false == ((value > Integer.MAX_VALUE) || (value < Integer.MIN_VALUE)));
        }
        if (object instanceof BigInteger)
        {
            final BigInteger value = ((BigInteger) object);

            return (false == ((value.compareTo(BIG_INTEGER_MAX) > 0) || (value.compareTo(BIG_INTEGER_MIN) < 0)));
        }
        if (object instanceof Short)
        {
            return true;
        }
        return false;
    }

    public static final boolean isDoubleInfiniteOrNan(final Double dval)
    {
        return (dval.isInfinite() || dval.isNaN());
    }

    public static final boolean isFloatInfiniteOrNan(final Float fval)
    {
        return (fval.isInfinite() || fval.isNaN());
    }

    public static final boolean isDouble(final Object object)
    {
        if (null == object)
        {
            return false;
        }
        if (object instanceof Double)
        {
            return (false == (isDoubleInfiniteOrNan(((Double) object))));
        }
        if (object instanceof Float)
        {
            return (false == (isFloatInfiniteOrNan(((Float) object))));
        }
        if (object instanceof BigDecimal)
        {
            final BigDecimal value = ((BigDecimal) object);

            if ((value.compareTo(BIG_DECIMAL_MAX) > 0) || (value.compareTo(BIG_DECIMAL_MIN) < 0))
            {
                return false;
            }
            if (isDoubleInfiniteOrNan(value.doubleValue()))
            {
                return false;
            }
            return true;
        }
        return false;
    }

    public static final boolean isNumber(final Object object)
    {
        return (object instanceof Number) ? (isInteger(object) || isDouble(object)) : false;
    }

    public static final boolean isObject(final Object object)
    {
        return (object instanceof Map);
    }

    public static final boolean isArray(final Object object)
    {
        return (object instanceof List);
    }

    public static final boolean isString(final Object object)
    {
        return (object instanceof CharSequence);
    }

    public static final boolean isBoolean(final Object object)
    {
        return (object instanceof Boolean);
    }

    public static final boolean isDate(final Object object)
    {
        return (object instanceof Date);
    }

    public static final Integer asInteger(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof Integer)
        {
            return ((Integer) object);
        }
        if (object instanceof Long)
        {
            final Long value = ((Long) object);

            if ((value > Integer.MAX_VALUE) || (value < Integer.MIN_VALUE))
            {
                return nulled();
            }
            return value.intValue();
        }
        if (object instanceof Short)
        {
            return ((Short) object).intValue();
        }
        if (object instanceof BigInteger)
        {
            final BigInteger value = ((BigInteger) object);

            if ((value.compareTo(BIG_INTEGER_MAX) > 0) || (value.compareTo(BIG_INTEGER_MIN) < 0))
            {
                return nulled();
            }
            return value.intValue();
        }
        if (object instanceof BigDecimal)
        {
            final BigDecimal value = ((BigDecimal) object);

            if ((value.compareTo(BIG_DEC_INT_MAX) > 0) || (value.compareTo(BIG_DEC_INT_MIN) < 0))
            {
                return nulled();
            }
            return value.intValue();
        }
        if (object instanceof Number)
        {
            final long lval = ((Number) object).longValue();

            if ((lval > Integer.MAX_VALUE) || (lval < Integer.MIN_VALUE))
            {
                return nulled();
            }
            return ((int) lval);
        }
        return nulled();
    }

    public static final Long asLong(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof Long)
        {
            return ((Long) object);
        }
        if (object instanceof Integer)
        {
            return ((Integer) object).longValue();
        }
        if (object instanceof Double)
        {
            final Double dval = ((Double) object);

            if (isDoubleInfiniteOrNan(dval))
            {
                return nulled();
            }
            if ((dval.doubleValue() > Long.MAX_VALUE) || (dval.doubleValue() < Long.MIN_VALUE))
            {
                return nulled();
            }
            return dval.longValue();
        }
        if (object instanceof BigInteger)
        {
            final BigInteger value = ((BigInteger) object);

            if ((value.compareTo(BIG_INTLONG_MAX) > 0) || (value.compareTo(BIG_INTLONG_MIN) < 0))
            {
                return nulled();
            }
            return value.longValue();
        }
        if (object instanceof BigDecimal)
        {
            final BigDecimal value = ((BigDecimal) object);

            if ((value.compareTo(BIG_DEC_LONGMAX) > 0) || (value.compareTo(BIG_DEC_LONGMIN) < 0))
            {
                return nulled();
            }
            return value.longValue();
        }
        if (object instanceof Number)
        {
            return ((Number) object).longValue();
        }
        return nulled();
    }

    public static final Double asDouble(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof Double)
        {
            final Double dval = ((Double) object);

            if (isDoubleInfiniteOrNan(dval))
            {
                return nulled();
            }
            return dval;
        }
        if (object instanceof Float)
        {
            final Float fval = ((Float) object);

            if (isFloatInfiniteOrNan(fval))
            {
                return nulled();
            }
            return fval.doubleValue();
        }
        if (object instanceof BigDecimal)
        {
            final BigDecimal value = ((BigDecimal) object);

            if ((value.compareTo(BIG_DECIMAL_MAX) > 0) || (value.compareTo(BIG_DECIMAL_MIN) < 0))
            {
                return nulled();
            }
            if (isDoubleInfiniteOrNan(value.doubleValue()))
            {
                return nulled();
            }
            return value.doubleValue();
        }
        if (object instanceof BigInteger)
        {
            final BigInteger value = ((BigInteger) object);

            if ((value.compareTo(BIG_INT_DEC_MAX) > 0) || (value.compareTo(BIG_INT_DEC_MIN) < 0))
            {
                return nulled();
            }
            return value.doubleValue();
        }
        if (object instanceof Number)
        {
            final Double dval = ((Number) object).doubleValue();

            if (isDoubleInfiniteOrNan(dval))
            {
                return nulled();
            }
            return dval;
        }
        return nulled();
    }

    public static final Number asNumber(final Object object)
    {
        if (object instanceof Number)
        {
            if (isInteger(object))
            {
                return asInteger(object);
            }
            if (isDouble(object))
            {
                return asDouble(object);
            }
            return asLong(object);
        }
        return nulled();
    }

    public static final JSONArray asArray(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof JSONArray)
        {
            return ((JSONArray) object);
        }
        if (object instanceof List)
        {
            return new JSONArray((List<?>) object);
        }
        return nulled();
    }

    @SuppressWarnings("unchecked")
    public static final JSONObject asObject(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof JSONObject)
        {
            return ((JSONObject) object);
        }
        if (object instanceof Map)
        {
            return new JSONObject((Map<String, ?>) object);
        }
        return nulled();
    }

    public static final String asString(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof CharSequence)
        {
            return ((CharSequence) object).toString();
        }
        return nulled();
    }

    public static final Boolean asBoolean(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof Boolean)
        {
            return ((Boolean) object);
        }
        return nulled();
    }

    public static final Date asDate(final Object object)
    {
        if (null == object)
        {
            return nulled();
        }
        if (object instanceof Date)
        {
            return new Date(((Date) object).getTime());
        }
        return nulled();
    }

    public static final JSONType getJSONType(final Object object)
    {
        if (null == object)
        {
            return JSONType.NULL;
        }
        if (object instanceof CharSequence)
        {
            return JSONType.STRING;
        }
        if (object instanceof Number)
        {
            if (null != asNumber(object))
            {
                return JSONType.NUMBER;
            }
            return JSONType.UNDEFINED;
        }
        if (object instanceof Boolean)
        {
            return JSONType.BOOLEAN;
        }
        if (object instanceof Map)
        {
            return JSONType.OBJECT;
        }
        if (object instanceof List)
        {
            return JSONType.ARRAY;
        }
        if (object instanceof Date)
        {
            return JSONType.DATE;
        }
        return JSONType.UNDEFINED;
    }

    public static final boolean isJSONType(final Object object, final JSONType type)
    {
        return (type == getJSONType(object));
    }
}
