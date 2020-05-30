package com.livequery.types;

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
        try {
            com.livequery.types.JsonArray array = com.livequery.types.JsonArray.newInstance();
            JsonType<?> valueType = (JsonType<?>) map.get(s);
            array.cast(valueType.toString());
            return array;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public javax.json.JsonObject getJsonObject(String s) {
        try {
            JsonObject object = JsonObject.newInstance();
            JsonType<?> valueType = (JsonType<?>) map.get(s);
            object.cast(valueType.toString());
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
        JsonType<?> valueType = (JsonType<?>) map.get(s);
        return valueType.valueOf().toString();
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
        
        return Integer.MIN_VALUE;
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
        JsonType<?> valueType = (JsonType<?>) map.get(s);
        if (null != valueType && valueType.toString().equals("null")) {
            return true;
        }
        
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
            map.remove(o);
        }
        
        return value;
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends JsonValue> map) {
        map.entrySet().stream().forEach(e -> this.map.put(e.getKey(), e.getValue()));
    }
    
    @Override
    public void clear() {
        map.clear();
    }
    
    @Override
    public Set<String> keySet() {
        Set<?> set = map.keySet();
        return (Set<String>) set;
    }
    
    @Override
    public Collection<JsonValue> values() {
        Collection<?> values = map.values();
        return (Collection<JsonValue>) values;
    }
    
    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        Set<?> set = map.entrySet();
        return (Set<Entry<String, JsonValue>>) set;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer().append("{");
        map.entrySet().stream().forEach(e -> {
            try {
                JsonType<?> keyType = com.livequery.types.JsonString.newInstance();
                keyType.cast(e.getKey());
                JsonType<?> valueType = (JsonType<?>) e.getValue();
                buffer.append(keyType.toString()).append(":").append(valueType.toString()).append(",");
            } catch (UnCastableObjectToInstanceTypeException ex) {
            }
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
            parser = JParser.getOrCreateNewInstance(value.toString());
            
            Event event = parser.next();
            if (!event.equals(Event.START_OBJECT)) {
                throw new IllegalArgumentException(
                    String.format("JsonObject should always begin with START_OBJECT found {%s} instead", event.toString()));
            }
            
            String key = null;
            JsonValue val = null;
            Object data = null;
            
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
                        parser.pushBack(Event.START_ARRAY);
                        val = com.livequery.types.JsonArray.newInstance().cast(value);
                        map.put(key, val);
                        /* Reset key and value for the next iteration */
                        key = null;
                        val = null;
                        break;
                    case END_ARRAY:
                        throw new UnCastableObjectToInstanceTypeException(
                            String.format("Error parsing input JSON unbalanced END_ARRAY object found"));
                    case KEY_NAME:
                        key = parser.getString();
                        if (StringUtils.isEmpty(key)) {
                            throw new UnCastableObjectToInstanceTypeException(
                                String.format("Key name can't be null or empty in JsonObject"));
                        }
                        break;
                    case VALUE_STRING:
                        data = parser.getString();
                        val = com.livequery.types.JsonString.newInstance().cast(data);
                        map.put(key, val);
                        /* Reset key and value for next iteration */
                        key = null;
                        val = null;
                        break;
                    case VALUE_NUMBER:
                        data = parser.getBigDecimal();
                        val = com.livequery.types.JsonNumber.newInstance().cast(data);
                        map.put(key, val);
                        /* Reset key and value for next iteration */
                        key = null;
                        val = null;
                        break;
                    case VALUE_TRUE:
                        val = com.livequery.types.JsonBoolean.newInstance().cast(JsonValue.TRUE);
                        map.put(key, val);
                        /* Reset key and value for next iteration */
                        key = null;
                        val = null;
                        break;
                    case VALUE_FALSE:
                        val = com.livequery.types.JsonBoolean.newInstance().cast(JsonValue.FALSE);
                        map.put(key, val);
                        /* Reset key and value for next iteration */
                        key = null;
                        val = null;
                        break;
                    case VALUE_NULL:
                        val = com.livequery.types.JsonNull.newInstance().cast(JsonValue.NULL);
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
            
            /* Close parser if no more tokens are left to parse */
            if (!parser.hasNext() && !parser.isClose()) {
                parser.close();
                parser = null;
            }
            
            super.value = this;
        } catch (Exception e) {
            throw new UnCastableObjectToInstanceTypeException(
                String.format("Exception creating JsonObject from input string {%s}: {%s}", value, e));
        }
        
        return this;
    }
}
