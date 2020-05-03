package com.livequery.types;

public class JsonString extends JsonType<JsonString> implements javax.json.JsonString {
    String string;
    
    private JsonString() {
    }
    
    public static final JsonString newInstance() {
        return new JsonString();
    }
    
    @Override
    public String getString() {
        return string;
    }
    
    @Override
    public CharSequence getChars() {
        return null;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }
    
    @Override
    public String toString() {
        return string;
    }
    
    @Override
    public JsonString cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null == value) {
            throw new IllegalArgumentException("Can't construct valid JsonString from null object");
        }
        
        if (null != string) {
            return this;
        }
        
        string = (String) value;
        return this;
    }
}
