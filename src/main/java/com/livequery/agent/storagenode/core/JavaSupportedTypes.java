package com.livequery.agent.storagenode.core;

import com.google.common.collect.ImmutableMap;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.objectweb.asm.Type;

public class JavaSupportedTypes {
    
    /**
     * Map of supported Java data types. The key represents type name whereas the value represents the type descriptor e.g.
     * ("string", "Ljava/lang/String") etc.
     */
    private final Map<String, String> types = new ImmutableMap.Builder<String, String>()
        .put("boolean", Type.getType(Boolean.class).getDescriptor())
        .put("integer", Type.getType(Integer.class).getDescriptor())
        .put("long", Type.getType(Long.class).getDescriptor())
        .put("double", Type.getType(Double.class).getDescriptor())
        .put("number", Type.getType(Number.class).getDescriptor())
        .put("string", Type.getType(String.class).getDescriptor())
        .put("timestamp", Type.getType(Timestamp.class).getDescriptor())
        .build();
    
    /**
     * Type tokens for type names
     */
    private final Map<String, Class<?>> tokens = new ImmutableMap.Builder<String, Class<?>>()
        .put("boolean", Boolean.class)
        .put("integer", Integer.class)
        .put("long", Long.class)
        .put("double", Double.class)
        .put("number", Number.class)
        .put("string", String.class)
        .put("timestamp", Timestamp.class)
        .build();
    
    public JavaSupportedTypes() {
    }
    
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
        
        Predicate<Object> isNull = object -> object == null;
        if (isNull.test(clazz)) {
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
