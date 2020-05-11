package com.livequery.types;

import com.livequery.types.JsonType.UnCastableObjectToInstanceTypeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonObjectTest {
    
    /* Input data */
    String INPUT_0 = "{}";
    
    String INPUT_1 = "{\"key\": \"value\"}";
    
    String INPUT_11 = "{\"key\": [1, 2, 3]}";
    
    String INPUT_2 = "{\"key1\": \"value1\", \"key2\": \"value2\"}";
    
    String INPUT_3 = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": [1, 2, 3], \"key4\": "
        + "{\"key5\": \"value5\", \"key6\": \"value6\"}}";
    
    String INPUT_4 = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": [1, [2, 3], 4], \"key4\": "
        + "{\"key5\": \"value5\", \"key6\": {\"key7\": \"value7\", \"key8\" : \"value8\"}}}";
    
    String INPUT_5 = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": [1, 2, 3], \"key4\": "
        + "{ \"key5\": \"value5\", \"key6\": \"value6\" }, \"key5\": [1, [2, [3, 4, 5]], {\"key6\": "
        + "{\"key7\": [1, 2, {\"key8\": \"value8\", \"key9\": [1, 2, [[1,2,3], 4]]}]}}]}";
    
    String INPUT_6 = "{\"key1\": \"value\", \"key2\": null}";
    
    String INPUT_7 = "{\"key1\": false, \"key2\": true}";
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
        JParser.cleanup();
    }
    
    @Test
    public void emptyStringAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_0);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 0);
    }
    
    @Test
    public void singleKeyAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_1);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 1);
    }
    
    @Test
    public void singleKeyWithArrayValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_11);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 1);
    }
    
    @Test
    public void multipleKeyAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_2);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 2);
    }
    
    @Test
    public void mixedValuesAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_3);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 4);
    }
    
    @Test
    public void nestedValuesAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_4);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 4);
    }
    
    @Test
    public void mixedNestedValuesAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_5);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 5);
    }
    
    @Test
    public void nullValuesAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_6);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 2);
        JsonNull jsonNull = JsonNull.newInstance().cast(jsonObject.get("key2"));
    }
}