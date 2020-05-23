package com.livequery.types;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.log4j.Logger;

public class JsonWriter implements IJsonWriter<JsonType> {
    /**
     * Logger
     */
    final Logger LOG = Logger.getLogger(JsonWriter.class.getSimpleName());
    
    @Override
    public void write(JsonType<JsonType> object, OutputStream out) {
        write(object, out, Charset.defaultCharset());
    }
    
    @Override
    public void write(JsonType<JsonType> object, OutputStream out, Charset charset) {
        String value = null;
        try {
            value = object.toString();
            out.write(value.getBytes(charset));
        } catch (IOException e) {
            LOG.error(String.format("Exception writing input: {%s} to output stream", value));
        }
    }
    
    @Override
    public void compress(JsonType<JsonType> object, OutputStream out) {
        throw new IllegalArgumentException();
    }
    
    @Override
    public void close() throws IOException {
    }
}
