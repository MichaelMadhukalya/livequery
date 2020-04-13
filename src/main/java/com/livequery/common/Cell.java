package com.livequery.common;

import com.livequery.common.JsonType.UnCastableObjectToInstanceTypeException;

public class Cell<T extends JsonType> {
    /**
     * Internal state of the Cell
     */
    private final String name;
    private final JsonType<?> type;
    private T value;
    
    /**
     * Pointers
     */
    private Cell<T> next;
    private Cell<T> child;
    
    private Cell() {
        this(null, null, null);
    }
    
    public Cell(String name, JsonType<T> type, Object value) {
        this.name = name;
        this.type = type;
        try {
            this.value = type.cast(value);
        } catch (UnCastableObjectToInstanceTypeException e) {
            this.value = null;
        }
    }
    
    public T getValue() {
        return this.value;
    }
}
