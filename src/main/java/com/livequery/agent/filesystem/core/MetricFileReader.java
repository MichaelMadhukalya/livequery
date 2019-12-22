package com.livequery.agent.filesystem.core;

import com.livequery.common.Environment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Optional;
import org.apache.log4j.Logger;

public class MetricFileReader<T> {
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
     * Maximum number of bytes read from file (~64 MB)
     */
    static final int MAXIMUM_BYTES_READ = 67_108_864;
    
    /**
     * File channel for reading chunks from the file
     */
    private FileChannel channel;
    
    /**
     * ByteBuffer for storing file contents
     */
    private ByteBuffer buffer = ByteBuffer.allocate(MAXIMUM_BYTES_READ);
    
    /**
     * Environment
     */
    private final Environment environment = new Environment();
    
    public MetricFileReader(String fileName) {
        try {
            this.fileName = fileName;
            channel = new FileInputStream(fileName).getChannel();
        } catch (FileNotFoundException e) {
            logger.error(String.format("Exception initializing file stream object : {%s}", e));
            throw new IllegalStateException(String.format("Unable to initialize stream object for reading file"));
        }
    }
    
    private Optional<byte[]> read() {
        /* Flip and reset buffer before next read iteration */
        buffer.flip();
        for (int i = buffer.position(); i < buffer.limit(); i++) {
            buffer.put(i, (byte) 0);
        }
        buffer.clear();
        
        try {
            int count = channel.read(buffer);
            logger.info(String.format("Number of bytes read from file : %s", count));
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
    
    private Map<T, T>[] deserialize() {
        String content = null;
        if (read().isPresent()) {
            try {
                content = new String(read().get(), environment.getEncoding());
            } catch (UnsupportedEncodingException e) {
                logger
                    .error(
                        String.format("Exception de-serializing bytes using encoding {%s} : {%s}", environment.getEncoding(), e));
            }
        }
        
        return null;
    }
}
