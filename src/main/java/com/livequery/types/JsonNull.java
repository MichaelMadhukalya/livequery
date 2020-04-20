package com.livequery.types;

public class JsonNull extends JsonType<JsonNull> {
    Object nullable;
    
    private static final String NULL_VALUE = "null";
    
    private JsonNull() {
        this.nullable = null;
    }
    
    public static final JsonNull newInstance() {
        return new JsonNull();
    }
    
    public String getString() {
        return NULL_VALUE;
    }
    
    @Override
    public JsonNull cast(Object value) throws UnCastableObjectToInstanceTypeException {
        /* Strangest code follows where we operate in the function only when the input is null */
        if (null != value) {
            throw new UnCastableObjectToInstanceTypeException(String.format("Can't convert non null object to JsonNull type"));
        }
        
        return this;
    }
}
