package com.livequery.types;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParser.Event;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonObject extends JsonType<JsonObject> implements javax.json.JsonObject {
    
    Map<? super String, ? super JsonValue> map = new LinkedHashMap<>();
    static JParser parser;
    
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
        JsonObject object = new JsonObject();
        try {
            object.cast(map.get(s));
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return object;
    }
    
    @Override
    public JsonNumber getJsonNumber(String s) {
        com.livequery.types.JsonNumber number = com.livequery.types.JsonNumber.newInstance();
        try {
            number.cast(s);
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return number;
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
    @Deprecated
    public String getString(String s, String s1) {
        return s1;
    }
    
    @Override
    public int getInt(String s) {
        return 0;
    }
    
    @Override
    @Deprecated
    public int getInt(String s, int i) {
        return i;
    }
    
    @Override
    public boolean getBoolean(String s) {
        return false;
    }
    
    @Override
    @Deprecated
    public boolean getBoolean(String s, boolean b) {
        return b;
    }
    
    @Override
    public boolean isNull(String s) {
        return false;
    }
    
    @Override
    public int size() {
        return MapUtils.isEmpty(map) ? 0 : map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return MapUtils.isEmpty(map);
    }
    
    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }
    
    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(o);
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
        if (MapUtils.isNotEmpty(this.map)) {
            this.map.clear();
        }
        
        map.entrySet().stream().forEach(e -> this.map.put(e.getKey(), e.getValue()));
    }
    
    @Override
    public void clear() {
        map.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return new ImmutableSet.Builder().addAll(map.keySet()).build();
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
        
        try {
            if (null == parser) {
                parser = new JParser((String) value) {
                };
            }
            
            Event event = parser.next();
            if (!event.equals(Event.START_OBJECT)) {
                throw new IllegalArgumentException(
                    String.format("JsonObject should always begin with START_OBJECT found {%s} instead", event.toString()));
            }
            
            String key = null;
            JsonType<?> val = null;
            
            boolean end = false;
            while (parser.hasNext() && !end) {
                event = parser.next();
                
                switch (event) {
                    case START_OBJECT:
                        parser.pushBack(Event.START_OBJECT);
                        val = JsonObject.newInstance().cast(value);
                        map.put(key, val);
                        /* Reset key and value for the next iteration */
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
            
            /* Close parser if no more tokens left to parse */
            if (!parser.hasNext()) {
                parser.close();
                parser = null;
            }
        } catch (Exception e) {
            throw new UnCastableObjectToInstanceTypeException(
                String.format("Exception creating JsonObject from input string {%s}: {%s}", value, e));
        }
        
        return this;
    }
}
