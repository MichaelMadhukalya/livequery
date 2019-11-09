package com.livequery.agent.filesystem.core;

public interface IFileChangeProcessor<E> {
    
    /**
     * Default batch size in number of messages consumed
     */
    final int DEFAULT_BATCH_SIZE = 50;
    
    /**
     * Default batch size in KB
     */
    final int DEFAULT_BATCH_SIZE_KB = 1000;
    
    /**
     * Maximum number of clients that consumer supports
     */
    final int MAX_NUM_CLIENTS = 5;
    
    void consume(E fileEvent);
    
    void consumeBatch(E[] fileEvents);
}
