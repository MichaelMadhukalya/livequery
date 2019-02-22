package com.livequery.agent.storagenode.core;

/**
 * Interface to verify a single class type token.
 */
public interface IClassVerify {

    /**
     * Verify a single class type token. It is up to the individual implementation to decide on how
     * to verify the class type token.
     */
    boolean verify() throws IllegalAccessException, InstantiationException;
}
