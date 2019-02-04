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
 * This class implements a simple <emp>Codec</emp> parser that reads an input codec file line by
 * line and extracts key/value pairs from the file.
 */
class CodecParser implements ICodecParser<String, Object> {

  /**
   * Logger
   */
  private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

  /**
   * Filename
   */
  private final String filename;

  /**
   * RegEx for comment line
   */
  private final Pattern REGEX_COMMENT = Pattern.compile("\\s*(#.*)");

  /**
   * RegEx for empty line
   */
  private final Pattern REGEX_EMPTY_LINE = Pattern.compile("\\s+");

  /**
   * RegEx for valid input line
   */
  private final Pattern REGEX_INPUT = Pattern.compile("\\s*(@?\\S+)\\s*=\\s*([a-zA-Z])");

  /**
   * RegEx for field attributes
   */
  private final Pattern REGEX_FIELD = Pattern.compile("\\s*(\\S+)\\s*:");

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
    static final Map<Integer, Status> reverseMap = new ImmutableMap.Builder<Integer, Status>()
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
      if (reverseMap.containsKey(value)) {
        return reverseMap.get(value);
      }
      throw new RuntimeException(String.format("Unable to find status code for value %d", value));
    }
  }

  public CodecParser(String filename) {
    this.filename = filename;
  }

  @Override
  public int next() {
    String line = nextLine();
    if (StringUtils.isEmpty(line)) {
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
    if (groupCount != 3) {
      logger.warn(String
          .format("For key/value parsing in codec file group count should be 3 but found %d",
              groupCount));
    }

    key = matcher.group(1);
    value = matcher.group(2);
    return LINE.getValue();
  }

  @Override
  public String nextKey() {
    return key;
  }

  @Override
  public Object nextValue() {
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

    return isNull.test(line) ? StringUtils.EMPTY : line;
  }

  private void init() {
    try {
      bufferedReader = new BufferedReader(new FileReader(filename));
    } catch (FileNotFoundException e) {
      logger.error(String.format("Exception encountered while opening file {%s}", e));
    }
  }

  private void release() {
    try {
      bufferedReader.close();
    } catch (IOException e) {
      logger.error(String.format("Unable to close buffered reader. Exception encountered {%s}", e));
    }
  }

  private boolean isComment(String line) {
    Predicate<Matcher> isComment = matcher -> matcher.find();
    return isComment.test(REGEX_COMMENT.matcher(line));
  }

  private boolean isEmpty(String line) {
    if (isComment(line)) {
      return false;
    }

    Predicate<Matcher> isEmpty = matcher -> matcher.find();
    return isEmpty.test(REGEX_EMPTY_LINE.matcher(line));
  }
}
