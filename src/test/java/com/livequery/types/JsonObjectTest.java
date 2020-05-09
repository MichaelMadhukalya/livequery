package com.livequery.types;

import com.livequery.types.JsonType.UnCastableObjectToInstanceTypeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonObjectTest {
    
    JsonObject jsonObject;
    
    /* Input data */
    String INPUT_0 = "{}";
    
    String INPUT_1 = "{\"key\": \"value\"}";
    
    String INPUT_2 = "{\"key1\": \"value1\", \"key2\": \"value2\"}";
    
    String INPUT_3 = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": [1, 2, 3], \"key4\": "
        + "{\"key5\": \"value5\", \"key6\": \"value6\"}}";
    
    String INPUT_4 = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": [1, [2, 3], 4], \"key4\": "
        + "{\"key5\": \"value5\", \"key6\": {\"key7\": \"value7\", \"key8\" : \"value8\"}}}";
    
    String INPUT_5 = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": [1, 2, 3], \"key4\": "
        + "{ \"key5\": \"value5\", \"key6\": \"value6\" }, \"key5\": [1, [2, [3, 4, 5]], {\"key6\": "
        + "{\"key7\": [1, 2, {\"key8\": \"value8\", \"key9\": [1, 2, [[1,2,3], 4]]}]}}]}";
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void emptyStringAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_0);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 0);
    }
    
    @Test
    public void singleKeyAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_1);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 1);
    }
    
    @Test
    public void multipleKeyAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_2);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 2);
    }
}