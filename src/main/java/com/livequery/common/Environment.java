package com.livequery.common;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * The class <code>Environment</code> provides access to properties defined in the
 * <code>.properties</code> file. These properties are specific to <emp>livequery</emp> application
 * and are different from system environment properties (<code>System.getEnv()</code>) as well as
 * properties that are set as input to JVM e.g. <code>-Dlivequery.root=/apollo/env/livequery</code>
 * </p>
 *
 * <p>
 * An example of such property is the <emp>log4j.config</emp> property using which
 * <code>log4j</code> config is set up for the application:
 * <p>
 * <code>log4j.config = ${livequery.config}/log4j.properties</code>
 * </p>
 *
 * <p>
 * There are two specific modes in which objects of this class are instantiated:
 * <ul>
 * <li> The default constructor does not accept any argument and should be called to instantiate
 * an object of this class <b>only after</b> <code>.properties</code> file has been loaded and
 * symbols within the file resolved.
 * </li>
 * <li> The non-default constructor takes livequery application root path as input argument and
 * loads the <code>.properties</code> file. During this process symbols such as ${livequery.base}
 * are resolved and replaced with appropriate values. This constructor should be called only
 * <b>once</b>.
 * </li>
 * </ul>
 *
 * <b>Note: </b> This class does not have a logger associated with it since in the most likely
 * scenario the first object of this class will instantiated even before <code>log4j</code> for the
 * application is configured.
 */
public class Environment {

  /**
   * Properties file
   */
  private final Map<Object, Object> properties = new LinkedHashMap<>();

  /**
   * Root path
   */
  private static String rootPath = StringUtils.EMPTY;

  public Environment() {
  }

  /**
   * This constructor should be called only once preferably when the system is getting initialized.
   * The constructor takes a single input argument that points to root path of the application. The
   * root path will be set using the -Dlivequery.root JVM property.
   */
  public Environment(String rootPath) {
    if (StringUtils.isNotEmpty(Environment.rootPath)) {
      throw new IllegalStateException(
          "This constructor should be called once when livequery runtime environment is initialized");
    }

    /* Set root path */
    Environment.rootPath = rootPath;

    /* Load properties */
    load();
  }

  private String stringify(String in) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < in.length(); i++) {
      char ch = in.charAt(i);
      if (ch != '$' && ch != '{' && ch != '}') {
        buffer.append(ch);
      }
    }

    return buffer.toString();
  }

  private String getPropertiesFilePath() {
    String path = appendPath(getRootPath(), new String[]{".properties"});

    File file = new File(path);
    if (!file.exists()) {
      throw new IllegalStateException(
          String.format("Unable to find properties file with specified filename %s", path));
    }

    return file.getAbsolutePath();
  }

  private String appendPath(String arg, String[] args) {
    StringBuffer sb = new StringBuffer().append(arg);
    if (sb.charAt(sb.length() - 1) != '/') {
      sb.append("/");
    }

    Arrays.asList(args).stream()
        .forEach(s -> sb.append(s).append("/"));
    return sb.deleteCharAt(sb.length() - 1).toString();
  }

  public Map<Object, Object> getProperties() {
    return properties;
  }

  public String getProperty(String name) {
    return (String) properties.get(name);
  }

  public String getCodecFilePath() {
    return (String) properties.get("livequery.codec");
  }

  public String getLog4jProperties() {
    return (String) properties.get("log4j.config");
  }

  public String getClassPath() {
    return (String) properties.get("classpath");
  }

  private String getRootPath() {
    return rootPath;
  }

  /**
   * Load properties object for livequery environment. If the properties object already has
   * properties associated with it then clears all existing properties and re-loads them.
   */
  private void load() {
    if (properties.size() > 0) {
      properties.clear();
    }

    String path = getPropertiesFilePath();

    try {
      Pattern pattern = Pattern.compile("(.*)=(.*)");
      List<String> lines = Files.readLines(new File(path), Charset.defaultCharset());
      lines.stream()
          .forEach(s -> {
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
              String key = matcher.group(1).trim();
              String value = matcher.group(2).trim();
              properties.put(key, value);
            }
          });
    } catch (IOException e) {
    }

    /* Resolve property values replacing property key parts with actual values e.g. a value of
     * ${livequery.base} will be replaced with actual values during resolution.
     */
    resolve();
  }

  private void resolve() {
    Map<Object, Object> entries = new LinkedHashMap<>();

    properties.entrySet().stream()
        .forEach(e -> {
          AbstractMap.SimpleEntry<String, String> entry =
              new AbstractMap.SimpleEntry<String, String>((String) e.getKey(),
                  extract((String) e.getValue(), entries));
          entries.put(entry.getKey(), entry.getValue());
        });

    properties.clear();
    entries.entrySet().forEach(e -> properties.put(e.getKey(), e.getValue()));
  }

  private String extract(String value, Map entries) {
    Pattern pattern = Pattern.compile(".*\\$\\{(.*)\\}.*");
    Matcher matcher = pattern.matcher(value);

    String text = StringUtils.EMPTY;
    if (matcher.find()) {
      text = matcher.group(1);
    } else {
      return value;
    }

    String replacementText = System.getProperty(text);
    if (null != replacementText) {
      return stringify(StringUtils.replace((String) value, text, replacementText));
    } else {
      replacementText = (String) entries.get(text);
      if (null != replacementText) {
        return stringify(StringUtils.replace((String) value, text, replacementText));
      }
    }

    return value;
  }
}
