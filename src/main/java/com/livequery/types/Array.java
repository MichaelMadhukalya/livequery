package com.livequery.types;

public class Array {
    private int MAX_ARRAY_LENGTH = 1_024;
    private Object[] objects;
    
    public Array() {
        this.objects = new Object[MAX_ARRAY_LENGTH];
    }
}
