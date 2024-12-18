package com.livequery.agent.storagenode.core;

/**
 * Interface for parsing file codec. Clients are expected to provide specific implementation.
 */
public interface ICodecParser<K, V> {

    /**
     * @return The status while extracting the next token key/value pair from code file
     */
    int next();

    /**
     * @return Extracted key from codec file
     */
    K getKey();

    /**
     * @return Extracted value from codec file
     */
    V getValue();
}
