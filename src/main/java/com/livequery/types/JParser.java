package com.livequery.types;

import com.google.common.annotations.VisibleForTesting;
import java.io.Closeable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.json.Json;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import org.apache.log4j.Logger;

class JParser implements JsonParser, Closeable {
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    
    /**
     * Mutable state of the parser
     */
    private JsonParser jsonParser;
    private String input;
    private final Stack<Event> events = new Stack<>();
    
    /**
     * Cache of JParser instance by thread
     */
    static final Map<String, JParser> cache = new HashMap<>();
    
    private JParser() {
        this(null);
    }
    
    private JParser(String input) {
        this.input = input;
        this.jsonParser = Json.createParser(new StringReader(this.input));
    }
    
    public static JParser getOrCreateNewInstance(String input) {
        String name = Thread.currentThread().getName();
        if (cache.containsKey(name) && cache.get(name) != null) {
            return cache.get(name);
        }
        
        cache.put(name, new JParser(input));
        return cache.get(name);
    }
    
    public boolean hasNext() {
        if (isClose()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }
        
        return jsonParser.hasNext();
    }
    
    public Event next() {
        if (checkAccess()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }
        
        if (events.size() == 0) {
            return jsonParser.next();
        }
        
        return events.pop();
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
        if (checkAccess()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }
        
        events.push(event);
    }
    
    @Override
    public void close() {
        if (checkAccess()) {
            String name = Thread.currentThread().getName();
            logger.warn(String.format("Thread {%s} does not have parser registered or not authorized for this instance", name));
            throw new IllegalStateException(String.format(
                "Thread {%s} does not have parser registered or not authorized for this instance", name));
        }
        
        if (null != jsonParser) {
            jsonParser.close();
            jsonParser = null;
            input = null;
            String name = Thread.currentThread().getName();
            cache.remove(name);
        }
        
        logger.debug(String.format("Removed JParser instance for thread: {%s}", Thread.currentThread().getName()));
    }
    
    public boolean checkAccess() {
        return isClose();
    }
    
    public boolean isClose() {
        return !cache.containsKey(Thread.currentThread().getName()) || null == cache.get(Thread.currentThread().getName());
    }
    
    @VisibleForTesting
    public static void cleanup() {
        cache.clear();
    }
}
