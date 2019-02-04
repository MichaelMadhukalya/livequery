package com.livequery.agent.storagenode.core;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

class AppClassVisitor extends ClassVisitor {

  /**
   * Field mappings with field name as keys and descriptors as values
   */
  private Map<?, ?> fieldDescriptorMappings;

  /**
   * Indicates whether field transformation should be applied or not
   */
  private boolean transform = false;

  /**
   * Number of fields visited
   */
  private int fieldsVisited = 0;

  public AppClassVisitor(int api) {
    super(api);
  }

  public AppClassVisitor(int api, ClassVisitor visitor) {
    super(api, visitor);
  }

  public void setFieldDescriptorMappings(
      @NonNull Map<? extends String, ? extends Object> fieldDescriptorMappings) {
    this.fieldDescriptorMappings = ImmutableMap.copyOf(fieldDescriptorMappings);
    transform = this.fieldDescriptorMappings != null && this.fieldDescriptorMappings.size() > 0;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    super.cv.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature,
      Object value) {
    if (!transform) {
      /* No transformation */
      ++fieldsVisited;
      return cv.visitField(access, name, desc, signature, value);
    }

    if (!fieldDescriptorMappings.containsKey(name)) {
      /* The field is no longer present in Codec */
      return null;
    }

    /* If the field exists in both Schema object as well as the Codec file then two cases are
       possible: either the field has a different type than the one specified in the Schema class,
       or the field has not been changed i.e. updated in the Codec file. In the first case, we
       update the field with the descriptor specified in the Codec file and the in the second case,
       we visit the field as it is. In both cases we remove the field from fieldDescriptorMappings.
     */
    String descriptor = (String) fieldDescriptorMappings.get(name);
    fieldDescriptorMappings.remove(name);
    ++fieldsVisited;
    if (StringUtils.equals(descriptor, desc)) {
      return cv.visitField(access, name, desc, signature, value);
    }

    return cv.visitField(access, name, descriptor, signature, value);
  }

  @Override
  public void visitEnd() {
    addFieldMappings();
    cv.visitEnd();
  }

  /**
   * Add new fields that have been specified in fieldDescriptorMappings.
   *
   * TODO: Add logic to add annotations when a field is visited if there are annotations present for
   * the field
   */
  private void addFieldMappings() {
    fieldDescriptorMappings.entrySet().stream()
        .forEach(e -> {
          ++fieldsVisited;
          String name = (String) e.getKey();
          String descriptor = (String) e.getValue();
          FieldVisitor fieldVisitor = cv.visitField(ACC_PUBLIC, name, descriptor, null, null);
          fieldVisitor.visitEnd();
        });
    fieldDescriptorMappings.clear();
  }
}
