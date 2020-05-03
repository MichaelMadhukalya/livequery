package com.livequery.types;

import com.livequery.types.JsonType.UnCastableObjectToInstanceTypeException;
import javax.json.JsonValue.ValueType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonBooleanTest {
    JsonBoolean jsonBoolean;
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void castValidStringAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("true");
        Assert.assertTrue(jsonBoolean.booleanValue == Boolean.TRUE);
        
        jsonBoolean.cast("False");
        Assert.assertTrue(jsonBoolean.booleanValue == Boolean.FALSE);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void castInValidStringAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("none");
    }
    
    @Test
    public void castValidStringAndGetIntegerValue_Test() throws UnCastableObjectToInstanceTypeException {
        jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("TRUE");
        Assert.assertTrue(jsonBoolean.getInt() == 1);
        
        jsonBoolean.cast("false");
        Assert.assertTrue(jsonBoolean.getInt() == 0);
    }
    
    @Test
    public void castValidStringAndCheckValueType_Test() throws UnCastableObjectToInstanceTypeException {
        jsonBoolean = JsonBoolean.newInstance();
        jsonBoolean.cast("TRUE");
        Assert.assertTrue(jsonBoolean.getValueType() == ValueType.TRUE);
        
        jsonBoolean.cast("false");
        Assert.assertTrue(jsonBoolean.getValueType() == ValueType.FALSE);
    }
    
}
