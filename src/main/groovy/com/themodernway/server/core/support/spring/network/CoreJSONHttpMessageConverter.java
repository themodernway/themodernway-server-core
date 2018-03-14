
package com.themodernway.server.core.support.spring.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreJSONHttpMessageConverter extends AbstractHttpMessageConverter<JSONObject>
{
    private static final IBinder         BINDER = BinderType.JSON.getBinder();

    private static final List<Charset>   ACCEPT = Arrays.asList(IO.UTF_8_CHARSET);

    private static final List<MediaType> MEDIAT = Arrays.asList(MediaType.APPLICATION_JSON_UTF8);

    private static final Logger          logger = LoggingOps.getLogger(CoreJSONHttpMessageConverter.class);

    public CoreJSONHttpMessageConverter()
    {
        super(IO.UTF_8_CHARSET, MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_PLAIN, MediaType.ALL);
    }

    @Override
    protected boolean supports(final Class<?> claz)
    {
        return claz == JSONObject.class;
    }

    @Override
    protected JSONObject readInternal(final Class<? extends JSONObject> claz, final HttpInputMessage message) throws IOException, HttpMessageNotReadableException
    {
        try
        {
            return BINDER.bindJSON(new InputStreamReader(message.getBody(), IO.UTF_8_CHARSET));
        }
        catch (final ParserException e)
        {
            logger.error("bind().", e);

            throw new HttpMessageNotReadableException("bind", e);
        }
    }

    @Override
    protected void writeInternal(final JSONObject json, final HttpOutputMessage message) throws IOException, HttpMessageNotWritableException
    {
        final HttpHeaders head = message.getHeaders();

        head.setAccept(MEDIAT);

        head.setAcceptCharset(ACCEPT);

        head.setContentType(MediaType.APPLICATION_JSON_UTF8);

        try
        {
            BINDER.send(new OutputStreamWriter(message.getBody(), IO.UTF_8_CHARSET), json);
        }
        catch (final ParserException e)
        {
            logger.error("send().", e);

            throw new HttpMessageNotWritableException("send", e);
        }
    }
}
