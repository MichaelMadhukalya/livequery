package com.livequery.common;

import static com.livequery.common.CodecParser.Status.COMMENT;
import static com.livequery.common.CodecParser.Status.EMPTY_LINE;
import static com.livequery.common.CodecParser.Status.EOF;
import static com.livequery.common.CodecParser.Status.FIELD;
import static com.livequery.common.CodecParser.Status.LINE;

import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class implements a simple <code>Codec</code> parser that reads an input codec file line by
 * line and extracts key/value pairs from the file. Objects of this class are supposed to be
 * instantiated from <code>CodecMapper</code> class.
 */
class CodecParser implements ICodecParser<String, Object> {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * Codec filename
     */
    private String filename;

    /**
     * RegEx for comment line
     */
    private final Pattern REGEX_COMMENT = Pattern.compile("\\s*[\\@\\$]?([\\w]*).*(#.*)");

    /**
     * RegEx for non empty line.
     */
    private final Pattern REGEX_NON_EMPTY_LINE = Pattern.compile("\\S+");

    /**
     * RegEx for valid input line
     */
    private final Pattern REGEX_INPUT = Pattern
        .compile("\\s*[\\@\\$]?(\\w+)\\s*=\\s*([\\w\\.\\/\\-]+).*");

    /**
     * RegEx for field attributes
     */
    private final Pattern REGEX_FIELD = Pattern.compile("\\s*([a-zA-Z]+)\\s*:.*");

    /**
     * Fields representing internal state of the codec parser
     */
    String key;
    Object value;

    /**
     * Buffered Reader for reading a character stream into lines
     */
    BufferedReader bufferedReader;

    /**
     * An enum representing the status of each extracted line from codec file.
     */
    static enum Status {
        EOF(-1),
        EMPTY_LINE(0),
        COMMENT(1),
        FIELD(2),
        LINE(3);

        final int status;
        static final Map<Integer, Status> statusCodes = new ImmutableMap.Builder<Integer, Status>()
            .put(-1, EOF)
            .put(0, EMPTY_LINE)
            .put(1, COMMENT)
            .put(2, FIELD)
            .put(3, LINE)
            .build();

        Status(int status) {
            this.status = status;
        }

        public int getValue() {
            return status;
        }

        public Status getStatus(int value) {
            if (statusCodes.containsKey(value)) {
                return statusCodes.get(value);
            }
            throw new RuntimeException(
                String.format("Unable to find status code for value %d", value));
        }
    }

    public CodecParser(String filename) {
        this.filename = filename;
    }

    @Override
    public int next() {
        String line = nextLine();
        if (null == line) {
            // release resource and close
            release();
            return EOF.getValue();
        }

        if (isEmpty(line)) {
            return EMPTY_LINE.getValue();
        }

        if (isComment(line)) {
            return COMMENT.getValue();
        }

        Matcher matcher = REGEX_FIELD.matcher(line);
        if (matcher.find()) {
            key = matcher.group(1);
            value = StringUtils.EMPTY;
            return FIELD.getValue();
        }

        /* Finally match and extract a key/value pair from codec. This has to match for correctness */
        matcher = REGEX_INPUT.matcher(line);
        if (!matcher.find()) {
            logger.error(String.format("Unable to find key/value pattern in codec file"));
        }

        // Ensure that we have three groups from the RegEx match with key at group 1 and value at group 2
        int groupCount = matcher.groupCount();
        if (groupCount != 2) {
            logger.warn(String
                .format("For key/value parsing in codec file group count should be 2 but found %d",
                    groupCount));
        }

        key = matcher.group(1);
        value = matcher.group(2);
        return LINE.getValue();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    private String nextLine() {
        Predicate<Object> isNull = object -> object == null;
        if (isNull.test(bufferedReader)) {
            init();
        }

        String line = null;
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            logger.error(String.format("Exception encountered while reading from file {%s}", e));
        }

        return line;
    }

    private void init() {
        try {
            /* Attempt to reload codec file if not present */
            if (StringUtils.isEmpty(filename)) {
                logger.warn(
                    String.format("Unable to load codec file path : {%s}. Reloading.", filename));
                filename = new Environment().getCodecFilePath();
            }

            bufferedReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            logger.error(String
                .format("Exception encountered while opening file {%s} with {%s}", filename, e));
        }
    }

    private void release() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            logger.error(
                String.format("Unable to close buffered reader. Exception encountered {%s}", e));
        }
    }

    private boolean isComment(String line) {
        Matcher matcher = REGEX_COMMENT.matcher(line);
        if (!matcher.find()) {
            return false;
        }

        /*
         * If it is a comment line need to ensure that it is not a parital comment line. We
         * distinguish between the two types of comment lines. E.g. consider the two lines:
         *
         * @Customer = String # Comment starts here
         * # This is a comment
         *
         * The first line is considered a partial comment line whereas the second line is considered
         * a full comment line. The second line is ignored during parsing of the codec file.
         */

        int groupCount = matcher.groupCount();
        String prefix = matcher.group(1);

        return StringUtils.isEmpty(prefix) ? true : false;
    }

    private boolean isEmpty(String line) {
        if (isComment(line)) {
            return false;
        }

        if (StringUtils.isEmpty(line) || StringUtils.isBlank(line)) {
            return true;
        }

        /* A line with non chars e.g. tabs, spaces, newline is also considered empty */
        Predicate<Matcher> isEmpty = matcher -> matcher.find();
        return !isEmpty.test(REGEX_NON_EMPTY_LINE.matcher(line));
    }
}
