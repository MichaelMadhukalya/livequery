package com.livequery.types;

public class Array {
    private static final int MAX_ARRAY_LENGTH = 1_024;
    private final Object[] objects;
    
    public Array() {
        this.objects = new Object[MAX_ARRAY_LENGTH];
    }
}
