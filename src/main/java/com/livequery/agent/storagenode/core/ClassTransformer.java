package com.livequery.agent.storagenode.core;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * A <code>ClassTransformer</code> transforms an input class field by field based upon a map
 * provided to it as input. The logic goes something like this: for every instance field in the
 * class check to see if the data type of the field specified in the input map is the same. If the
 * data type is not same then we update the instance field of the class with the data type specified
 * in the map. On the other hand if the data types of both source and target fields are same then it
 * is a no-op.
 *
 * Furthermore, there could be instance fields specified in the input map which are not part of the
 * class and fields that are part of the class but are absent in the input map. In the former case,
 * a new field will be added to the class, whereas, in the later case the field from the class will
 * be deleted.
 */
class ClassTransformer<T> implements IClassTransformer<String, String> {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * Type to be transformed
     */
    private final Class<T> type;

    /**
     * Supported types
     */
    private final JavaSupportedTypes javaSupportedDataTypes = new JavaSupportedTypes();

    public ClassTransformer(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] transform(@NonNull Map<String, String> input) {
        Map<String, String> target = input.entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey(),
                e -> javaSupportedDataTypes.getDescriptor(e.getKey()).orElse(StringUtils.EMPTY)));

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

        byte[] bytes = null;
        try {
            ClassReader reader = new ClassReader(type.getCanonicalName());
            ClassWriter writer = new ClassWriter(0);
            ClassVisitor visitor = new AppClassVisitor(Opcodes.V1_8, writer);
            reader.accept(visitor, 0);
            bytes = writer.toByteArray();
        } catch (IOException e) {
            logger.error(String.format("Unable to load class {%s} for parsing", type.getCanonicalName()));
        } catch (Exception e) {
            logger.error(String
                .format("Exception encountered while trying to transform class {%s}. Exception {%s}",
                    type.getCanonicalName(), e));
        }

        return bytes;
    }
}

