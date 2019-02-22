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
 * <p>
 * A class loader implementation for reloading <code>Schema</code> class in accordance with
 * schmeatization provided as per user defined <code>Codec</code> file. Note that for the app class
 * loader to work correctly the byte codes in .class file for the class has to be present in class
 * path. The class path link for the application can be found in <code>.properties</code> file.
 *
 * <p>
 * The class loader performs the following actions in order while loading a new class:
 * <ul>
 * <li>
 * First, check if the requested class has already been loaded. If the requested class is found in
 * local cache then return it to client.
 * </li>
 * <li>
 * Second, if the class is not yet loaded then start the process of loading the class. This involves
 * ensuring that the class is not part of a <code>System</code> class or does not have any
 * restrictions associated with it e.g. canonical name starts with "java." etc.
 * </li>
 * <li>
 * Load byte code for the class, define the class and resolve it. As part of defining the class all
 * JVM machinery for subsequent loading of the class is set.
 * </li>
 * <li>
 * Finally, cache the class for future loading in a local hash map and return the cached reference.
 * </li>
 * </ul>
 */
public class AppClassLoader extends ClassLoader {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * A cache of class name and class object. If a class name is found in cache the class is
     * resolved using this cache.
     */
    private final Map<String, Class<?>> cache = new HashMap<>();

    /**
     * Environment
     */
    private final Environment environment = new Environment();

    private final String className;

    public AppClassLoader(String className) {
        this.className = className;
    }

    @Override
    public Class findClass(String className) throws ClassNotFoundException {
        /* During class resolution, all classes that are dependencies of this class will be resolved
         * and findClass called for each of those classes. Need to ensure that for such dependent
         * classes we find the super findClass method rather than proceeding ahead with loading the
         * bytes for the class from .class file.
         */
        if (!StringUtils.equals(this.className, className)) {
            return super.findClass(className);
        }

        logger.info(String.format("Reloading class {%s} using App Class Loader", className));

        Class result;

        /* Check to see if the class has already been loaded by this class loader. If that is the case
         * then return a cached copy.
         */
        result = cache.get(className);
        if (result != null) {
            logger
                .info(String.format("Class %s already loaded. Returning cached instance", result));
            return result;
        }

        /* Verify that class is not a system class. Throws ClassNotFoundException in case it is */
        super.findSystemClass(className);

        /* Verify that class is not part of protected domain i.e. canonical name does not start with
         * the words "java" e.g. "java.lang.String"
         */
        if (isProtected(className)) {
            logger.error(String.format("Unable to load {%s} since it is part of protected domain"));
            return result;
        }

        /* Load bytes for class (sync) */
        byte[] data = loadBytes(className);
        if (data == null || data.length == 0) {
            logger.error(String.format("Unable to load bytes for %s from class path", className));
            return result;
        }

        /* Define the class */
        result = defineClass(className, data, 0, data.length);
        if (result == null) {
            logger.error(String.format("Unable to define %s using app class loader", className));
            return result;
        }

        /* Cache for future reference */
        cache.put(className, result);
        return result;
    }

    private boolean isProtected(String className) {
        return StringUtils.startsWithIgnoreCase(className, "java");
    }

    private byte[] loadBytes(String className) {
        byte[] data = null;

        try {
            String fileName = StringUtils.join(
                new String[]{
                    StringUtils.replaceChars(Class.forName(className).getCanonicalName(),
                        '.', '/'),
                    "class"}, ".");
            Path path = Paths.get(environment.getClassPath(), fileName);
            data = Files.readAllBytes(path);
            logger.info(String.format("%d bytes read from file %s", data.length, path.toString()));
        } catch (ClassNotFoundException e) {
            logger
                .error(String.format("Exception while attempting to read bytes for class %s: {%s}",
                    className, e));
        } catch (IOException e) {
            logger
                .error(String.format("Exception while attempting to read bytes for class %s: {%s}",
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
