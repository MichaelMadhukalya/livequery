package com.livequery.common;

import javax.json.JsonValue;

public abstract class JsonType<T extends JsonType> implements JsonValue {
    T value;
    
    public JsonType() {
    }
    
    public JsonType(JsonType<T> instance, Object valueObject) {
        try {
            this.value = cast(valueObject);
        } catch (UnCastableObjectToInstanceTypeException e) {
            this.value = null;
        }
    }
    
    @Override
    public ValueType getValueType() {
        return null;
    }
    
    @Override
    public String toString() {
        return null;
    }
    
    abstract T cast(Object value) throws UnCastableObjectToInstanceTypeException;
    
    static class UnCastableObjectToInstanceTypeException extends Exception {
        public UnCastableObjectToInstanceTypeException(String message) {
            super(message);
        }
    }
}
