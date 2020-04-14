package com.livequery.common;

import com.livequery.types.JsonType;
import com.livequery.types.JsonType.UnCastableObjectToInstanceTypeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class Cell<T extends JsonType> {
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    
    /**
     * Internal state of the Cell
     */
    private final int index;
    private final String name;
    private final JsonType<?> type;
    private T value;
    
    /**
     * Pointers
     */
    private Cell<T> next;
    private Cell<T> child;
    
    private Cell() {
        this(-1, null, null, null);
    }
    
    public Cell(int index, String name, JsonType<T> type, Object value) {
        this.index = index;
        this.name = name;
        this.type = type;
        try {
            this.value = type.cast(value);
        } catch (UnCastableObjectToInstanceTypeException e) {
            this.value = null;
            logger.error(String.format("Exception creating new cell for object: {%s}", e));
        }
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getName() {
        return StringUtils.isEmpty(name) ? StringUtils.join(new String[]{"$", "_", String.valueOf(index)}) : name;
    }
    
    public JsonType<T> getType() {
        return (JsonType<T>) type;
    }
    
    public T getValue() {
        return value;
    }
}
