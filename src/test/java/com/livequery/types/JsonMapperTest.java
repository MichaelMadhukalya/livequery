package com.livequery.types;

import com.livequery.types.JsonType.UnCastableObjectToInstanceTypeException;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonMapperTest {
    String input =
        "{  \"key1\": \"value1\","
            + "\"key2\": null,"
            + "\"key3\": true,"
            + "\"key4\": 2.34E+02,"
            + "\"key5\" : [1, 2, 3.14159], "
            + "\"key6\": {\"key7\": \"value7\", \"key8\": [0, null, false]}"
            + "}";
    
    @Before
    public void before() {
    }
    
    @After
    public void after() {
    }
    
    @Test
    public void stringToJsonObject_test() throws UnCastableObjectToInstanceTypeException {
        JsonMapper mapper = new JsonMapper();
        JsonObject jsonObject = mapper.toJsonObject(input);
        Assert.assertTrue(null != jsonObject && jsonObject.size() == 6);
    }
    
    @Test
    public void jsonObjectToMap_test() throws UnCastableObjectToInstanceTypeException {
        JsonMapper mapper = new JsonMapper();
        JsonObject jsonObject = mapper.toJsonObject(input);
        Assert.assertTrue(null != jsonObject && jsonObject.size() == 6);
        Map<Object, Object> map = mapper.toMap(jsonObject);
        Assert.assertTrue(null != map && map.size() == 6);
    }
    
    @Test
    public void stringToMap_test() throws UnCastableObjectToInstanceTypeException {
        JsonMapper mapper = new JsonMapper();
        Map<Object, Object> map = mapper.toMap(input);
        Assert.assertTrue(null != map && map.size() == 6);
    }
    
    @Test
    public void jsonObjectToString_test() throws UnCastableObjectToInstanceTypeException {
        JsonMapper mapper = new JsonMapper();
        JsonObject jsonObject = mapper.toJsonObject(input);
        Assert.assertTrue(null != jsonObject && jsonObject.size() == 6 && mapper.toString(jsonObject).contains("null"));
    }
    
}
