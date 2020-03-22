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
     * Current count of number of chars read from underlying file
     */
    private int count = 0;
    
    /**
     * Maximum number of chars read from file (~32 MB)
     */
    private static final int MAXIMUM_BYTES_READ = 33_553_920;
    
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
        count = 0;
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, Character.MIN_VALUE);
        }
        buffer.clear();
    }
    
    private Optional<CharBuffer> read() {
        /* Flip and reset buffer as well as counters before next read iteration */
        reset();
        
        try {
            /* Skip chars that have been processed */
            reader.skip(offset);
            
            long st = System.currentTimeMillis();
            count = reader.read(buffer);
            long et = System.currentTimeMillis();
            
            if (count <= 0) {
                logger.warn(String.format("Unable to read chars from file. Offset: {%d}", offset));
                return Optional.empty();
            }
            logger.debug(String.format("Chars read: {%s} Offset: {%d}. Time: {%d}", count, offset, et - st));
        } catch (IOException e) {
            logger.error(String.format("Exception reading file stream object : {%s}", e));
            return Optional.empty();
        }
        
        return Optional.ofNullable(buffer);
    }
    
    public List<Map<T, T>> get() {
        char[] content = null;
        Optional<CharBuffer> bufferOptional = read();
        
        if (bufferOptional.isPresent()) {
            CharBuffer buffer = bufferOptional.get();
            if (count > 0) {
                content = new char[count];
                buffer.flip();
                buffer.get(content);
            }
        }
        
        if (ArrayUtils.isNotEmpty(content)) {
            return deserialize(String.valueOf(content));
        }
        
        return new ArrayList<>();
    }
    
    private List<Map<T, T>> deserialize(String content) {
        logger.debug(String.format("Content: %s", content));
        int mark = 0;
        
        String[] records = StringUtils.split(content, END_OF_ENTRY_MARKER);
        if (ArrayUtils.isEmpty(records)) {
            logger.warn(String.format("Unable to parse records. Record format invalid."));
            return new ArrayList<>();
        }
        
        logger.debug(String.format("Number of valid records : %d", records.length));
        mark = StringUtils.lastIndexOf(content, END_OF_ENTRY_MARKER) + 8;
        offset += mark;
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
