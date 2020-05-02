package com.livequery.types;

import com.livequery.types.JsonType.UnCastableObjectToInstanceTypeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonNullTest {
    
    JsonNull jsonNull;
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void castNullObjectAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        jsonNull = JsonNull.newInstance();
        JsonNull target = jsonNull.cast(null);
        Assert.assertTrue(target.nullable == null);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void castNonNullObjectAsINputExpectedException_Test() throws UnCastableObjectToInstanceTypeException {
        jsonNull = JsonNull.newInstance();
        JsonNull target = jsonNull.cast("test");
    }
}
