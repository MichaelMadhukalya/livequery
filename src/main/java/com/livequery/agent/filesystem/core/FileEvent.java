package com.livequery.agent.filesystem.core;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
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
    
    public int getCookie() {
        return cookie;
    }
    
    @Override
    public String toString() {
        return new StringBuffer()
            .append("[")
            .append(path)
            .append(",")
            .append(fileName)
            .append(",")
            .append(eventName)
            .append(",")
            .append(cookie)
            .append(",")
            .append(isDirEvent)
            .append(",")
            .append(isFileEvent)
            .append("]")
            .toString();
    }
    
    /**
     * FileEventType represents a kernel file event
     */
    public enum FileEventType {
        IN_ACCESS(0),
        IN_ATTRIB(1),
        
        IN_CLOSE_NOWRITE(2),
        IN_CLOSE_WRITE(3),
        
        IN_CREATE(4),
        IN_DELETE(5),
        IN_DELETE_SELF(6),
        
        IN_IGNORED(7),
        IN_ISDIR(8),
        IN_MODIFY(9),
        
        IN_MOVE_SELF(10),
        IN_MOVED_FROM(11),
        IN_MOVED_TO(12),
        
        IN_OPEN(13),
        IN_Q_OVERFLOW(14),
        IN_UNMOUNT(15);
        
        final int type;
        
        static final Map<String, FileEventType> eventTypeMap =
            ImmutableMap.<String, FileEventType>builder()
                .put(IN_ACCESS.name(), IN_ACCESS)
                .put(IN_ATTRIB.name(), IN_ATTRIB)
                .put(IN_CLOSE_NOWRITE.name(), IN_CLOSE_NOWRITE)
                .put(IN_CLOSE_WRITE.name(), IN_CLOSE_WRITE)
                .put(IN_CREATE.name(), IN_CREATE)
                .put(IN_DELETE.name(), IN_DELETE)
                .put(IN_DELETE_SELF.name(), IN_DELETE_SELF)
                .put(IN_IGNORED.name(), IN_IGNORED)
                .put(IN_ISDIR.name(), IN_ISDIR)
                .put(IN_MODIFY.name(), IN_MODIFY)
                .put(IN_MOVE_SELF.name(), IN_MOVE_SELF)
                .put(IN_MOVED_FROM.name(), IN_MOVED_FROM)
                .put(IN_MOVED_TO.name(), IN_MOVED_TO)
                .put(IN_OPEN.name(), IN_OPEN)
                .put(IN_Q_OVERFLOW.name(), IN_Q_OVERFLOW)
                .put(IN_UNMOUNT.name(), IN_UNMOUNT)
                .build();
        
        FileEventType(int type) {
            this.type = type;
        }
        
        static FileEventType get(String eventTypeName) {
            if (eventTypeMap.containsKey(eventTypeName)) {
                return eventTypeMap.get(eventTypeName);
            }
            
            throw new RuntimeException(String.format("Unable to find file event type for name : %s", eventTypeName));
        }
    }
}
