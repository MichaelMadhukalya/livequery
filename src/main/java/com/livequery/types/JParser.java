package com.livequery.types;

import java.io.Closeable;
import java.io.StringReader;
import java.math.BigDecimal;
import javax.json.Json;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import org.apache.log4j.Logger;

class JParser implements JsonParser, Closeable {
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    
    final JsonParser jsonParser;
    final String input;
    
    private Event lastEvent;
    
    public JParser() {
        this(null);
    }
    
    public JParser(String input) {
        this.input = input;
        this.jsonParser = Json.createParser(new StringReader(this.input));
    }
    
    public boolean hasNext() {
        return jsonParser.hasNext();
    }
    
    public Event next() {
        if (null != lastEvent) {
            Event event = lastEvent;
            lastEvent = null;
            return event;
        }
        
        return jsonParser.next();
    }
    
    @Override
    public String getString() {
        return jsonParser.getString();
    }
    
    @Override
    public boolean isIntegralNumber() {
        return jsonParser.isIntegralNumber();
    }
    
    @Override
    public int getInt() {
        return jsonParser.getInt();
    }
    
    @Override
    public long getLong() {
        return jsonParser.getLong();
    }
    
    @Override
    public BigDecimal getBigDecimal() {
        return jsonParser.getBigDecimal();
    }
    
    @Override
    public JsonLocation getLocation() {
        return jsonParser.getLocation();
    }
    
    public void pushBack(Event event) {
        lastEvent = event;
    }
    
    @Override
    public void close() {
        if (null != jsonParser) {
            jsonParser.close();
        }
    }
}
