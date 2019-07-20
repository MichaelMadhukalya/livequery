package com.livequery.agent.filesystem.core;

import org.apache.commons.lang3.StringUtils;

public class FileEvent {
    /**
     * File event related fields
     */
    final String path;
    final String fileName;
    final String eventName;
    final int cookie;
    boolean isDirEvent = Boolean.FALSE;
    boolean isFileEvent = Boolean.FALSE;
    
    public FileEvent(String path, String fileName, String eventName) {
        this(path, fileName, eventName, -1);
    }
    
    public FileEvent(String path, String fileName, String eventName, int cookie) {
        this.path = path;
        this.fileName = fileName;
        this.eventName = eventName;
        this.cookie = cookie;
        // Derive dir or file event
        if (StringUtils.isNotEmpty(fileName)) {
            this.isFileEvent = Boolean.TRUE;
        } else if (StringUtils.isNotEmpty(path)) {
            this.isDirEvent = Boolean.TRUE;
        }
    }
    
    public boolean isDirEvent() {
        return isDirEvent;
    }
    
    public boolean isFileEvent() {
        return isFileEvent;
    }
}
