package com.livequery.types;

import com.livequery.common.JParser;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonObject extends JsonType<JsonObject> implements javax.json.JsonObject {
    
    Map<? super String, ? super JsonType<?>> map = new LinkedHashMap<>();
    
    private JsonObject() {
    }
    
    public static final JsonObject newInstance() {
        return new JsonObject();
    }
    
    @Override
    public JsonArray getJsonArray(String s) {
        return null;
    }
    
    @Override
    public javax.json.JsonObject getJsonObject(String s) {
        return null;
    }
    
    @Override
    public JsonNumber getJsonNumber(String s) {
        return null;
    }
    
    @Override
    public JsonString getJsonString(String s) {
        return null;
    }
    
    @Override
    public String getString(String s) {
        return null;
    }
    
    @Override
    public String getString(String s, String s1) {
        return null;
    }
    
    @Override
    public int getInt(String s) {
        return 0;
    }
    
    @Override
    public int getInt(String s, int i) {
        return 0;
    }
    
    @Override
    public boolean getBoolean(String s) {
        return false;
    }
    
    @Override
    public boolean getBoolean(String s, boolean b) {
        return false;
    }
    
    @Override
    public boolean isNull(String s) {
        return false;
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public boolean containsKey(Object o) {
        return false;
    }
    
    @Override
    public boolean containsValue(Object o) {
        return false;
    }
    
    @Override
    public JsonValue get(Object o) {
        return null;
    }
    
    @Override
    public JsonValue put(String s, JsonValue jsonValue) {
        return null;
    }
    
    @Override
    public JsonValue remove(Object o) {
        return null;
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends JsonValue> map) {
    
    }
    
    @Override
    public void clear() {
    
    }
    
    @Override
    public Set<String> keySet() {
        return null;
    }
    
    @Override
    public Collection<JsonValue> values() {
        return null;
    }
    
    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return null;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }
    
    @Override
    public JsonObject cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null == value) {
            throw new IllegalArgumentException("Can't construct valid JsonObject from null object");
        }
        
        if (MapUtils.isNotEmpty(map)) {
            return this;
        }
        
        JsonParser parser = JParser.getJsonParserInstance();
        
        String key = null;
        JsonType<?> val = null;
        
        boolean end = false;
        while (parser.hasNext() && !end) {
            Event event = parser.next();
            
            switch (event) {
                case START_OBJECT:
                    val = JsonObject.newInstance().cast(value);
                    map.put(key, val);
                    // Reset key and value for the next iteration
                    key = null;
                    val = null;
                    break;
                case END_OBJECT:
                    end = true;
                    break;
                case START_ARRAY:
                    break;
                case END_ARRAY:
                    break;
                case KEY_NAME:
                    key = parser.getString();
                    if (StringUtils.isEmpty(key)) {
                        throw new UnCastableObjectToInstanceTypeException(
                            String.format("Key name can't be null or empty in JsonObject"));
                    }
                    break;
                case VALUE_STRING:
                    String data = parser.getString();
                    val = com.livequery.types.JsonString.newInstance().cast(data);
                    map.put(key, val);
                    /* Reset key and value for next iteration */
                    key = null;
                    val = null;
                    break;
                case VALUE_NUMBER:
                    val = com.livequery.types.JsonNumber.newInstance().cast(parser.getString());
                    map.put(key, val);
                    /* Reset key and value for next iteration */
                    key = null;
                    val = null;
                    break;
                case VALUE_TRUE:
                    val = com.livequery.types.JsonBoolean.newInstance().cast((Object) String.valueOf(Boolean.TRUE));
                    map.put(key, val);
                    /* Reset key and value for next iteration */
                    key = null;
                    val = null;
                    break;
                case VALUE_FALSE:
                    val = com.livequery.types.JsonBoolean.newInstance().cast((Object) String.valueOf(Boolean.FALSE));
                    map.put(key, val);
                    /* Reset key and value for next iteration */
                    key = null;
                    val = null;
                    break;
                case VALUE_NULL:
                    val = com.livequery.types.JsonNull.newInstance().cast(null);
                    map.put(key, val);
                    /* Reset key and value for next iteration */
                    key = null;
                    val = null;
                    break;
                default:
                    throw new UnCastableObjectToInstanceTypeException(
                        String.format("Unknown event type encountered parsing input for JsonObject"));
            }
        }
        
        return this;
    }
}
