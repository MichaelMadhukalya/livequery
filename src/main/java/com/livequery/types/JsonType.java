package com.livequery.types;

import javax.json.JsonValue;

public abstract class JsonType<T extends JsonType> implements JsonValue {
    T value;
    
    JsonType() {
    }
    
    public abstract ValueType getValueType();
    
    public abstract String toString();
    
    public abstract T cast(Object value) throws UnCastableObjectToInstanceTypeException;
    
    public static class UnCastableObjectToInstanceTypeException extends Exception {
        public UnCastableObjectToInstanceTypeException(String message) {
            super(message);
        }
    }
}
