package com.livequery.agent.storagenode.core;

import java.util.Map;

public interface IClassTransformer<K, V> {

  /**
   * Transform the instance fields of a given class to produce new byte codes taking a map as an
   * input parameter.
   *
   * @param map A map of instance field names along with their data types
   * @return An array of bytes representing transformed class that can be written into .class file
   */
  byte[] transform(Map<K, V> map);
}
