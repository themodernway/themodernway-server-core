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

package com.themodernway.server.core.json;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.themodernway.server.core.io.NoSyncStringBuilderWriter;
import com.themodernway.server.core.json.binder.JSONBinder;

public final class JSONUtils
{
    private static final Logger                         logger          = Logger.getLogger(JSONUtils.class);

    private static final String                         NULL_FOR_OUTPUT = "null".intern();

    private final static BigDecimal                     BIG_DECIMAL_MAX = BigDecimal.valueOf(Double.MAX_VALUE);

    private final static BigDecimal                     BIG_DECIMAL_MIN = BigDecimal.valueOf(Double.MIN_VALUE);

    private final static BigInteger                     BIG_INTEGER_MAX = BigInteger.valueOf(Integer.MAX_VALUE);

    private final static BigInteger                     BIG_INTEGER_MIN = BigInteger.valueOf(Integer.MIN_VALUE);

    private final static BigInteger                     BIG_INTLONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private final static BigInteger                     BIG_INTLONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);

    private final static BigDecimal                     BIG_DEC_INT_MAX = BigDecimal.valueOf(Integer.MAX_VALUE);

    private final static BigDecimal                     BIG_DEC_INT_MIN = BigDecimal.valueOf(Integer.MIN_VALUE);

    private final static BigDecimal                     BIG_DEC_LONGMAX = BigDecimal.valueOf(Long.MAX_VALUE);

    private final static BigDecimal                     BIG_DEC_LONGMIN = BigDecimal.valueOf(Long.MIN_VALUE);

    private final static BigInteger                     BIG_INT_DEC_MAX = BIG_DECIMAL_MAX.toBigInteger();

    private final static BigInteger                     BIG_INT_DEC_MIN = BIG_DECIMAL_MIN.toBigInteger();

    private final static int                            INDENT_ADDED    = 4;

    private static final LinkedHashMap<Integer, String> INDENT_CACHE    = new LinkedHashMap<Integer, String>();

    protected JSONUtils()
    {
    }

    public static final <T> T NULL()
    {
        return null;
    }

    public static final String toJSONString(final Object value, final boolean strict)
    {
        return toJSONString(value, null, strict);
    }

    public static final String toJSONString(final Object value, final IJSONContext context, final boolean strict)
    {
        if (null == value)
        {
            return NULL_FOR_OUTPUT;
        }
        final NoSyncStringBuilderWriter out = new NoSyncStringBuilderWriter();

        try
        {
            writeJSONString(value, out, context, strict);
        }
        catch (IOException e)
        {
            // We should never get an IOException on a JSONStringWriter!!! ( I hope )

            throw new RuntimeException(e);
        }
        return out.toString();
    }

    public static final synchronized String getIndent(final Integer indent)
    {
        if ((null == indent) || (indent < 1))
        {
            return "";
        }
        String find = INDENT_CACHE.get(indent);

        if (null != find)
        {
            return find;
        }
        final StringBuilder b = new StringBuilder(indent);

        for (int i = 0; i < indent; i++)
        {
            b.append("-");
        }
        find = b.toString();

        INDENT_CACHE.put(indent, find);

        return find;
    }

    public static final String getLineNumber(final int lineno)
    {
        return String.format("%1$-4d", lineno).replaceAll(" ", "-");
    }

    public static final String dumpClassNamesToString(final Object o)
    {
        final NoSyncStringBuilderWriter w = new NoSyncStringBuilderWriter();

        dumpClassNames(o, new PrintWriter(w));

        return w.toString();
    }

    public static final void dumpClassNames(final Object o)
    {
        dumpClassNames(o, System.out);
    }

    public static final void dumpClassNames(final Object o, final PrintStream stream)
    {
        dumpClassNames(o, new PrintWriter(stream), 0, 0);
    }

    public static final void dumpClassNames(final Object o, final PrintWriter writer)
    {
        dumpClassNames(o, writer, 0, 0);
    }

    @SuppressWarnings("unchecked")
    public static final int dumpClassNames(final Object o, final PrintWriter writer, final int indent, int lineno)
    {
        if (null != o)
        {
            if (o instanceof List)
            {
                lineno = dumpClassNames((List<?>) o, writer, indent + INDENT_ADDED, lineno);
            }
            else if (o instanceof Map)
            {
                lineno = dumpClassNames((Map<String, ?>) o, writer, indent + INDENT_ADDED, lineno);
            }
            else if (o instanceof Collection)
            {
                lineno = dumpClassNames((Collection<?>) o, writer, indent + INDENT_ADDED, lineno);
            }
            else
            {
                lineno++;

                writer.println(getLineNumber(lineno) + getIndent(indent) + "Unnown=" + o.getClass().getName());
            }
        }
        return lineno;
    }

    @SuppressWarnings("unchecked")
    public static final int dumpClassNames(final List<?> list, final PrintWriter writer, final int indent, int lineno)
    {
        if (null != list)
        {
            final int size = list.size();

            final String p = getIndent(indent);

            final String t = list.getClass().getName();

            for (int i = 0; i < size; i++)
            {
                lineno++;

                final Object o = list.get(i);

                if (null == o)
                {
                    writer.println(getLineNumber(lineno) + p + t + "[" + i + "]=null");
                }
                else
                {
                    writer.println(getLineNumber(lineno) + p + t + "[" + i + "]=" + o.getClass().getName());

                    if (o instanceof List)
                    {
                        lineno = dumpClassNames((List<?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                    else if (o instanceof Map)
                    {
                        lineno = dumpClassNames((Map<String, ?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                    else if (o instanceof Collection)
                    {
                        lineno = dumpClassNames((Collection<?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                }
            }
        }
        return lineno;
    }

    @SuppressWarnings("unchecked")
    public static final int dumpClassNames(final Collection<?> collection, final PrintWriter writer, final int indent, int lineno)
    {
        if (null != collection)
        {
            final ArrayList<Object> list = new ArrayList<Object>(collection);

            final int size = list.size();

            final String t = collection.getClass().getName();

            final String p = getIndent(indent);

            for (int i = 0; i < size; i++)
            {
                lineno++;

                final Object o = list.get(i);

                if (null == o)
                {
                    writer.println(getLineNumber(lineno) + p + t + "[" + i + "]=null");
                }
                else
                {
                    writer.println(getLineNumber(lineno) + p + t + "[" + i + "]=" + o.getClass().getName());

                    if (o instanceof List)
                    {
                        lineno = dumpClassNames((List<?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                    else if (o instanceof Map)
                    {
                        lineno = dumpClassNames((Map<String, ?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                    else if (o instanceof Collection)
                    {
                        lineno = dumpClassNames((Collection<?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                }
            }
        }
        return lineno;
    }

    @SuppressWarnings("unchecked")
    public static final int dumpClassNames(final Map<String, ?> map, final PrintWriter writer, final int indent, int lineno)
    {
        if (null != map)
        {
            final String p = getIndent(indent);

            final String t = map.getClass().getName();

            for (String k : map.keySet())
            {
                lineno++;

                final Object o = map.get(k);

                if (null == o)
                {
                    writer.println(getLineNumber(lineno) + p + t + "[" + k + "]=null");
                }
                else
                {
                    writer.println(getLineNumber(lineno) + p + t + "[" + k + "]=" + o.getClass().getName());

                    if (o instanceof List)
                    {
                        lineno = dumpClassNames((List<?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                    else if (o instanceof Map)
                    {
                        lineno = dumpClassNames((Map<String, ?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                    else if (o instanceof Collection)
                    {
                        lineno = dumpClassNames((Collection<?>) o, writer, indent + INDENT_ADDED, lineno);
                    }
                }
            }
        }
        return lineno;
    }

    public static final void writeJSONString(final Object value, final Writer out, final IJSONContext context, final boolean strict) throws IOException
    {
        if (null == value)
        {
            out.write(NULL_FOR_OUTPUT);

            return;
        }
        if (value instanceof String)
        {
            out.write('\"');

            escape(value.toString(), out);

            out.write('\"');

            return;
        }
        if (value instanceof Number)
        {
            if (value instanceof Double)
            {
                final Double dval = ((Double) value);

                if (isDoubleInfiniteOrNan(dval))
                {
                    out.write(NULL_FOR_OUTPUT);
                }
                else
                {
                    out.write(dval.toString());
                }
                return;
            }
            if (value instanceof Integer)
            {
                out.write(value.toString());

                return;
            }
            if (value instanceof Long)
            {
                if (strict)
                {
                    final Integer thunk = asInteger(value);

                    if (null != thunk)
                    {
                        out.write(thunk.toString());
                    }
                    else
                    {
                        out.write(NULL_FOR_OUTPUT);
                    }
                }
                else
                {
                    out.write(value.toString());
                }
                return;
            }
            if (value instanceof Float)
            {
                final Float fval = ((Float) value);

                if (isFloatInfiniteOrNan(fval))
                {
                    out.write(NULL_FOR_OUTPUT);
                }
                else
                {
                    out.write(fval.toString());
                }
                return;
            }
            final Number nval = asNumber(value);

            if (null != nval)
            {
                out.write(nval.toString());
            }
            else
            {
                out.write(NULL_FOR_OUTPUT);
            }
            return;
        }
        if (value instanceof Boolean)
        {
            out.write(value.toString());

            return;
        }
        if (value instanceof IJSONStreamAware)
        {
            ((IJSONStreamAware) value).writeJSONString(out, context, strict);

            return;
        }
        if (value instanceof Map)
        {
            JSONObject.writeJSONString((Map<?, ?>) value, out, context, strict);

            return;
        }
        if (value instanceof List)
        {
            JSONArray.writeJSONString((List<?>) value, out, context, strict);

            return;
        }
        if (value instanceof Date)
        {
            out.write('\"');

            escape(format((Date) value, context), out);

            out.write('\"');

            return;
        }
        if (value instanceof Collection)
        {
            JSONArray.writeJSONString((Collection<?>) value, out, context, strict);

            return;
        }
        try
        {
            final JSONBinder binder = new JSONBinder();

            if (binder.canSerializeType(value.getClass()))
            {
                binder.send(out, value);

                return;
            }
            else
            {
                logger.warn("Can't serialize type " + value.getClass().getName() + ", trying toString()");
            }
        }
        catch (Exception e)
        {
            logger.error("Can't serialize type " + value.getClass().getName() + ", trying toString()", e);

        }
        out.write('\"');

        escape(value.toString(), out);

        out.write('\"');
    }

    public static final String format(final Date date, final IJSONContext context)
    {
        if (null == date)
        {
            return NULL_FOR_OUTPUT;
        }
        if (null != context)
        {
            final JSONDateFormatter formatter = context.getDateFormatter();

            if (null != formatter)
            {
                return formatter.format(date);
            }
        }
        return date.toString();
    }

    static final void escape(final String string, final Writer out) throws IOException
    {
        final int leng = string.length();

        for (int i = 0; i < leng; i++)
        {
            final char c = string.charAt(i);

            if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || (c == ' ') || ((c >= '0') && (c <= '9')))
            {
                out.write(c);// ASCII will be most common, this improves write speed about 5%, FWIW.
            }
            else
            {
                switch (c)
                {
                    case '"':
                        out.write("\\\"");
                        break;
                    case '\\':
                        out.write("\\\\");
                        break;
                    case '\b':
                        out.write("\\b");
                        break;
                    case '\f':
                        out.write("\\f");
                        break;
                    case '\n':
                        out.write("\\n");
                        break;
                    case '\r':
                        out.write("\\r");
                        break;
                    case '\t':
                        out.write("\\t");
                        break;
                    default:
                        // Reference: http://www.unicode.org/versions/Unicode5.1.0/

                        if (((c >= '\u0000') && (c <= '\u001F')) || ((c >= '\u007F') && (c <= '\u009F')) || ((c >= '\u2000') && (c <= '\u20FF')))
                        {
                            final String unic = Integer.toHexString(c);

                            final int size = 4 - unic.length();

                            out.write("\\u");

                            for (int k = 0; k < size; k++)
                            {
                                out.write('0');
                            }
                            out.write(unic.toUpperCase());
                        }
                        else
                        {
                            out.write(c);
                        }
                        break;
                }
            }
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

            if ((value > Integer.MAX_VALUE) || (value < Integer.MIN_VALUE))
            {
                return false;
            }
            return true;
        }
        if (object instanceof BigInteger)
        {
            final BigInteger value = ((BigInteger) object);

            if ((value.compareTo(BIG_INTEGER_MAX) > 0) || (value.compareTo(BIG_INTEGER_MIN) < 0))
            {
                return false;
            }
            return true;
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
            if (isDoubleInfiniteOrNan(((Double) object)))
            {
                return false;
            }
            return true;
        }
        if (object instanceof Float)
        {
            if (isFloatInfiniteOrNan((Float) object))
            {
                return false;
            }
            return true;
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
        if (object instanceof Number)
        {
            return (isInteger(object) || isDouble(object));
        }
        return false;
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
        return (object instanceof String);
    }

    public static final boolean isBoolean(final Object object)
    {
        return (object instanceof Boolean);
    }

    public static final Integer asInteger(final Object object)
    {
        if (null == object)
        {
            return null;
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
                return null;
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
                return null;
            }
            return value.intValue();
        }
        if (object instanceof BigDecimal)
        {
            final BigDecimal value = ((BigDecimal) object);

            if ((value.compareTo(BIG_DEC_INT_MAX) > 0) || (value.compareTo(BIG_DEC_INT_MIN) < 0))
            {
                return null;
            }
            return value.intValue();
        }
        if (object instanceof Number)
        {
            final long lval = new Long(((Number) object).longValue());

            if ((lval > Integer.MAX_VALUE) || (lval < Integer.MIN_VALUE))
            {
                return null;
            }
            return ((int) lval);
        }
        return null;
    }

    public static final Long asLong(final Object object)
    {
        if (null == object)
        {
            return null;
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
                return null;
            }
            if ((dval.doubleValue() > Long.MAX_VALUE) || (dval.doubleValue() < Long.MIN_VALUE))
            {
                return null;
            }
            return dval.longValue();
        }
        if (object instanceof BigInteger)
        {
            final BigInteger value = ((BigInteger) object);

            if ((value.compareTo(BIG_INTLONG_MAX) > 0) || (value.compareTo(BIG_INTLONG_MIN) < 0))
            {
                return null;
            }
            return value.longValue();
        }
        if (object instanceof BigDecimal)
        {
            final BigDecimal value = ((BigDecimal) object);

            if ((value.compareTo(BIG_DEC_LONGMAX) > 0) || (value.compareTo(BIG_DEC_LONGMIN) < 0))
            {
                return null;
            }
            return value.longValue();
        }
        if (object instanceof Number)
        {
            final long lval = new Long(((Number) object).longValue());

            if ((lval > Integer.MAX_VALUE) || (lval < Integer.MIN_VALUE))
            {
                return null;
            }
            return lval;
        }
        return null;
    }

    public static final Double asDouble(final Object object)
    {
        if (null == object)
        {
            return null;
        }
        if (object instanceof Double)
        {
            final Double dval = ((Double) object);

            if (isDoubleInfiniteOrNan(dval))
            {
                return null;
            }
            return dval;
        }
        if (object instanceof Float)
        {
            final Double dval = new Double(((Float) object).doubleValue());

            if (isDoubleInfiniteOrNan(dval))
            {
                return null;
            }
            return dval;
        }
        if (object instanceof BigDecimal)
        {
            final BigDecimal value = ((BigDecimal) object);

            if ((value.compareTo(BIG_DECIMAL_MAX) > 0) || (value.compareTo(BIG_DECIMAL_MIN) < 0))
            {
                return null;
            }
            if (isDoubleInfiniteOrNan(value.doubleValue()))
            {
                return null;
            }
            return value.doubleValue();
        }
        if (object instanceof BigInteger)
        {
            final BigInteger value = ((BigInteger) object);

            if ((value.compareTo(BIG_INT_DEC_MAX) > 0) || (value.compareTo(BIG_INT_DEC_MIN) < 0))
            {
                return null;
            }
            return value.doubleValue();
        }
        if (object instanceof Number)
        {
            final Double dval = new Double(((Number) object).doubleValue());

            if (isDoubleInfiniteOrNan(dval))
            {
                return null;
            }
            return dval;
        }
        return null;
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
        return null;
    }

    public static final JSONArray asArray(final Object object)
    {
        if (null == object)
        {
            return null;
        }
        if (object instanceof JSONArray)
        {
            return ((JSONArray) object);
        }
        if (object instanceof List)
        {
            return new JSONArray((List<?>) object);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static final JSONObject asObject(final Object object)
    {
        if (null == object)
        {
            return null;
        }
        if (object instanceof JSONObject)
        {
            return ((JSONObject) object);
        }
        if (object instanceof Map)
        {
            return new JSONObject((Map<String, ?>) object);
        }
        return null;
    }
}
