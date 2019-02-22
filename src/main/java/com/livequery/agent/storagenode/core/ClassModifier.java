package com.livequery.agent.storagenode.core;

import com.livequery.common.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * The purpose of <code>ClassModifier</code> is to overwrite byte codes for a given input class on
 * disk with updated fields/methods/annotations. Once the byte codes are overwritten, the app class
 * loader can re-load the class again with updated fields/methods.
 * </p>
 *
 * <p>
 * TODO: implement method to update byte codes for a class on disk asynchronously.
 */
public class ClassModifier {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());
    /**
     * Class type that needs to be re-written to .class file stored as byte code on disk
     */
    private final Class<?> type;

    /**
     * Environment
     */
    private final Environment environment = new Environment();

    /**
     * Location of the class file
     */
    private Path location;

    public ClassModifier(Class<?> type) {
        this.type = type;
    }

    public boolean write(byte[] data) {
        Path path = findClass(environment.getClassPath());
        if (null == path || StringUtils.isEmpty(path.toString())) {
            logger.error(
                String.format("Unable to locate class %s inside class path", type.getSimpleName()));
            return false;
        }

        boolean success = false;
        try (FileOutputStream fileOutputStream = new FileOutputStream(
            new File(path.toAbsolutePath().toString()), false)) {
            fileOutputStream.write(data);
            success = true;
            logger.info(String
                .format("Successfully updated file : {%s}", path.toAbsolutePath().toString()));
        } catch (Exception e) {
            logger.error(String.format("Exception encountered while updating file: {%s}", e));
        }

        return success;
    }

    /**
     * TODO: implement async update of .class files with contents of byte array
     */
    public boolean writeAsync(byte[] data) {
        return false;
    }

    /**
     * TODO: implement a byte code verifier to verify contents of byte array
     */
    private boolean verify(byte[] data) {
        return false;
    }

    /**
     * Finds the location of a class file by searching inside class path. The class path is provided
     * as a property inside <code>.properties</code> file.
     *
     * @param classpath Class path
     * @return Location of the class file
     */
    private Path findClass(String classpath) {
        logger.info(String.format("Searching for class inside class path : {%s}", classpath));

        try {
            Files.walkFileTree(Paths.get(classpath), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path,
                    BasicFileAttributes basicFileAttributes)
                    throws IOException {
                    if (null != ClassModifier.this.location) {
                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes)
                    throws IOException {
                    String classFile = type.getSimpleName();

                    if (path.toString().contains(classFile)) {
                        location = path;
                        logger.info(
                            String.format("Class {%s} found inside classpath with file name {%s}",
                                ClassModifier.this.type.getSimpleName(), path.toString()));
                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path path, IOException e)
                    throws IOException {
                    if (null != e) {
                        logger.error(String
                            .format("File visit failed %s with exception {%s}", path.toString(),
                                e));
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path path, IOException e)
                    throws IOException {
                    if (null != e) {
                        logger.error(
                            String.format("Directory visit failed %s with exception {%s}",
                                path.toString(), e));
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error(String
                .format("Exception while trying to search for class %s in classpath {%s}",
                    type.getSimpleName(), e));
        }

        return location;
    }
}
