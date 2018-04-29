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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.INativeFunction;
import com.themodernway.common.api.types.ParserException;
import com.themodernway.common.api.types.json.JSONType;
import com.themodernway.server.core.CoreThrowables;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;

public final class JSONUtils
{
    public static final String     JSON_OBJECT_DEFAULT_ARRAY_NAME = "list";

    public static final String     JSON_OBJECT_DEFAULT_VALUE_NAME = "value";

    public static final BigDecimal BIG_DECIMAL_MAX                = BigDecimal.valueOf(Double.MAX_VALUE);

    public static final BigDecimal BIG_DECIMAL_MIN                = BigDecimal.valueOf(-Double.MAX_VALUE);

    public static final BigInteger BIG_INTEGER_MAX                = BigInteger.valueOf(Integer.MAX_VALUE);

    public static final BigInteger BIG_INTEGER_MIN                = BigInteger.valueOf(Integer.MIN_VALUE);

    public static final BigInteger BIG_INTLONG_MAX                = BigInteger.valueOf(Long.MAX_VALUE);

    public static final BigInteger BIG_INTLONG_MIN                = BigInteger.valueOf(Long.MIN_VALUE);

    public static final BigDecimal BIG_DEC_INT_MAX                = BigDecimal.valueOf(Integer.MAX_VALUE);

    public static final BigDecimal BIG_DEC_INT_MIN                = BigDecimal.valueOf(Integer.MIN_VALUE);

    public static final BigDecimal BIG_DEC_LONGMAX                = BigDecimal.valueOf(Long.MAX_VALUE);

    public static final BigDecimal BIG_DEC_LONGMIN                = BigDecimal.valueOf(Long.MIN_VALUE);

    public static final BigInteger BIG_INT_DEC_MAX                = BIG_DECIMAL_MAX.toBigInteger();

    public static final BigInteger BIG_INT_DEC_MIN                = BIG_DECIMAL_MIN.toBigInteger();

    private static final IBinder   OBJECT_BINDER                  = BinderType.JSON.getBinder();

    private static final IBinder   STRICT_BINDER                  = BinderType.JSON.getBinder().setStrict();

    private JSONUtils()
    {
    }

    public static final <T> T asType(final Object self, final Class<T> type)
    {
        if (null == self)
        {
            return CommonOps.NULL();
        }
        if (type.isAssignableFrom(self.getClass()))
        {
            return type.cast(self);
        }
        try
        {
            if (String.class.equals(type) || CharSequence.class.equals(type))
            {
                return CommonOps.CAST(OBJECT_BINDER.toString(self));
            }
            if (OBJECT_BINDER.canSerializeClass(type))
            {
                final T valu = OBJECT_BINDER.convert(self, type);

                if (valu != null)
                {
                    return valu;
                }
            }
        }
        catch (final ParserException e)
        {
            CoreThrowables.handle(e);
        }
        throw new ClassCastException(self.getClass().getName() + " cannot be coerced into " + type.getName());
    }

    public static final JSONObject json(final Collection<? extends JSONObjectSupplier> source)
    {
        return new JSONObject(source);
    }

    public static final JSONObject json(final String name, final Collection<? extends JSONObjectSupplier> source)
    {
        return new JSONObject(name, source);
    }

