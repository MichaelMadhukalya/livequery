package com.livequery.types;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
        try {
            com.livequery.types.JsonArray array = com.livequery.types.JsonArray.newInstance();
            array.cast(map.get(s));
            return array;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public javax.json.JsonObject getJsonObject(String s) {
        try {
            JsonObject object = new JsonObject();
            object.cast(map.get(s));
            return object;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public JsonNumber getJsonNumber(String s) {
        try {
            com.livequery.types.JsonNumber number = com.livequery.types.JsonNumber.newInstance();
            number.cast(map.get(s));
            return number;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public JsonString getJsonString(String s) {
        try {
            com.livequery.types.JsonString string = com.livequery.types.JsonString.newInstance();
            string.cast(map.get(s));
            return string;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public String getString(String s) {
        return (String) map.get(s);
    }
    
    @Override
    @Deprecated
    public String getString(String s, String s1) {
        return s1;
    }
    
    @Override
    public int getInt(String s) {
        try {
            com.livequery.types.JsonNumber number = com.livequery.types.JsonNumber.newInstance();
            number.cast(map.get(s));
            return number.intValue();
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return 0;
    }
    
    @Override
    @Deprecated
    public int getInt(String s, int i) {
        return i;
    }
    
    @Override
    public boolean getBoolean(String s) {
        try {
            com.livequery.types.JsonBoolean jsonBoolean = JsonBoolean.newInstance();
            jsonBoolean.cast(map.get(s));
            return jsonBoolean.booleanValue;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return Boolean.FALSE;
    }
    
    @Override
    @Deprecated
    public boolean getBoolean(String s, boolean b) {
        return b;
    }
    
    @Override
    public boolean isNull(String s) {
        return map.get(s) == null ? true : false;
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
        return (JsonValue) map.get(o);
    }
    
    @Override
    public JsonValue put(String s, JsonValue jsonValue) {
        map.put(s, jsonValue);
        return (JsonValue) map.get(s);
    }
    
    @Override
    public JsonValue remove(Object o) {
        JsonValue value = null;
        if (map.containsKey(o)) {
            value = (JsonValue) map.get(o);
        }
        
        return value;
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
        return new ImmutableSet.Builder<String>().addAll((Iterable<? extends String>) map.keySet()).build();
    }
    
    @Override
    public Collection<JsonValue> values() {
        return Collections.<JsonValue>unmodifiableCollection(
            map.values().stream().map(e -> (JsonValue) e).collect(Collectors.toList()));
    }
    
    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        Set<Entry<String, JsonValue>> set = map.entrySet().stream()
            .map(e -> Maps.immutableEntry((String) e.getKey(), (JsonValue) e.getValue()))
            .collect(Collectors.toSet());
        
        return (Set<Entry<String, JsonValue>>) new ImmutableSet.Builder<Entry<String, JsonValue>>()
            .addAll((Iterable<? extends Entry<String, JsonValue>>) set);
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer().append("{");
        map.entrySet().stream().forEach(e -> {
            buffer.append("\"").append(e.getKey()).append("\"").append(":").append(e.getValue()).append(",");
        });
        if (buffer.length() > 1 && buffer.charAt(buffer.length() - 1) == ',') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append("}");
        return buffer.toString();
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
            JsonValue val = null;
            
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
