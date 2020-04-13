package com.livequery.common;

import javax.json.JsonValue;
import org.apache.commons.lang3.StringUtils;

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
        return ValueType.NULL;
    }
    
    @Override
    public String toString() {
        return StringUtils.EMPTY;
    }
    
    abstract T cast(Object value) throws UnCastableObjectToInstanceTypeException;
    
    static class UnCastableObjectToInstanceTypeException extends Exception {
        public UnCastableObjectToInstanceTypeException(String message) {
            super(message);
        }
    }
}
