package com.livequery.agent.storagenode.core;

import com.livequery.common.Environment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * A simple app class loader implementation based upon the following article:
 * <pre>
 *   https://www.javaworld.com/article/2077260/learn-java/learn-java-the-basics-of-java-class-loaders.html
 * </pre>
 *
 * The class loader performs the following actions in order while loading a new class:
 * <ul>
 * <li>
 * First, check if the requested class has already been loaded. If the requested class is found in
 * local cache then return it to client.
 * </li>
 * <li>
 * Second, if the class is not yet loaded then start the process of loading the class. This involves
 * ensuring that the class is not part of a <pre>System</pre> class or does not have any
 * restrictions associated with it e.g. its canonical name starts with "java." etc.
 * </li>
 * <li>
 * Load byte code for the class, define the class and resolve it. Defining the class means that all
 * JVM machinery for loading the class in subsequent invocations will be set.
 * </li>
 * <li>
 * Finally, cache the class for future loading in a local hash map and return the cached reference.
 * </li>
 * </ul>
 */
class AppClassLoader extends ClassLoader {

  /**
   * Logger
   */
  private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

  /**
   * A cache of class name and class object. If a class name is found in cache the class is resolved
   * using this cache.
   */
  private final Map<String, Class<?>> cache = new HashMap<>();

  /**
   * Environment
   */
  private final Environment environment = new Environment();

  public AppClassLoader() {
  }

  @Override
  public Class loadClass(String className, boolean resolveIt) {
    Class<?> result;

    /* Check to see if the class has already been loaded by this class loader. If that is the case
     * then return a cached copy.
     */
    result = cache.get(className);
    if (result != null) {
      logger.info(
          String.format("Class %s has already been loaded. Returning the same class", result));
      return result;
    }

    /* Check if requested class is a system class. System classes are not resolved */
    if (isSystemClass(className)) {
      logger.warn(String
          .format(
              "Class %s is marked as a system class. Will not be resolved by app class loader",
              className));
      return result;
    }

    /* Check if class is protected */
    if (isProtected(className)) {
      logger.warn(String
          .format("Class %s has been deemed protected. Will not be resolved by app class loader",
              className));
      return result;
    }

    /* Load bytes for class (sync) */
    byte[] data = loadBytes(className);
    if (data == null || data.length == 0) {
      logger.error(String.format("Unable to load bytes for class %s from class path", className));
      return result;
    }

    /* Define the class */
    result = defineClass(className, data, 0, data.length);
    if (result == null) {
      logger.error(String.format("Unable to define class %s using app class loader", className));
      return result;
    }

    /* Resolves the class */
    if (resolveIt) {
      try {
        resolveClass(result);
      } catch (LinkageError e) {
        logger.error(String.format("Exception encountered while trying to resolve class %s", e));
      } catch (Exception e) {
        logger.error(String.format("Exception encountered while trying to resolve class %s", e));
      }
    }

    /* Cache for future reference */
    cache.put(className, result);
    return result;
  }

  private boolean isSystemClass(String className) {
    try {
      Class<?> result = super.findSystemClass(className);
      return true;
    } catch (ClassNotFoundException e) {
      /* An exception means that the class requested is not a system class */
      logger.info(String.format("%s is not a system class", className));
    }

    return false;
  }

  private boolean isProtected(String className) {
    return StringUtils.startsWithIgnoreCase(className, "java");
  }

  private byte[] loadBytes(String className) {
    byte[] data = null;
    try {
      Path path = Paths.get(environment.getClassPath(), new String[]{
          StringUtils.replaceChars(Class.forName(className).getCanonicalName(),
              '.',
              '/')});
      data = Files.readAllBytes(path);
      logger.info(String.format("%d bytes read from class file %s", data.length, path.toString()));
    } catch (ClassNotFoundException e) {
      logger.error(String
          .format("Exception encountered while attempting to read bytes for class %s: {%s}",
              className, e));
    } catch (IOException e) {
      logger.error(String
          .format("Exception encountered while attempting to read bytes for class %s: {%s}",
              className, e));
    }

    return data;
  }

  /**
   * TODO: implement async loading of bytes from .class file into a byte array
   */
  private byte[] loadBytesAsync(String className) {
    return null;
  }
}
