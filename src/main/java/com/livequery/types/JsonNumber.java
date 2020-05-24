package com.livequery.types;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonNumber extends JsonType<JsonNumber> implements javax.json.JsonNumber {
    BigDecimal decimalValue;
    
    private JsonNumber() {
    }
    
    public static final JsonNumber newInstance() {
        return new JsonNumber();
    }
    
    @Override
    public boolean isIntegral() {
        return decimalValue.scale() <= 0;
    }
    
    @Override
    public int intValue() {
        return decimalValue.intValue();
    }
    
    @Override
    public int intValueExact() {
        return decimalValue.intValueExact();
    }
    
    @Override
    public long longValue() {
        return decimalValue.longValue();
    }
    
    @Override
    public long longValueExact() {
        return decimalValue.longValueExact();
    }
    
    @Override
    public BigInteger bigIntegerValue() {
        return decimalValue.toBigInteger();
    }
    
    @Override
    public BigInteger bigIntegerValueExact() {
        return decimalValue.toBigIntegerExact();
    }
    
    @Override
    public double doubleValue() {
        return decimalValue.doubleValue();
    }
    
    @Override
    public BigDecimal bigDecimalValue() {
        return decimalValue;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }
    
    @Override
    public String toString() {
        return BigDecimal.valueOf(doubleValue()).toEngineeringString();
    }
    
    @Override
    public JsonNumber cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null == value) {
            throw new IllegalArgumentException("Can't construct valid JsonNumber from null object");
        }
        
        try {
            decimalValue = new BigDecimal(value.toString());
            super.valueType = this;
        } catch (NumberFormatException e) {
        }
        
        if (null != decimalValue) {
            return this;
        }
        
        throw new UnCastableObjectToInstanceTypeException(String.format("Can't find a decimal type for value = {%s}", value));
    }
    
}
