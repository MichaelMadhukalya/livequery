package com.livequery.types;

public class JsonString extends JsonType<JsonString> implements javax.json.JsonString {
    
    String value;
    
    @Override
    public String getString() {
        return value;
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
    public JsonString cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null != this.value) {
            return this;
        }
        
        this.value = (String) value;
        return this;
    }
}
