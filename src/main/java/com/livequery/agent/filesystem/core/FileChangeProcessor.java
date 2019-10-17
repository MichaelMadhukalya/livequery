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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

public class FileChangeProcessor<FileEvent> implements IFileChangeProcessor, Runnable {
    
    /**
     * Change processor queue max size
     */
    private static final int CAPACITY = 4096;
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(FileChangeProcessor.class.getName());
    private final Object[] events = new Object[CAPACITY];
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
    private final Consumer<Object[]> consumer;
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
    
    public FileChangeProcessor(String fileName, String groupName, Consumer<Object[]> consumer, CyclicBarrier cyclicBarrier) {
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
        logger.debug(String.format("Processing %s dir to watch for changed events", filename));
        
        while (true) {
            lock.lock();
            
            try {
                /* Start processing dir for changed events */
                logger.debug(String.format("Start processing watched dir for changed events"));
                dpoll();
                logger.debug(String.format("End processing watched dir for changed events"));
                
                /* Signal consumer */
                isEmpty.signal();
                logger.debug(String.format("Consumer notified awaiting on changed events"));
                isFull.await();
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn(String.format("Exception while trying to add file changes : %s", e));
                isError = true;
            } finally {
                /* Check error */
                if (isError) {
                    latch.countDown();
                    lock.unlock();
                    break;
                }
            }
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
            lock.lock();
            
            try {
                if (empty()) {
                    /* Wait if queue is empty after signalling producer */
                    isFull.signal();
                    logger.debug(String.format("Waiting until changed events are available"));
                    isEmpty.await();
                    continue;
                }
                
                logger.debug(String.format("Events are available for watched dir, consuming..."));
                consume();
                
                /* Signal producer */
                isFull.signal();
                logger.debug(String.format("Producer notified to start watching for changed events"));
                isEmpty.await();
            } catch (Exception e) {
                isError = true;
                logger.info(String.format("Exception while polling for file changes : %s", e));
            } finally {
                /* Exit if error */
                if (isError) {
                    latch.countDown();
                    lock.unlock();
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
        return low.get() == high.get() && itemCount == CAPACITY;
    }
    
    public void produce(Object[] vals) {
        List<?> data = Arrays.stream(vals).collect(Collectors.toList());
        logger.debug(String.format("Number of change events received : {%d}", data == null ? 0 : data.size()));
        
        for (int i = 0; i < data.size(); ) {
            try {
                if (full()) {
                    /* Await while input buffer is full */
                    logger.debug(String.format("Awaiting on full input buffer. Capacity : {%s}", CAPACITY));
                    isEmpty.signal();
                    isFull.await();
                    lock.lock();
                    continue;
                }
            } catch (Exception e) {
                logger.info(String.format("Exception while awaiting on a full buffer : {%s}", e));
            }
            
            int pos = high.getAndIncrement();
            events[pos] = data.get(i++);
            itemCount++;
            
            if (high.get() == CAPACITY) {
                high.set(high.get() % CAPACITY);
            }
        }
        
        logger.debug(String.format("Items in events queue : %d. Low = %d, high = %d", itemCount, low.get(), high.get()));
    }
    
    public void consume() {
        int consumed = 0;
        List<FileEvent> cache = new ArrayList<>();
        
        for (int i = 0; i < BATCH_SIZE; ) {
            try {
                if (empty()) {
                    /* Await while input buffer is empty */
                    logger.debug(String.format("No messages in event queue.Low = %d, high = %d", low.get(), high.get()));
                    isFull.signal();
                    isEmpty.await();
                    lock.lock();
                    continue;
                }
            } catch (Exception e) {
                logger.info(String.format("Exception while awaiting on a empty buffer : {%s}", e));
            }
            
            int pos = low.getAndIncrement();
            cache.add((FileEvent) events[pos]);
            itemCount--;
            consumed++;
            i++;
            
            /* Reset pointer if required */
            if (low.get() == CAPACITY) {
                low.set(low.get() % CAPACITY);
            }
        }
        
        if (cache.size() > 0) {
            consumer.accept((FileEvent[]) cache.toArray());
        }
        
        logger.debug(String.format("Items consumed %d and remaining in events queue %d", consumed, itemCount));
    }
}
