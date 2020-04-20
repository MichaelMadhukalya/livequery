package com.livequery.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;

public class JsonNumber extends JsonType<JsonNumber> implements javax.json.JsonNumber {
    BigDecimal decimalValue;
    
    private JsonNumber() {
    }
    
    public static final JsonNumber newInstance() {
        return new JsonNumber();
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
    public String toString() {
        return decimalValue == null ? StringUtils.EMPTY : bigDecimalValue().toString();
    }
    
    @Override
    public JsonNumber cast(Object value) throws UnCastableObjectToInstanceTypeException {
        if (null == value) {
            throw new IllegalArgumentException("Can't construct valid JsonNumber from null object");
        }
        
        if (null != super.value) {
            return this;
        }
        
        try {
            decimalValue = new BigDecimal((String) value);
        } catch (NumberFormatException e) {
        }
        
        if (decimalValue != null) {
            super.value = this;
            return this;
        } else {
            throw new UnCastableObjectToInstanceTypeException(
                String.format("Can't find a valid decimal target type for source value = {%s}", value));
        }
    }
    
}
