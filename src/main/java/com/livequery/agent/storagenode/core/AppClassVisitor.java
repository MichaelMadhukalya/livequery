package com.livequery.agent.storagenode.core;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_8;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

/**
 * <p>
 * The main purpose of <code>AppClassVisitor</code> is to produce transformed <tt>byte code</tt> for
 * a input class <code>Schema</code> based on input provided using a <code>Codec</code> file. Four
 * different scenarios are possible in this situation:
 * </p>
 *
 * <p>
 * <ul>
 * <li>An instance field present in input <code>Schema</code> class is also present in the
 * <code>Codec</code> file with the same data type. No further action is required.</li>
 * <li>An instance field present in input <code>Schema</code> class is also present in the
 * <code>Codec</code> file but with a different data type. The data type of the instance field of
 * the input <code>Schema</code> class is updated.</li>
 * <li>An instance field present in <code>Schema</code> class is not part of <code>Codec</code>
 * file. The instance field from the input <code>Schema</code> class is deleted.</li>
 * <li>A new field is present in the input <code>Codec</code> file which is not part of the
 * <code>Schema</code> class. A new field is inserted into the <code>Schema</code> class with data
 * type specified as per the <code>Codec</code> file.</li>
 * </ul>
 * </p>
 */
class AppClassVisitor extends ClassVisitor {

    /**
     * Logger
     */
    private final Logger logger = LogManager.getLogger(getClass().getCanonicalName());

    /**
     * Field mappings with field name as keys and descriptors as values
     */
    private final Map<? extends String, ? extends String> fieldDescriptorMappings;

    /**
     * Indicates whether field transformation should be applied or not
     */
    private boolean transform = false;

    /**
     * Number of fields visited
     */
    private int fieldsVisited = 0;

    public AppClassVisitor(int api, ClassVisitor visitor,
        @NonNull Map<String, String> fieldDescriptorMappings) {
        super(api, visitor);
        this.fieldDescriptorMappings = Maps.newHashMap(fieldDescriptorMappings);
        this.transform = this.fieldDescriptorMappings.size() > 0;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
        String[] interfaces) {
        cv.visit(V1_8, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature,
        Object value) {
        ++fieldsVisited;

        /* No transformation */
        if (!transform) {
            return cv.visitField(access, name, desc, signature, value);
        }

        if (!fieldDescriptorMappings.containsKey(name)) {
            /* The field is no longer present in Codec */
            return null;
        }

        /*
         * If the field exists in both Schema object as well as the Codec file then two cases are
         * possible: either the field has a different type than the one specified in the Schema class,
         * or the field has not been changed i.e. updated in the Codec file. In the first case, we
         * update the field with the descriptor specified in the Codec file and the in the second case,
         * we visit the field as it is. In both cases we remove the field from fieldDescriptorMappings.
         */
        String descriptor = (String) fieldDescriptorMappings.get(name);
        fieldDescriptorMappings.remove(name);
        if (StringUtils.equalsIgnoreCase(descriptor, desc)) {
            return cv.visitField(access, name, desc, signature, value);
        }

        return cv.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        /* Add new fields present in the Codec but not present in current Schema class */
        addFieldMappings();
        cv.visitEnd();
    }

    /**
     * Add new fields that have been specified in fieldDescriptorMappings.
     * <p>
     * TODO: Add logic to add annotations when a field is visited if there are annotations present
     * for the field
     */
    private void addFieldMappings() {
        fieldDescriptorMappings.entrySet().stream()
            .forEach(e -> {
                ++fieldsVisited;
                String name = e.getKey();
                String descriptor = e.getValue();
                FieldVisitor fieldVisitor = cv.visitField(ACC_PUBLIC, name, descriptor, null, null);
                fieldVisitor.visitEnd();
            });

        logger.info(String
            .format("Number of fields visited including new fields in Codec: {%d}", fieldsVisited));
        fieldDescriptorMappings.clear();
    }
}