    public static final JSONObject deep(final JSONObject object)
    {
        if (null == object)
        {
            return CommonOps.NULL();
        }
        if (object.isEmpty())
        {
            return new JSONObject();
        }
        try
        {
            return OBJECT_BINDER.bindJSON(OBJECT_BINDER.toString(object));
        }
        catch (final ParserException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public static final JSONArray deep(final JSONArray array)
    {
        if (null == array)
        {
            return CommonOps.NULL();
        }
        if (array.isEmpty())
        {
            return new JSONArray();
        }
        try
        {
            return OBJECT_BINDER.bindJSON(OBJECT_BINDER.toString(new JSONObject(JSON_OBJECT_DEFAULT_ARRAY_NAME, array))).getAsArray(JSON_OBJECT_DEFAULT_ARRAY_NAME);
        }
        catch (final ParserException e)
        {
            throw new IllegalArgumentException(e);
        }
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

    public static final boolean isDoubleInfiniteOrNan(final Double dval)
    {
        return (dval.isInfinite() || dval.isNaN());
    }

    public static final boolean isFloatInfiniteOrNan(final Float fval)
    {
        return (fval.isInfinite() || fval.isNaN());
    }

    public static final boolean isInteger(final Object object)
    {
        return (null != asInteger(object));
    }

    public static final boolean isDouble(final Object object)
    {
        return (null != asDouble(object));
    }

    public static final boolean isNumber(final Object object)
    {
        return (null != asNumber(object));
    }

    public static final boolean isLong(final Object object)
    {
        return (null != asLong(object));
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

    public static final boolean isNativeFunction(final Object object)
    {
        return false;
    }

    public static final Integer asInteger(final Object object)
    {
        if (object instanceof Number)
        {
            if (object instanceof Integer)
            {
                return ((Integer) object);
            }
            if (object instanceof Long)
            {
                final Long value = ((Long) object);

                return (((value > Integer.MAX_VALUE) || (value < Integer.MIN_VALUE)) ? CommonOps.NULL() : value.intValue());
            }
            if (object instanceof Short)
            {
                return ((Short) object).intValue();
            }
            if (object instanceof BigInteger)
            {
                final BigInteger value = ((BigInteger) object);

                return (((value.compareTo(BIG_INTEGER_MAX) > 0) || (value.compareTo(BIG_INTEGER_MIN) < 0)) ? CommonOps.NULL() : value.intValue());
            }
            if (object instanceof BigDecimal)
            {
                final BigDecimal value = ((BigDecimal) object);

                return (((value.compareTo(BIG_DEC_INT_MAX) > 0) || (value.compareTo(BIG_DEC_INT_MIN) < 0)) ? CommonOps.NULL() : value.intValue());
            }
            final long lval = ((Number) object).longValue();

            return (((lval > Integer.MAX_VALUE) || (lval < Integer.MIN_VALUE)) ? CommonOps.NULL() : ((int) lval));
        }
        return CommonOps.NULL();
    }

    public static final Long asLong(final Object object)
    {
        if (object instanceof Number)
        {
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

                return (((isDoubleInfiniteOrNan(dval)) || ((dval.doubleValue() > Long.MAX_VALUE) || (dval.doubleValue() < Long.MIN_VALUE))) ? CommonOps.NULL() : dval.longValue());
            }
            if (object instanceof BigInteger)
            {
                final BigInteger value = ((BigInteger) object);

                return (((value.compareTo(BIG_INTLONG_MAX) > 0) || (value.compareTo(BIG_INTLONG_MIN) < 0)) ? CommonOps.NULL() : value.longValue());
            }
            if (object instanceof BigDecimal)
            {
                final BigDecimal value = ((BigDecimal) object);

                return (((value.compareTo(BIG_DEC_LONGMAX) > 0) || (value.compareTo(BIG_DEC_LONGMIN) < 0)) ? CommonOps.NULL() : value.longValue());
            }
            return ((Number) object).longValue();
        }
        return CommonOps.NULL();
    }

    public static final Double asDouble(final Object object)
    {
        if (object instanceof Number)
        {
            if (object instanceof Double)
            {
                final Double dval = ((Double) object);

                return ((isDoubleInfiniteOrNan(dval)) ? CommonOps.NULL() : dval);
            }
            if (object instanceof Float)
            {
                final Float fval = ((Float) object);

                return ((isFloatInfiniteOrNan(fval)) ? CommonOps.NULL() : fval.doubleValue());
            }
            if (object instanceof BigDecimal)
            {
                final BigDecimal value = ((BigDecimal) object);

                return ((((value.compareTo(BIG_DECIMAL_MAX) > 0) || (value.compareTo(BIG_DECIMAL_MIN) < 0)) || (isDoubleInfiniteOrNan(value.doubleValue()))) ? CommonOps.NULL() : value.doubleValue());
            }
            if (object instanceof BigInteger)
            {
                final BigInteger value = ((BigInteger) object);

                return (((value.compareTo(BIG_INT_DEC_MAX) > 0) || (value.compareTo(BIG_INT_DEC_MIN) < 0)) ? CommonOps.NULL() : value.doubleValue());
            }
            final Double dval = ((Number) object).doubleValue();

            return ((isDoubleInfiniteOrNan(dval)) ? CommonOps.NULL() : dval);
        }
        return CommonOps.NULL();
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
        return CommonOps.NULL();
    }

    static final JSONArray toJSONArray(final List<?> list)
    {
        return ((list instanceof JSONArray) ? ((JSONArray) list) : new JSONArray(list));
    }

    public static final JSONArray asArray(final Object object)
    {
        return ((object instanceof List) ? toJSONArray((List<?>) object) : CommonOps.NULL());
    }

    static final JSONObject toJSONObject(final Map<String, ?> json)
    {
        return ((json instanceof JSONObject) ? ((JSONObject) json) : new JSONObject(json));
    }

    @SuppressWarnings("unchecked")
    public static final JSONObject asObject(final Object object)
    {
        return ((object instanceof Map) ? toJSONObject((Map<String, ?>) object) : CommonOps.NULL());
    }

    public static final String asString(final Object object)
    {
        return ((object instanceof CharSequence) ? ((CharSequence) object).toString() : CommonOps.NULL());
    }

    public static final Boolean asBoolean(final Object object)
    {
        return ((object instanceof Boolean) ? ((Boolean) object) : CommonOps.NULL());
    }

    public static final Date asDate(final Object object)
    {
        return ((object instanceof Date) ? new Date(((Date) object).getTime()) : CommonOps.NULL());
    }

    public static final <T> INativeFunction<T> asNativeFunction(final Object object)
    {
        return CommonOps.NULL();
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
        if (object instanceof Map)
        {
            return JSONType.OBJECT;
        }
        if (object instanceof List)
        {
            return JSONType.ARRAY;
        }
        if (object instanceof Boolean)
        {
            return JSONType.BOOLEAN;
        }
        if (object instanceof Date)
        {
            return JSONType.DATE;
        }
        return (isNumber(object) ? JSONType.NUMBER : JSONType.UNDEFINED);
    }

    public static final boolean isJSONType(final Object object, final JSONType type)
    {
        return (type == getJSONType(object));
    }
}
