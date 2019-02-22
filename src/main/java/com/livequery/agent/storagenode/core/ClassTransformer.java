package com.livequery.agent.storagenode.core;

import static org.objectweb.asm.Opcodes.ASM4;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * <p>
 * A <code>ClassTransformer</code> transforms an input class field by field based upon a map
 * provided to it as input. The logic goes something like this: for every instance field in the
 * class check to see if the data type of the field specified in the input map is the same. If the
 * data type is not same then we update the instance field of the class with the data type specified
 * in the map. On the other hand if the data types of both source and target fields are same then it
 * is a no-op.
 *
 * <p>
 * Furthermore, there could be instance fields specified in the input map which are not part of the
 * class and fields that are part of the class but are absent in the input map. In the former case,
 * a new field will be added to the class, whereas, in the later case the field from the class will
 * be deleted.
 */
public class ClassTransformer implements IClassTransformer<String, Object> {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * Type to be transformed
     */
    private final Class<?> type;

    /**
     * Supported types
     */
    private final JavaSupportedTypes javaSupportedDataTypes = new JavaSupportedTypes();

    public ClassTransformer(Class<?> type) {
        this.type = type;
    }

    @Override
    public byte[] transform(@NonNull Map<String, Object> input) {
        Map<String, String> target = input.entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey(),
                e -> javaSupportedDataTypes.getDescriptor((String) e.getValue())
                    .orElse(StringUtils.EMPTY)));

        /* Ensure that data types have correct Java supported types */
        long invalidTypeCount = target.entrySet().stream()
            .filter(e -> e.getValue().equals(StringUtils.EMPTY))
            .count();
        if (invalidTypeCount > 0) {
            logger.error(String
                .format("%d unsupported data type found in input, transformation won't be correct",
                    invalidTypeCount));
            throw new IllegalStateException(
                "Unsupported data type found in input, transformation won't be correct");
        }

        byte[] data = null;
        try {
            ClassReader reader = new ClassReader(type.getCanonicalName());
            ClassWriter writer = new ClassWriter(0);
            logger.info(String
                .format("Initialized reader and writer class for byte transforming : {%s}",
                    type.getCanonicalName()));

            ClassVisitor visitor = new AppClassVisitor(ASM4, writer, target);
            reader.accept(visitor, 0);
            data = writer.toByteArray();
            logger.info(String.format("Byte code array created with length: {%d}", data.length));
        } catch (IOException e) {
            logger.error(
                String.format("Unable to load class {%s} for parsing", type.getCanonicalName()));
        } catch (Exception e) {
            logger.error(String
                .format(
                    "Exception encountered while trying to transform class {%s}. Exception {%s}",
                    type.getCanonicalName(), e));
        }

        return data;
    }
}

