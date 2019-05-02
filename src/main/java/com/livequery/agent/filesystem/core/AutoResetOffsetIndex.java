package com.livequery.agent.filesystem.core;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

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
        if (value != 0 && value != 1 && value != 2) {
            throw new IllegalArgumentException(
                "Value provided for auto reset offset index is not valid");
        }

        return cache.get(value);
    }
}
