package com.livequery.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

public class Document {
    /**
     * Logger
     */
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    
    /**
     * Internal map where key and values are stored for the Document object after parsing in raw format.
     */
    private final Map<String, Object> raw = new LinkedHashMap<>();
    
    /**
     * Json for document
     */
    private String json;
    
    /**
     * UserDefinedType
     */
    private static final UserDefinedType USER_DEFINED_TYPE = new UserDefinedType();
    private static final Map<String, Class<?>> MAPPERS = new HashMap<>();
    
    static {
        Map<String, Class<?>> mappers = USER_DEFINED_TYPE.getTypeMappers();
        mappers.entrySet().stream().forEach(e -> MAPPERS.put(e.getKey(), e.getValue()));
    }
    
    public Document(Map<?, ?> map) {
        if (MapUtils.isEmpty(map)) {
            LOG.warn(String.format("Document initialization using empty input map"));
        } else {
            map.entrySet().stream().forEach(e -> raw.put((String) e.getKey(), (Object) e.getValue()));
            map.entrySet().stream().forEach(e -> LOG.debug(String.format("[Key=%s,Value=%s]", e.getKey(), e.getValue())));
        }
    }
    
    private Map<String, Object> toRawMap() {
        return raw;
    }
    
    @Override
    public boolean equals(Object that) {
        if (null == that) {
            return false;
        }
        
        Document doc = (Document) that;
        if (null == doc.toRawMap() || doc.toRawMap().size() != raw.size()) {
            return false;
        }
        
        long match = raw.entrySet()
            .stream()
            .filter(e -> doc.toRawMap().containsKey(e.getKey()) && doc.toRawMap().get(e.getKey()).equals(e.getValue()))
            .count();
        return match == raw.size();
    }
    
    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        raw.entrySet().stream().forEach(e -> builder.append(e));
        return builder.hashCode();
    }
    
    public String toJson() {
        if (StringUtils.isEmpty(json)) {
            Type type = new TypeToken<Map<Object, Object>>() {
            }.getType();
            json = new Gson().toJson(raw, type);
        }
        
        return json;
    }
}
