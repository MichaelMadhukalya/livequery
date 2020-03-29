package com.livequery.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
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
     * Internal map where key and values are stored for the Document object
     */
    private final Map<Object, Object> map = new HashMap<>();
    
    /**
     * Json for document
     */
    private String json;
    
    public Document(Map<?, ?> map) {
        if (MapUtils.isEmpty(map)) {
            LOG.warn(String.format("Document initialization using empty input map"));
        } else {
            map.entrySet().stream().forEach(e -> this.map.put((Object) e.getKey(), (Object) e.getValue()));
        }
    }
    
    public Map<Object, Object> toMap() {
        return map;
    }
    
    @Override
    public boolean equals(Object that) {
        if (null == that) {
            return false;
        }
        
        Document doc = (Document) that;
        if (null == doc.toMap() || doc.toMap().size() != map.size()) {
            return false;
        }
        
        long match = map.entrySet()
            .stream()
            .filter(e -> doc.toMap().containsKey(e.getKey())
                && doc.toMap().get(e.getKey()).equals(e.getValue()))
            .count();
        return match == map.size();
    }
    
    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        map.entrySet().stream().forEach(e -> builder.append(e));
        return builder.hashCode();
    }
    
    @Override
    public String toString() {
        return toJson();
    }
    
    public String toJson() {
        if (StringUtils.isEmpty(json)) {
            Type type = new TypeToken<Map<Object, Object>>() {
            }.getType();
            json = new Gson().toJson(map, type);
        }
        
        return json;
    }
}
