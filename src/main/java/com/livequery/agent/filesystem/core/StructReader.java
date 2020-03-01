package com.livequery.agent.filesystem.core;

import com.livequery.common.Environment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
    
    /**
     * End of entry block marker
     */
    private static final String END_OF_ENTRY_MARKER = String.valueOf(new char[]{'E', 'O', 'E', '\n', '-', '-', '-', '\n'});
    
    public StructReader(String fileName) {
        try {
            this.fileName = fileName;
            reader = new InputStreamReader(new FileInputStream(fileName));
            reset();
        } catch (FileNotFoundException e) {
            logger.error(String.format("Exception initializing file stream object : {%s}", e));
            throw new IllegalStateException(String.format("Unable to initialize stream object for reading file"));
        }
    }
    
    private void reset() {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, Character.MIN_VALUE);
        }
    }
    
    private Optional<CharBuffer> read() {
        /* Flip and reset buffer before next read iteration */
        if (buffer.position() > 0) {
            buffer.flip();
            reset();
            buffer.clear();
        }
        
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
        
        return Optional.ofNullable(buffer);
    }
    
    public List<Map<T, T>> get() {
        String content = null;
        if (read().isPresent()) {
            CharBuffer buffer = read().get();
            if (buffer.hasRemaining()) {
                content = buffer.toString();
            }
        }
        
        logger.info(String.format("Data : %s", content));
        return deserialize(content);
    }
    
    private List<Map<T, T>> deserialize(String content) {
        int mark = 0;
        
        String[] records = StringUtils.split(content, END_OF_ENTRY_MARKER);
        if (ArrayUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        
        mark += StringUtils.lastIndexOf(content, END_OF_ENTRY_MARKER) + 8;
        offset = mark;
        List<Map<T, T>> vals = new ArrayList<>();
        Arrays.stream(records).map(this::parse).collect(Collectors.toList()).forEach(m -> vals.add((Map<T, T>) m));
        return vals;
    }
    
    private Map<Object, Object> parse(String record) {
        Map<Object, Object> map = new HashMap<>();
        Pattern pattern = Pattern.compile("(.+)=(.+)");
        
        for (int i = 0, j = 0; i < record.length() && j < record.length(); j++) {
            if (record.charAt(j) != '\n') {
                continue;
            } else {
                String sub = record.substring(i, j);
                Matcher matcher = pattern.matcher(sub);
                if (matcher.find() && matcher.groupCount() == 3) {
                    map.put(matcher.group(1), matcher.group(2));
                }
                i = j + 1;
            }
        }
        
        return map;
    }
}
