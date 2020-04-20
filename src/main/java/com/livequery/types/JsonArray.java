package com.livequery.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class JsonArray extends JsonType<JsonArray> implements javax.json.JsonArray {
    
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
        return null;
    }
    
    @Override
    public JsonArray cast(Object value) throws UnCastableObjectToInstanceTypeException {
        return null;
    }
}
