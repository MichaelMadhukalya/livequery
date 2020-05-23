package com.livequery.types;

import java.io.Closeable;
import java.io.OutputStream;
import java.nio.charset.Charset;

public interface IJsonWriter<T extends JsonType> extends Closeable {
    /**
     * Write a JsonType object to an output stream in serialized format using the default encoding scheme
     *
     * @param object Input JsonType object
     * @param out output stream
     */
    void write(JsonType<T> object, OutputStream out);
    
    /**
     * Write a JsonType object to an output stream in serialized format using the supplied encoding scheme
     *
     * @param object Input JsonType object
     * @param out output stream
     * @param charset Encoding scheme
     */
    void write(JsonType<T> object, OutputStream out, Charset charset);
    
    /**
     * Write a JsonType object to an output stream in compressed binary format. It is upto the implementation to select a
     * suitable compression technique for this purpose.
     *
     * @param object Input JsonType object
     * @param out output stream
     */
    void compress(JsonType<T> object, OutputStream out);
}
