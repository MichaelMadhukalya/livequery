package com.livequery.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonNumber extends JsonType<JsonNumber> implements javax.json.JsonNumber {
    BigDecimal decimalValue;
    
    public JsonNumber(Object value) {
        super(new JsonNumber(), value);
    }
    
    private JsonNumber() {
    }
    
    @Override
    public boolean isIntegral() {
        return this.decimalValue.scale() > 0 ? false : true;
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
        return decimalValue != null ? decimalValue : null;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }
    
    @Override
    JsonNumber cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null != super.value) {
            return this;
        }
        
        try {
            decimalValue = new BigDecimal((String) value);
        } catch (NumberFormatException e) {
        }
        
        if (decimalValue != null) {
            return this;
        } else {
            throw new UnCastableObjectToInstanceTypeException(
                String.format("Can't find a valid decimal target type for source value = {%s}", value));
        }
    }
}
