package com.livequery.types;

import javax.json.JsonValue;
import org.apache.commons.lang3.StringUtils;

public class JsonBoolean extends JsonType<JsonBoolean> implements JsonValue {
    Boolean value;
    
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    
    private JsonBoolean() {
        this.value = null;
    }
    
    public static final JsonBoolean newInstance() {
        return new JsonBoolean();
    }
    
    public boolean getBoolean() {
        return value;
    }
    
    public int getInt() {
        if (value == Boolean.TRUE) {
            return 1;
        } else if (value == Boolean.FALSE) {
            return 0;
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    public String getString() {
        if (value == Boolean.TRUE) {
            return TRUE_VALUE;
        } else if (value == Boolean.FALSE) {
            return FALSE_VALUE;
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    public JsonNumber getJsonNumber() throws UnCastableObjectToInstanceTypeException {
        if (value == Boolean.TRUE) {
            return JsonNumber.newInstance().cast((Object) String.valueOf(1));
        } else if (value == Boolean.FALSE) {
            return JsonNumber.newInstance().cast((Object) String.valueOf(0));
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    @Override
    public ValueType getValueType() {
        if (value == Boolean.TRUE) {
            return ValueType.TRUE;
        } else if (value == Boolean.FALSE) {
            return ValueType.FALSE;
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public JsonBoolean cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null == value) {
            throw new IllegalArgumentException("Can't construct valid JsonBoolean from null object");
        }
        
        String val = String.valueOf(value);
        if (StringUtils.equalsIgnoreCase(val, TRUE_VALUE) || StringUtils.equalsIgnoreCase(val, "1")) {
            value = Boolean.TRUE;
        } else if (StringUtils.equalsIgnoreCase(val, FALSE_VALUE) || StringUtils.equalsIgnoreCase(val, "0")) {
            value = Boolean.FALSE;
        }
        
        if (null != value) {
            return this;
        }
        
        throw new UnCastableObjectToInstanceTypeException(
            String.format("Can't find a boolean target type for value = {%s}", value));
    }
}
