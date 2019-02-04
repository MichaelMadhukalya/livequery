package com.livequery.common;

/**
 * An <code>INode</code> provides an interface for a service that can be started and terminated.
 * Every sub-system of <emp>livequery</emp> e.g. file system, storage node or query processor starts
 * as a service and provides semantics for starting/terminating the service. It is up to the service
 * to decide what should be implemented as part of start/terminate logic.
 */
public interface INode {

    /**
     * Starts the node/service
     */
    void start();

    /**
     * Stops the node/service
     */
    void terminate();
}
