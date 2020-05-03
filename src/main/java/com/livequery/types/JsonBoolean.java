package com.livequery.types;

import javax.json.JsonValue;
import org.apache.commons.lang3.StringUtils;

public class JsonBoolean extends JsonType<JsonBoolean> implements JsonValue {
    Boolean booleanValue;
    
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    
    private JsonBoolean() {
        this.booleanValue = null;
    }
    
    public static final JsonBoolean newInstance() {
        return new JsonBoolean();
    }
    
    public boolean getBoolean() {
        return booleanValue;
    }
    
    public int getInt() {
        if (booleanValue == Boolean.TRUE) {
            return 1;
        } else if (booleanValue == Boolean.FALSE) {
            return 0;
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    public String getString() {
        if (booleanValue == Boolean.TRUE) {
            return TRUE_VALUE;
        } else if (booleanValue == Boolean.FALSE) {
            return FALSE_VALUE;
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    public JsonNumber getJsonNumber() throws UnCastableObjectToInstanceTypeException {
        if (booleanValue == Boolean.TRUE) {
            return JsonNumber.newInstance().cast((Object) String.valueOf(1));
        } else if (booleanValue == Boolean.FALSE) {
            return JsonNumber.newInstance().cast((Object) String.valueOf(0));
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    @Override
    public ValueType getValueType() {
        if (booleanValue == Boolean.TRUE) {
            return ValueType.TRUE;
        } else if (booleanValue == Boolean.FALSE) {
            return ValueType.FALSE;
        }
        
        throw new IllegalStateException(String.format("Uninitialized JsonBoolean type not associated with boolean value"));
    }
    
    @Override
    public String toString() {
        return booleanValue.toString();
    }
    
    @Override
    public JsonBoolean cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null == value) {
            throw new IllegalArgumentException("Can't construct valid JsonBoolean from null object");
        }
        
        String val = (String) value;
        if (StringUtils.equalsIgnoreCase(val, TRUE_VALUE) || StringUtils.equalsIgnoreCase(val, "1")) {
            booleanValue = Boolean.TRUE;
        } else if (StringUtils.equalsIgnoreCase(val, FALSE_VALUE) || StringUtils.equalsIgnoreCase(val, "0")) {
            booleanValue = Boolean.FALSE;
        }
        
        if (null != booleanValue) {
            return this;
        }
        
        throw new UnCastableObjectToInstanceTypeException(
            String.format("Can't find a boolean target type for value = {%s}", value));
    }
}
