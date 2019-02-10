package com.livequery.common;

import com.google.common.collect.Maps;
import com.livequery.common.CodecParser.Status;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class CodecMapper {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * Internal state contains a file codec parser
     */
    final CodecParser codecParser;

    /**
     * Mapper
     */
    final Map<? super String, ? super Object> mapper = new HashMap<>();

    public CodecMapper() {
        this(new CodecParser(new Environment().getCodecFilePath()));
    }

    public CodecMapper(String filename) {
        this(new CodecParser(filename));
    }

    public CodecMapper(CodecParser codecParser) {
        this.codecParser = codecParser;
    }

    public Map getCodecMapper() {
        if (mapper.size() != 0) {
            return mapper;
        }
        parse();
        return mapper;
    }

    private int parse() {
        int lineCount = 0;
        Map<? super String, ? super Object> previous = mapper;
        Map<? super String, ? super Object> current = mapper;

        while (true) {
            Status status = Status.EOF.getStatus(codecParser.next());
            ++lineCount;

            String key = null;
            Object value = null;

            /* Further processing of the codec file will be done on the basis of returned value */
            switch (status) {
                case EOF:
                    break;
                case EMPTY_LINE:
                    current = previous;
                    continue;
                case COMMENT:
                    continue;
                case FIELD:
                    key = codecParser.getKey();
                    value = new HashMap<>();
                    current.put(key, value);
                    current = (Map<? super String, ? super Object>) value;
                    continue;
                case LINE:
                    key = codecParser.getKey();
                    value = codecParser.getValue().toString();
                    current.put(key, value);
                    logger.info(
                        String.format("Codec mapping found : (%s, %s)", key, value));
                    continue;
                default:
                    logger.error(
                        String.format("Error encountered while parsing codec file at line %d",
                            lineCount));
                    break;
            }
            break;
        }

        logger.info(String.format("Number of lines parsed from codec file : {%d}", lineCount));
        return lineCount;
    }

    /**
     * Reset mapper so that the codec file can be parsed again
     */
    public void reset() {
        mapper.clear();
    }

    public Map<String, Object> getSchema() {
        if (mapper.size() == 0) {
            parse();
        }

        logger.info(String.format("Number of entries found in Codec file : {%d}", mapper.size()));

        Object object = mapper.get("Fields");
        if (!(object instanceof Map)) {
            logger.error("Unable to find field mappings for user provided Schema");
            return Maps.newHashMap();
        }

        return (Map) object;
    }
}
