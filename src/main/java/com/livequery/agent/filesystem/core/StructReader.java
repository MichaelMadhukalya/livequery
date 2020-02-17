package com.livequery.agent.filesystem.core;

import com.livequery.common.Environment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.log4j.Logger;

class StructReader<T> {
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    
    /**
     * Filename
     */
    private final String fileName;
    
    /**
     * File offset seen or read until now
     */
    private long offset = 0L;
    
    /**
     * Maximum number of chars read from file (~64 MB)
     */
    private static final int MAXIMUM_BYTES_READ = 67_108_864;
    
    /**
     * File input stream reader in chars
     */
    private final InputStreamReader reader;
    private final CharBuffer buffer = CharBuffer.allocate(MAXIMUM_BYTES_READ);
    
    /**
     * Environment
     */
    private final Environment environment = new Environment();
    
    public StructReader(String fileName) {
        try {
            this.fileName = fileName;
            reader = new InputStreamReader(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            logger.error(String.format("Exception initializing file stream object : {%s}", e));
            throw new IllegalStateException(String.format("Unable to initialize stream object for reading file"));
        }
    }
    
    private Optional<char[]> read() {
        /* Flip and reset buffer before next read iteration */
        buffer.flip();
        for (int i = buffer.position(); i < buffer.limit(); i++) {
            buffer.put(i, ' ');
        }
        buffer.clear();
        
        int count = -1;
        try {
            /* Skip chars that have been processed */
            reader.skip(offset);
            count = reader.read(buffer);
            if (count <= 0) {
                return Optional.empty();
            }
            logger.info(String.format("Number of bytes read from file : {%s}", count));
        } catch (IOException e) {
            logger.error(String.format("Exception reading file stream object : {%s}", e));
            return Optional.empty();
        }
        
        return Optional.ofNullable(buffer.array());
    }
    
    private long getOffset() {
        return this.offset;
    }
    
    private void setOffset(long offset) {
        this.offset = offset;
    }
    
    public List<Map<T, T>>[] get() {
        String content = null;
        if (read().isPresent()) {
            content = read().get().toString();
        }
        
        logger.info(String.format("Data : %s", content));
        return null;
    }
}
