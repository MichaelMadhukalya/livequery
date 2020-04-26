package com.livequery.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParser.Event;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonArray extends JsonType<JsonArray> implements javax.json.JsonArray {
    
    List<? super JsonValue> list = new ArrayList<>();
    static JParser parser;
    
    private JsonArray() {
    }
    
    public static final JsonArray newInstance() {
        return new JsonArray();
    }
    
    @Override
    public JsonObject getJsonObject(int i) {
        return null;
    }
    
    @Override
    public javax.json.JsonArray getJsonArray(int i) {
        return null;
    }
    
    @Override
    public JsonNumber getJsonNumber(int i) {
        return null;
    }
    
    @Override
    public JsonString getJsonString(int i) {
        return null;
    }
    
    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> aClass) {
        return null;
    }
    
    @Override
    public String getString(int i) {
        return null;
    }
    
    @Override
    public String getString(int i, String s) {
        return null;
    }
    
    @Override
    public int getInt(int i) {
        return 0;
    }
    
    @Override
    public int getInt(int i, int i1) {
        return 0;
    }
    
    @Override
    public boolean getBoolean(int i) {
        return false;
    }
    
    @Override
    public boolean getBoolean(int i, boolean b) {
        return false;
    }
    
    @Override
    public boolean isNull(int i) {
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
    public boolean contains(Object o) {
        return false;
    }
    
    @Override
    public Iterator<JsonValue> iterator() {
        return null;
    }
    
    @Override
    public Object[] toArray() {
        return new Object[0];
    }
    
    @Override
    public <T> T[] toArray(T[] ts) {
        return null;
    }
    
    @Override
    public boolean add(JsonValue jsonValue) {
        return false;
    }
    
    @Override
    public boolean remove(Object o) {
        return false;
    }
    
    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }
    
    @Override
    public boolean addAll(Collection<? extends JsonValue> collection) {
        return false;
    }
    
    @Override
    public boolean addAll(int i, Collection<? extends JsonValue> collection) {
        return false;
    }
    
    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }
    
    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }
    
    @Override
    public void clear() {
    
    }
    
    @Override
    public JsonValue get(int i) {
        return null;
    }
    
    @Override
    public JsonValue set(int i, JsonValue jsonValue) {
        return null;
    }
    
    @Override
    public void add(int i, JsonValue jsonValue) {
    
    }
    
    @Override
    public JsonValue remove(int i) {
        return null;
    }
    
    @Override
    public int indexOf(Object o) {
        return 0;
    }
    
    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }
    
    @Override
    public ListIterator<JsonValue> listIterator() {
        return null;
    }
    
    @Override
    public ListIterator<JsonValue> listIterator(int i) {
        return null;
    }
    
    @Override
    public List<JsonValue> subList(int i, int i1) {
        return null;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }
    
    @Override
    public JsonArray cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null == value) {
            throw new IllegalArgumentException(String.format("Can't construct valid JsonArray from null object"));
        }
        
        if (CollectionUtils.isNotEmpty(list)) {
            return this;
        }
        
        try {
            if (null == parser) {
                JParser parser = new com.livequery.types.JParser((String) value) {
                };
            }
            
            Event event = parser.next();
            if (!event.equals(Event.START_ARRAY)) {
                throw new IllegalArgumentException(
                    String.format("JsonArray should always begin with START_ARRAY but found {%s} instead", event.toString()));
            }
            
            String key = null;
            JsonType<?> val = null;
            
            boolean end = false;
            while (parser.hasNext() && !end) {
                event = parser.next();
                
                switch (event) {
                    case START_OBJECT:
                        val = com.livequery.types.JsonObject.newInstance().cast(value);
                        list.add(val);
                        break;
                    case END_OBJECT:
                        break;
                    case START_ARRAY:
                        parser.pushBack(Event.START_ARRAY);
                        val = com.livequery.types.JsonArray.newInstance().cast(value);
                        list.add(val);
                        break;
                    case END_ARRAY:
                        end = true;
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
                        list.add(val);
                        break;
                    case VALUE_NUMBER:
                        val = com.livequery.types.JsonNumber.newInstance().cast(parser.getString());
                        list.add(val);
                        break;
                    case VALUE_TRUE:
                        val = com.livequery.types.JsonBoolean.newInstance().cast((Object) String.valueOf(Boolean.TRUE));
                        list.add(val);
                        break;
                    case VALUE_FALSE:
                        val = com.livequery.types.JsonBoolean.newInstance().cast((Object) String.valueOf(Boolean.FALSE));
                        list.add(val);
                        break;
                    case VALUE_NULL:
                        val = com.livequery.types.JsonNull.newInstance().cast(null);
                        list.add(val);
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
                String.format("Exception creating JsonArray from input string {%s}: {%s}", value, e));
        }
        
        return this;
    }
}
