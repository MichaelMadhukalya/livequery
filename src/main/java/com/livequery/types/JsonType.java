package com.livequery.types;

import javax.json.JsonValue;
import org.apache.commons.lang3.StringUtils;

public abstract class JsonType<T extends JsonType> implements JsonValue {
    T value;
    
    JsonType() {
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.NULL;
    }
    
    @Override
    public String toString() {
        return StringUtils.EMPTY;
    }
    
    public abstract T cast(Object value) throws UnCastableObjectToInstanceTypeException;
    
    public static class UnCastableObjectToInstanceTypeException extends Exception {
        public UnCastableObjectToInstanceTypeException(String message) {
            super(message);
        }
    }
}
