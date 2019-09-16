package com.livequery.agent.filesystem.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

public class FileChangeProcessor<FileEvent> implements IFileChangeProcessor, Runnable {
    
    /**
     * Change processor queue max size
     */
    private static final int MAX_BUFFER_SIZE = 4096;
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(FileChangeProcessor.class.getName());
    private final Object[] events = new Object[MAX_BUFFER_SIZE];
    /**
     * Filename and consumer group name
     */
    private static String filename;
    private final String groupName;
    /**
     * Lock settings for managing change processor queue reads and writes via wait/notify
     */
    private final Lock lock = new ReentrantLock();
    private final Condition isEmpty = lock.newCondition();
    private final Condition isFull = lock.newCondition();
    /**
     * File change processor consumer
     */
    private final Function<Object[], Void> consumer;
    /**
     * Batch size of items processed
     */
    private final int BATCH_SIZE = 50;
    /**
     * Atomic counters over queue
     */
    private AtomicInteger low = new AtomicInteger(0), high = new AtomicInteger(0);
    /**
     * Item count
     */
    private int itemCount = 0;
    
    /**
     * Processor service pool
     */
    private static final int NUMBER_OF_THREADS = 2;
    private ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    /**
     * Synchronize between caller thread and the producer and consumer
     */
    private final CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS + 1);
    
    /**
     * Cyclic barrier for synchronization with invoking (parent) thread
     */
    private final CyclicBarrier cyclicBarrier;
    
    public FileChangeProcessor(String fileName, String groupName, Function<Object[], Void> consumer, CyclicBarrier cyclicBarrier) {
        FileChangeProcessor.filename = fileName;
        this.groupName = groupName;
        this.consumer = consumer;
        this.cyclicBarrier = cyclicBarrier;
    }
    
    @Override
    public void run() {
        service.submit(this::process);
        service.submit(this::poll);
        
        try {
            /* Await completion */
            latch.countDown();
            latch.await();
            
            /* Synchronize with parent through barrier synchronization */
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            logger.error(String.format("Exception while awaiting task completion : {%s}", e));
        } catch (BrokenBarrierException e) {
            logger.error(String.format("Broken barrier exception encountered : {%s}", e));
        }
    }
    
    private String getFileName() {
        return filename;
    }
    
    private void process() {
        boolean isError = false;
        logger.debug(String.format("Processing %s dir to watch for change events", filename));
        
        while (true) {
            try {
                /* Start processing dir for changed events */
                logger.debug(String.format("Start processing watched dir for changed events"));
                dpoll();
                logger.debug(String.format("End processing watched dir for changed events"));
            } catch (Exception e) {
                logger.warn(String.format("Exception while trying to add file changes : {%s}", e));
                isError = true;
            } finally {
                /* Check error */
                if (isError) {
                    latch.countDown();
                    return;
                }
            }
            
            /* Signal consumer */
            isEmpty.signal();
            logger.debug(String.format("Signal to consumer awaiting on changed events"));
        }
    }
    
    /**
     * A native method (JNI) for polling a directory for change events
     */
    private native void dpoll();
    
    @Override
    public void poll() {
        boolean isError = false;
        logger.debug(String.format("Starting to poll %s directory for change events", filename));
        
        while (true) {
            try {
                if (empty()) {
                    /* Wait if queue is empty */
                    logger.debug(String.format("Waiting until changed events are available"));
                    isEmpty.await();
                }
                
                logger.debug(String.format("Events are available for watched dir, start consuming..."));
                consume();
                
                /* Signal producer */
                isFull.signal();
                logger.debug(String.format("Producer notified to start watching for change events"));
            } catch (Exception e) {
                isError = true;
                logger.info(String.format("Exception while polling for file changes : {%s}", e));
            } finally {
                /* Exit if error */
                if (isError) {
                    latch.countDown();
                    break;
                }
            }
        }
    }
    
    @Override
    public boolean tryPoll(int time, TimeUnit timeUnit) {
        return false;
    }
    
    private boolean empty() {
        return low.get() == high.get() && itemCount == 0;
    }
    
    private boolean full() {
        return low.get() == high.get() && itemCount == MAX_BUFFER_SIZE;
    }
    
    public void produce(Object[] vals) {
        List<?> data = Arrays.stream(vals).collect(Collectors.toList());
        logger.info(String.format("Number of change events received from native code : {%d}", data == null ? 0 : data.size()));
        
        for (int i = 0; !full() && i < data.size(); i++) {
            try {
                if (full()) {
                    /** Await while input buffer is full */
                    logger.debug(String.format("Awaiting on full input buffer. Capacity : {%s}", MAX_BUFFER_SIZE));
                    isFull.await();
                }
            } catch (Exception e) {
                logger.info(String.format("Exception while awaiting on a full buffer : {%s}", e));
            }
            
            int pos = high.getAndIncrement();
            events[pos] = data.get(i);
            itemCount++;
            
            if (high.get() == MAX_BUFFER_SIZE) {
                high.set(high.get() % MAX_BUFFER_SIZE);
            }
        }
    }
    
    public void consume() {
        List<FileEvent> data = new ArrayList<>();
        
        for (int i = 0; i < BATCH_SIZE; i++) {
            /* Return on empty buffer */
            if (empty()) {
                return;
            }
            
            int pos = low.getAndIncrement();
            data.add((FileEvent) events[pos]);
            itemCount--;
            
            /* Reset pointer if required */
            if (low.get() == MAX_BUFFER_SIZE) {
                low.set(low.get() % MAX_BUFFER_SIZE);
            }
        }
        
        if (data.size() > 0) {
            consumer.apply((FileEvent[]) data.toArray());
        }
    }
}
