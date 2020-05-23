package com.livequery.types;

import com.livequery.types.JsonType.UnCastableObjectToInstanceTypeException;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
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
        + "{\"key9\": [0.0096, 8.0E+02, {\"key10\": \"value10\", \"key11\": [1001.01, 2.1E-03, [[1.6E+02,0.2,3.14159], 4.3E+03]]}]}}]}";
    
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
    
    String INPUT_18 = "{\"key\": null}";
    
    String INPUT_19 = "{\"key\": \"null\"}";
    
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
        
        JsonType<?> valueType = (JsonType) jsonObject.get("key2");
        Assert.assertTrue(valueType.typeOf() instanceof JsonNull);
        Assert.assertTrue((((JsonNull) valueType.typeOf()).toString().equals("null")));
        
        JsonNull jsonNull = JsonNull.newInstance();
        jsonNull.cast(valueType);
        Assert.assertTrue(null != jsonNull);
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
    public void validNumberValueInput_Test() throws UnCastableObjectToInstanceTypeException {
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
    
    @Test
    public void getJsonArrayAsValue_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_5);
        javax.json.JsonArray jsonArray = jsonObject.getJsonArray("key7");
        Assert.assertTrue(jsonArray.size() == 3);
    }
    
    @Test
    public void verifyNumberValues_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_5);
        javax.json.JsonArray jsonArray = jsonObject.getJsonArray("key7");
        Assert.assertTrue(jsonArray.size() == 3);
        
        JsonValue value = jsonArray.get(2);
        JsonObject jsonObject1 = JsonObject.newInstance();
        jsonObject1.cast(value);
        Assert.assertTrue(jsonObject1.size() == 1);
        
        value = jsonObject1.getJsonObject("key8");
        JsonObject jsonObject2 = JsonObject.newInstance();
        jsonObject2.cast(value);
        Assert.assertTrue(jsonObject2.size() == 1);
        
        value = jsonObject2.getJsonArray("key9");
        JsonArray jsonArray1 = JsonArray.newInstance();
        jsonArray1.cast(value);
        Assert.assertTrue(jsonArray1.size() == 3);
        
        value = jsonArray1.get(0);
        JsonNumber number1 = JsonNumber.newInstance();
        number1.cast(value);
        Assert.assertTrue(number1.toString().equals("0.0096"));
        
        value = jsonArray1.get(1);
        JsonNumber number2 = JsonNumber.newInstance();
        number2.cast(value);
        Assert.assertTrue(number2.toString().equals("800.0"));
        
        value = jsonArray1.get(2);
        JsonObject jsonObject3 = JsonObject.newInstance();
        jsonObject3.cast(value);
        Assert.assertTrue(jsonObject3.size() == 2);
        
        value = jsonObject3.get("key11");
        JsonArray jsonArray2 = JsonArray.newInstance();
        jsonArray2.cast(value);
        Assert.assertTrue(jsonArray2.size() == 3);
        
        value = jsonArray2.get(0);
        JsonNumber number3 = JsonNumber.newInstance();
        number3.cast(value);
        Assert.assertTrue(number3.toString().equals("1001.01"));
        
        value = jsonArray2.get(1);
        JsonNumber number4 = JsonNumber.newInstance();
        number4.cast(value);
        Assert.assertTrue(number4.toString().equals("0.0021"));
        
        value = jsonArray2.get(2);
        JsonArray jsonArray3 = JsonArray.newInstance();
        jsonArray3.cast(value);
        Assert.assertTrue(jsonArray3.size() == 2);
        
        value = jsonArray3.get(0);
        JsonArray jsonArray4 = JsonArray.newInstance();
        jsonArray4.cast(value);
        Assert.assertTrue(jsonArray4.size() == 3);
        
        value = jsonArray4.get(0);
        JsonNumber number5 = JsonNumber.newInstance();
        number5.cast(value);
        Assert.assertTrue(number5.toString().equals("160.0"));
        
        value = jsonArray4.get(1);
        JsonNumber number6 = JsonNumber.newInstance();
        number6.cast(value);
        Assert.assertTrue(number6.toString().equals("0.2"));
        
        value = jsonArray4.get(2);
        JsonNumber number7 = JsonNumber.newInstance();
        number7.cast(value);
        Assert.assertTrue(number7.toString().equals("3.14159"));
    }
    
    @Test
    public void getNumberValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_16);
        javax.json.JsonNumber jsonNumber = jsonObject.getJsonNumber("key");
        Assert.assertTrue(jsonNumber.toString().equals("215000.0"));
    }
    
    @Test
    public void getJsonStringValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_1);
        javax.json.JsonString jsonString = jsonObject.getJsonString("key");
        Assert.assertTrue(jsonString.toString().equals("\"value\""));
    }
    
    @Test
    public void getStringValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_1);
        String s = jsonObject.getString("key");
        Assert.assertTrue(null != s && s.equals("\"value\""));
    }
    
    @Test
    public void getIntValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_16);
        int num = jsonObject.getInt("key");
        Assert.assertTrue(num == 215000);
    }
    
    @Test
    public void getBooleanValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_7);
        Boolean value = jsonObject.getBoolean("key1");
        Assert.assertTrue(value == Boolean.FALSE);
    }
    
    @Test
    public void getNullValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_6);
        Assert.assertTrue(jsonObject.isNull("key3") == false);
    }
    
    @Test
    public void nullValueAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_18);
        Assert.assertTrue(jsonObject.isNull("key") == true);
    }
    
    @Test
    public void getValueType_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_1);
        Assert.assertTrue(jsonObject.getValueType().equals(ValueType.OBJECT));
    }
    
    @Test
    public void isEmpty_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_1);
        Assert.assertTrue(jsonObject.isEmpty() == false);
    }
    
    @Test
    public void clearAndVerifyIsEmpty_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_1);
        jsonObject.clear();
        Assert.assertTrue(jsonObject.isEmpty());
    }
    
    @Test
    public void verifyContainsKey_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_5);
        Assert.assertTrue(jsonObject.containsKey("key4"));
    }
    
    @Test
    public void verifyContainsValue_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_5);
        
        JsonType<?> valueType = (JsonType<?>) jsonObject.get("key4");
        Assert.assertTrue(jsonObject.containsValue(valueType.typeOf()) == true);
        
        JsonObject jsonObject1 = JsonObject.newInstance();
        jsonObject1.cast(valueType);
        Assert.assertTrue(jsonObject.containsValue(jsonObject1) == false);
    }
    
    @Test
    public void verifyEntrySetElementCount_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_5);
        Assert.assertTrue(jsonObject.entrySet().size() == 5);
    }
    
    @Test
    public void verifyStringValueWithNullWordAsInput_Test() throws UnCastableObjectToInstanceTypeException {
        JsonObject jsonObject = JsonObject.newInstance();
        jsonObject.cast(INPUT_19);
        
        JsonType<?> valueType = (JsonType<?>) jsonObject.get("key");
        Assert.assertTrue(valueType instanceof JsonString);
    }
}