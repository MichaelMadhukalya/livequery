package com.livequery.types;

import javax.json.JsonValue;

public class JsonNull extends JsonType<JsonNull> implements JsonValue {
    Object nullable;
    
    private JsonNull() {
        this.nullable = null;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.NULL;
    }
    
    @Override
    public String toString() {
        return null;
    }
    
    public static final JsonNull newInstance() {
        return new JsonNull();
    }
    
    @Override
    public JsonNull cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null != value) {
            throw new UnCastableObjectToInstanceTypeException(String.format("Can't convert non null object to JsonNull type"));
        }
        
        return this;
    }
}
