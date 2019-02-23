package com.livequery.common;

/**
 * An <code>INode</code> provides an interface for a service that can be started and terminated.
 * Every sub-system of <emp>livequery</emp> e.g. file system, storage node or query processor starts
 * as a service and provides semantics for starting/terminating the service. The service will decide
 * what to implement as part of start/termination logic and it is up to the calling client to decide
 * as to when to call the start/terminate methods.
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
