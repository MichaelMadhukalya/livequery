package com.livequery.common;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import org.apache.log4j.Logger;

public final class JParser implements Closeable {
    /**
     * Logger
     */
    static final Logger LOG = Logger.getLogger(JParser.class.getSimpleName());
    
    /**
     * Static instance of JParser
     */
    private static JParser parser;
    static final Object LOCK = new Object();
    
    /**
     * Internal state of JParser object
     */
    private JsonParser jsonParser;
    private String source;
    
    private JParser() {
    }
    
    private JParser(String source) {
        jsonParser = Json.createParser(new StringReader(source));
    }
    
    public static final JParser newInstance(String source) {
        if (null == parser) {
            synchronized (LOCK) {
                if (null == parser) {
                    parser = new JParser(source);
                }
            }
        }
        
        return parser;
    }
    
    public static final JsonParser getJsonParserInstance() {
        if (null != parser) {
            return parser.jsonParser;
        }
        
        LOG.error(String.format("Call to access JParser instance before it is instantiated"));
        throw new IllegalStateException(String.format("Unable to get instance of JParser since it is not created"));
    }
    
    public boolean isObject() {
        if (jsonParser.hasNext()) {
            Event event = jsonParser.next();
            return event.equals(Event.START_OBJECT) ? true : false;
        }
        
        return false;
    }
    
    @Override
    public void close() throws IOException {
        if (null != parser) {
            synchronized (LOCK) {
                if (null != parser) {
                    jsonParser.close();
                    jsonParser = null;
                    parser = null;
                }
            }
        }
    }
}
