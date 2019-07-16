package com.livequery.agent.filesystem.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Logger;

public class FileChangeProcessor<E extends FileEvent> implements IFileChangeProcessor {
    
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
     * File change processor observer
     */
    private final Function<E[], Void> observer;
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
    
    public FileChangeProcessor(String filename, String groupName, Function<E[], Void> fileChangeObserver) {
        FileChangeProcessor.filename = filename;
        this.groupName = groupName;
        this.observer = fileChangeObserver;
    }
    
    public void start() {
        service.submit(this::process);
    }
    
    private String getFilename() {
        return filename;
    }
    
    private void process() {
        while (true) {
            try {
                if (full()) {
                    isFull.await();
                }
                
                /* Start polling */
                dpoll();
            } catch (Exception e) {
                logger.info(String.format("Exception while trying to put file changes : {%s}", e));
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
    
    private void produce(List<E> data) {
        int i = 0;
        
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
    
    private void consume() {
        List<E> data = new ArrayList<>();
        
        int i = 0;
        while (!empty() && i < BATCH_SIZE) {
            int pos = low.getAndIncrement();
            data.add((E) events[pos]);
            i++;
            ic--;
            
            /* Reset pointer if required */
            if (low.get() == MAX_SIZE) {
                low.set(low.get() % MAX_SIZE);
            }
        }
        
        observer.apply((E[]) data.toArray());
    }
}
