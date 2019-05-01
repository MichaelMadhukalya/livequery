package com.livequery.agent.filesystem.core;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum AutoResetOffsetIndex {
    /**
     * Begin
     */
    AUTO_RESET_OFFSET_INDEX_BEGIN(0),

    /**
     * End
     */
    AUTO_RESET_OFFSET_INDEX_END(1),

    /* Current */
    AUTO_RESET_OFFSET_INDEX_CURRENT(2);

    int value = -1;

    /**
     * Map of enums
     */
    static final Map<Integer, AutoResetOffsetIndex> cache =
        new ImmutableMap.Builder<Integer, AutoResetOffsetIndex>()
            .put(AUTO_RESET_OFFSET_INDEX_BEGIN.valueOf(), AUTO_RESET_OFFSET_INDEX_BEGIN)
            .put(AUTO_RESET_OFFSET_INDEX_END.valueOf(), AUTO_RESET_OFFSET_INDEX_END)
            .put(AUTO_RESET_OFFSET_INDEX_CURRENT.valueOf(), AUTO_RESET_OFFSET_INDEX_CURRENT)
            .build();

    AutoResetOffsetIndex(int value) {
        this.value = value;
    }

    public int valueOf() {
        return this.value;
    }

    public AutoResetOffsetIndex get(int value) {
        if (value != 0 && value != 1 && value != 2) {
            throw new IllegalArgumentException(
                "Value provided for auto reset offset index is not valie");
        }

        return cache.get(value);
    }
}
