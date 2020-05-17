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
        + "{ \"key5\": \"value5\", \"key6\": \"value6\"}, \"key7\": [1, [2, [3, 4, 5]], {\"key8\": "
        + "{\"key9\": [1, 2, {\"key10\": \"value10\", \"key11\": [1, 2, [[1,2,3], 4]]}]}}]}";
    
    String INPUT_6 = "{\"key1\": \"value\", \"key2\": null}";
    
    String INPUT_7 = "{\"key1\": false, \"key2\": true}";
    
    String INPUT_8 = "{\"key1\": 3.14159, \"key2\": 2.3E-4}";
    
    String INPUT_9 = "{{}";
    
    String INPUT_10 = "{2: 2.15E05}";
    
    String INPUT_12 = "{\"key1\": [1, 2]}, \"key2\"}";
    
    String INPUT_13 = "{\"key1\": [1, 2, [3]}]";
    
    String INPUT_14 = "{\"key1\": [1, 2], \"key2\": \"key3\" }, [1, 6]}";
    
    String INPUT_15 = ".{}";
    
    String INPUT_16 = "{\"key\": 2.15E+05}";
    
    String INPUT_17 = "{\"\": 2.15E+05}";
    
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
    
    @Test
    public void booleanValuesAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_7);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 2);
        JsonBoolean jsonBoolean = JsonBoolean.newInstance().cast(jsonObject.get("key1"));
        Assert.assertTrue(jsonBoolean.getBoolean() == Boolean.FALSE);
    }
    
    @Test
    public void numberValuesAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_8);
        Assert.assertTrue(null != jsonObject.map && jsonObject.size() == 2);
        
        JsonNumber jsonNumber1 = JsonNumber.newInstance().cast(jsonObject.get("key1"));
        JsonNumber jsonNumber2 = JsonNumber.newInstance().cast(jsonObject.get("key2"));
        
        Assert.assertTrue(jsonNumber1.doubleValue() == 3.14159);
        Assert.assertTrue(jsonNumber2.bigDecimalValue().toEngineeringString().equalsIgnoreCase("0.00023"));
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void invalidStringAsInput1_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_9);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void nonStringKeyAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_10);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void invalidStringAsInput2_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_12);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void invalidStringAsInput3_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_13);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void invalidStringAsInput4_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_14);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void invalidStringAsInput5_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_15);
    }
    
    @Test
    public void validScientificStringAsValueInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_16);
        JsonNumber jsonNumber = JsonNumber.newInstance().cast(jsonObject.get("key"));
        Assert.assertTrue(jsonNumber.bigDecimalValue().toEngineeringString().equalsIgnoreCase("215000.0"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullObjectAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(null);
    }
    
    @Test(expected = UnCastableObjectToInstanceTypeException.class)
    public void emptyStringAsKeyInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_17);
    }
    
    @Test
    public void getJsonObjectAsValue_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_5);
        javax.json.JsonObject jsonObject1 = jsonObject.getJsonObject("key4");
        Assert.assertTrue(jsonObject1.size() == 2);
    }
}