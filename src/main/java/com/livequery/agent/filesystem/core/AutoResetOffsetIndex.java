package com.livequery.agent.filesystem.core;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * This enum is used to indicate the offset index from where a consumer wants to start consuming the
 * log file. By default it will be assumed that the consumer will start consuming data from the
 * beginning of the log file.
 */
public enum AutoResetOffsetIndex {
    /**
     * Begin
     */
    BEGIN(0),

    /**
     * End
     */
    END(1),

    /* Current */
    CURRENT(2);

    int value = -1;

    /**
     * Map of enums
     */
    static final Map<Integer, AutoResetOffsetIndex> cache =
        new ImmutableMap.Builder<Integer, AutoResetOffsetIndex>()
            .put(BEGIN.valueOf(), BEGIN)
            .put(END.valueOf(), END)
            .put(CURRENT.valueOf(), CURRENT)
            .build();

    AutoResetOffsetIndex(int value) {
        this.value = value;
    }

    public int valueOf() {
        return value;
    }

    public AutoResetOffsetIndex get(int value) {
        if (value != BEGIN.value && value != END.value && value != CURRENT.value) {
            throw new IllegalArgumentException(
                "Value provided for auto reset offset index is not valid");
        }

        return cache.get(value);
    }
}
