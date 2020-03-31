package com.livequery.agent.filesystem.core;

import com.livequery.common.Environment;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

class StructReader implements AutoCloseable {
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
    private BufferedReader reader;
    private final CharBuffer buffer = CharBuffer.allocate(MAXIMUM_BYTES_READ);
    
    /**
     * Environment
     */
    private final Environment environment = new Environment();
    
    /**
     * End of entry block marker
     */
    private static final String END_OF_ENTRY_MARKER = String.valueOf(new char[]{'E', 'O', 'E', '\n', '-', '-', '-', '\n'});
    
    /**
     * Line pattern
     */
    private static final Pattern LINE_PATTERN = Pattern.compile("(.*)=(.*)");
    
    public StructReader(String fileName) {
        this.fileName = fileName;
    }
    
    private void reset() {
        count = 0;
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, Character.MIN_VALUE);
        }
        buffer.clear();
    }
    
    @Override
    public void close() {
        try {
            if (null != reader) {
                reader.close();
            }
            logger.debug(String.format("Input stream reader closed successfully"));
        } catch (IOException e) {
            logger.error(String.format("Exception closing input stream reader: {%s}", e));
        }
    }
    
    private Optional<CharBuffer> read() {
        /* Flip and reset buffer as well as counters before next read iteration */
        reset();
        
        try {
            /* Open reader */
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), new Environment().getEncoding()));
            /* Skip chars that have been processed */
            reader.skip(offset);
            
            long st = System.currentTimeMillis();
            count = reader.read(buffer);
            long et = System.currentTimeMillis();
            
            /* Verify count of chars read */
            if (count <= 0) {
                logger.warn(String.format("Unable to read chars from input stream"));
                return Optional.empty();
            }
            logger.debug(String.format("Chars read: {%s} from start Offset: {%d} Time: {%d}", count, offset, et - st));
        } catch (IOException e) {
            logger.error(String.format("Exception reading file stream object : {%s}", e));
            return Optional.empty();
        } finally {
            close();
        }
        
        return Optional.ofNullable(buffer);
    }
    
    public <K, V> List<Map<K, V>> get() {
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
    
    private <K, V> List<Map<K, V>> deserialize(String content) {
        int mark = 0;
        
        String[] records = StringUtils.split(content, '\n');
        if (ArrayUtils.isEmpty(records)) {
            logger.warn(String.format("Unable to parse records. Record format invalid."));
            return new ArrayList<>();
        }
        
        mark = StringUtils.lastIndexOf(content, END_OF_ENTRY_MARKER) + 8;
        offset += mark;
        
        List<Map<K, V>> vals = new ArrayList<>();
        parse(records).forEach(m -> vals.add((Map<K, V>) m));
        return vals;
    }
    
    private List<Map<Object, Object>> parse(String[] record) {
        List<Map<Object, Object>> values = new ArrayList<>();
        Map<Object, Object> map = new LinkedHashMap<>();
        
        for (int i = 0; i < record.length; i++) {
            if (record[i].equals(StringUtils.EMPTY)) {
            } else if (record[i].equals("EOE")) {
                values.add(map);
                map = new HashMap<>();
            } else if (record[i].equals("---")) {
            } else {
                String sub = record[i];
                Matcher matcher = LINE_PATTERN.matcher(sub);
                if (matcher.find() && matcher.groupCount() >= 2) {
                    map.put(matcher.group(1), matcher.group(2));
                }
            }
        }
        
        return values;
    }
}
