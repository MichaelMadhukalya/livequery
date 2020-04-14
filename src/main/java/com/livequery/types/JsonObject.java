package com.livequery.types;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

public class JsonObject extends JsonType<JsonObject> implements javax.json.JsonObject {
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
        return null;
    }
    
    @Override
    public JsonObject cast(Object value) throws UnCastableObjectToInstanceTypeException {
        return null;
    }
}
