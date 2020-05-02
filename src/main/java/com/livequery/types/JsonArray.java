package com.livequery.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
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
        try {
            com.livequery.types.JsonObject object = com.livequery.types.JsonObject.newInstance();
            object.cast(list.get(i));
            return object;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public javax.json.JsonArray getJsonArray(int i) {
        try {
            JsonArray array = JsonArray.newInstance();
            array.cast(list.get(i));
            return array;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public JsonNumber getJsonNumber(int i) {
        try {
            com.livequery.types.JsonNumber number = com.livequery.types.JsonNumber.newInstance();
            number.cast(list.get(i));
            return number;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public JsonString getJsonString(int i) {
        try {
            com.livequery.types.JsonString string = com.livequery.types.JsonString.newInstance();
            string.cast(list.get(i));
            return string;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return null;
    }
    
    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> aClass) {
        List<T> newlist = new ArrayList<>();
        newlist = list.stream().map(e -> {
            T val = null;
            try {
                JsonType<?> jsonType = (JsonType<?>) e;
                val = (T) jsonType.cast(((JsonType<?>) e).toString());
            } catch (UnCastableObjectToInstanceTypeException ex) {
            }
            
            return val;
        }).collect(Collectors.toList());
        
        return newlist;
    }
    
    @Override
    public String getString(int i) {
        return (String) list.get(i);
    }
    
    @Override
    @Deprecated
    public String getString(int i, String s) {
        return null;
    }
    
    @Override
    public int getInt(int i) {
        try {
            com.livequery.types.JsonNumber number = com.livequery.types.JsonNumber.newInstance();
            number.cast(list.get(i));
            return number.intValue();
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return 0;
    }
    
    @Override
    @Deprecated
    public int getInt(int i, int i1) {
        return 0;
    }
    
    @Override
    public boolean getBoolean(int i) {
        try {
            com.livequery.types.JsonBoolean jsonBoolean = com.livequery.types.JsonBoolean.newInstance();
            jsonBoolean.cast(list.get(i));
            return jsonBoolean.value;
        } catch (UnCastableObjectToInstanceTypeException e) {
        }
        
        return false;
    }
    
    @Override
    @Deprecated
    public boolean getBoolean(int i, boolean b) {
        return false;
    }
    
    @Override
    public boolean isNull(int i) {
        JsonValue value = (JsonValue) list.get(i);
        return value == null ? true : false;
    }
    
    @Override
    public int size() {
        return list.size();
    }
    
    @Override
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(list);
    }
    
    @Override
    public boolean contains(Object o) {
        return list.stream().filter(e -> e.equals(o)).count() >= 1 ? true : false;
    }
    
    @Override
    public Iterator<JsonValue> iterator() {
        return (Iterator<JsonValue>) list.iterator();
    }
    
    @Override
    public Object[] toArray() {
        return list.toArray();
    }
    
    @Override
    public <T> T[] toArray(T[] ts) {
        return list.toArray(ts);
    }
    
    @Override
    public boolean add(JsonValue jsonValue) {
        return list.add(jsonValue);
    }
    
    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }
    
    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.containsAll(collection);
    }
    
    @Override
    public boolean addAll(Collection<? extends JsonValue> collection) {
        return list.addAll(collection);
    }
    
    @Override
    public boolean addAll(int i, Collection<? extends JsonValue> collection) {
        return list.addAll(i, collection);
    }
    
    @Override
    public boolean removeAll(Collection<?> collection) {
        return list.removeAll(collection);
    }
    
    @Override
    public boolean retainAll(Collection<?> collection) {
        return list.retainAll(collection);
    }
    
    @Override
    public void clear() {
        list.clear();
    }
    
    @Override
    public JsonValue get(int i) {
        return (JsonValue) list.get(i);
    }
    
    @Override
    public JsonValue set(int i, JsonValue jsonValue) {
        return (JsonValue) list.set(i, jsonValue);
    }
    
    @Override
    public void add(int i, JsonValue jsonValue) {
        list.add(i, jsonValue);
    }
    
    @Override
    public JsonValue remove(int i) {
        return (JsonValue) list.remove(i);
    }
    
    @Override
    public int indexOf(Object o) {
        int index = -1;
        
        for (Object e : list) {
            ++index;
            if (e.equals(o)) {
                return index;
            }
        }
        
        return -1;
    }
    
    @Override
    public int lastIndexOf(Object o) {
        int lastIndex = -1, index = 0;
        
        for (Object e : list) {
            if (e.equals(o)) {
                lastIndex = index;
            }
            index++;
        }
        
        return lastIndex;
    }
    
    @Override
    public ListIterator<JsonValue> listIterator() {
        return (ListIterator<JsonValue>) list.listIterator();
    }
    
    @Override
    public ListIterator<JsonValue> listIterator(int i) {
        return listIterator(i);
    }
    
    @Override
    public List<JsonValue> subList(int i, int i1) {
        return subList(i, i1);
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer().append("[");
        list.stream().forEach(e -> buffer.append(((Object) e).toString()).append(','));
        if (buffer.length() > 1 && buffer.charAt(buffer.length() - 1) == ',') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append("]");
        return buffer.toString();
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
