package com.livequery.agent.filesystem.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static final int MAX_SIZE = 4096;
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(FileChangeProcessor.class.getName());
    private final Object[] events = new Object[MAX_SIZE];
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
    private int ic = 0;
    
    /**
     * Processor service pool
     */
    private ExecutorService service = Executors.newSingleThreadScheduledExecutor();
    
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
    }
    
    private String getFileName() {
        return filename;
    }
    
    private void process() {
        boolean isError = false;
        logger.debug(String.format("Polling %s dir to watch for change events", filename));
        
        while (true) {
            try {
                if (full()) {
                    isFull.await();
                }
                
                /* Start polling */
                logger.debug(String.format("Start polling watched dir for changed events"));
                dpoll();
                logger.debug(String.format("End polling watched dir for changed events"));
            } catch (Exception e) {
                logger.warn(String.format("Exception while trying to add file changes : {%s}", e));
                isError = true;
            } finally {
                /* Check for error in adding polling info */
                if (isError) {
                    try {
                        cyclicBarrier.await();
                    } catch (Exception e) {
                        logger.error(String.format("Exception encountered while awaiting on barrier : {%s}", e));
                    }
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
        while (true) {
            try {
                if (empty()) {
                    isEmpty.await();
                }
                consume();
                isFull.signal();
            } catch (Exception e) {
                logger.info(String.format("Exception while polling for file changes : {%s}", e));
            }
        }
    }
    
    @Override
    public boolean tryPoll(int time, TimeUnit timeUnit) {
        return false;
    }
    
    private boolean empty() {
        return low.get() == high.get() && ic == 0;
    }
    
    private boolean full() {
        return low.get() == high.get() && ic == MAX_SIZE;
    }
    
    public void produce(Object[] vals) {
        int i = 0;
        
        List<?> data = Arrays.stream(vals).collect(Collectors.toList());
        logger.info(String.format("Number of change events received from native code : {%d}", data == null ? 0 : data.size()));
        
        while (true) {
            if (i == data.size()) {
                break;
            }
            
            try {
                if (full()) {
                    isFull.await();
                }
            } catch (Exception e) {
                logger.info(String.format("Exception while await on a full buffer : {%s}", e));
            }
            
            for (; !full() && i < data.size(); i++) {
                int pos = high.getAndIncrement();
                events[pos] = data.get(i);
                ic++;
                
                if (high.get() == MAX_SIZE) {
                    high.set(high.get() % MAX_SIZE);
                }
            }
            
            /* Signal to consumer */
            isEmpty.signal();
        }
    }
    
    public void consume() {
        List<FileEvent> data = new ArrayList<>();
        
        int i = 0;
        while (!empty() && i < BATCH_SIZE) {
            int pos = low.getAndIncrement();
            data.add((FileEvent) events[pos]);
            i++;
            ic--;
            
            /* Reset pointer if required */
            if (low.get() == MAX_SIZE) {
                low.set(low.get() % MAX_SIZE);
            }
        }
        
        consumer.apply((FileEvent[]) data.toArray());
    }
}
