package com.livequery.agent.storagenode.core;

import com.google.common.collect.ImmutableMap;
import com.livequery.common.Array;
import java.util.Map;
import java.util.Optional;
import org.objectweb.asm.Type;

public class JavaSupportedTypes {
    /**
     * Map of supported Java data types. The key represents type name whereas the value represents the type descriptor e.g.
     * ("string", "Ljava/lang/String") etc.
     */
    private final Map<String, String> types = new ImmutableMap.Builder<String, String>()
        .put("BOOLEAN", Type.getType(Boolean.class).getDescriptor())
        .put("INTEGER", Type.getType(Integer.class).getDescriptor())
        .put("LONG", Type.getType(Long.class).getDescriptor())
        .put("FLOAT", Type.getType(Float.class).getDescriptor())
        .put("DOUBLE", Type.getType(Double.class).getDescriptor())
        .put("NUMBER", Type.getType(Number.class).getDescriptor())
        .put("STRING", Type.getType(String.class).getDescriptor())
        .put("OBJECT", Type.getType(Object.class).getDescriptor())
        .put("ARRAY", Type.getType(Array.class).getDescriptor())
        .build();
    
    /**
     * Type tokens for type names
     */
    private final Map<String, Class<?>> tokens = new ImmutableMap.Builder<String, Class<?>>()
        .put("BOOLEAN", Boolean.class)
        .put("INTEGER", Integer.class)
        .put("LONG", Long.class)
        .put("FLOAT", Float.class)
        .put("DOUBLE", Double.class)
        .put("NUMBER", Number.class)
        .put("STRING", String.class)
        .put("OBJECT", Object.class)
        .put("ARRAY", Array.class)
        .build();
    
    /**
     * Get type descriptor for the given typeName (data type name)
     *
     * @param typeName Type name e.g. Number, String, Timestamp etc.
     * @return Type descriptor name in String format e.g. Ljava/lang/String
     */
    public Optional<String> getDescriptor(String typeName) {
        return Optional.ofNullable(types.get(typeName.toLowerCase()));
    }
    
    /**
     * Return the type token corresponding to the type name
     *
     * @param typeName Type name e.g. Number, String, Timestamp etc.
     * @return Type token corresponding to the type name
     */
    public <T> Optional<Class<T>> getTypeToken(String typeName) {
        Class<?> clazz = tokens.get(typeName.toLowerCase());
        if (null == clazz) {
            return Optional.empty();
        }
        
        return Optional.ofNullable((Class<T>) tokens.get(typeName.toLowerCase()));
    }
    
    /**
     * @return Java type descriptors supported
     */
    public Map<String, String> getTypeDescriptors() {
        return types;
    }
}
