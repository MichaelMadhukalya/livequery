package com.livequery.agent.filesystem.core;

import com.livequery.agent.filesystem.core.FileEvent.FileEventType;
import com.livequery.agent.storagenode.core.CodecMapper;
import com.livequery.common.AbstractNode;
import com.livequery.common.Environment;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileChangeConsumer<T extends FileEvent> extends AbstractNode implements IFileChangeProcessor {
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());
    
    /**
     * Load native library
     */
    private static final String NATIVE_LIB_NAME = "poll";
    
    /** Load native library */
    static {
        System.loadLibrary(NATIVE_LIB_NAME);
    }
    
    /**
     * Data source name
     */
    private final String dataSourceName;
    
    /**
     * File change processor
     */
    private FileChangeProcessor fileChangeProcessor;
    
    /* Cyclic barrier for synchronization across threads, barrier needs one more for the main thread */
    private static final int NUM_OF_THREADS = 2;
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_OF_THREADS + 1, this::post);
    
    /**
     * Executor service
     */
    private ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
    
    private volatile boolean modified = false;
    private static final int MAX_SLEEP_TIME_MILLISECONDS = 5000;
    private StructReader<String> reader;
    
    /**
     * Codec mapper
     */
    private final CodecMapper codecMapper;
    
    @Override
    protected void pre() {
    }
    
    @Override
    protected void post() {
        /* Initiate shutdown on termination */
        try {
            logger.info(String.format("Initiating shutdown of executor service"));
            
            if (!service.isShutdown()) {
                /* initiate shutdown */
                service.shutdown();
                boolean shutDown = service.awaitTermination(60, TimeUnit.SECONDS);
                
                if (!shutDown) {
                    logger.warn(String.format("Executor service wasn't shutdown. Re-attempting."));
                    
                    /* Shut down now */
                    List<?> tasks = service.shutdownNow();
                    logger.warn(String.format("Found %d tasks waiting when shutdown was initiated",
                        tasks != null ? tasks.size() : 0));
                }
            }
        } catch (InterruptedException e) {
            logger.error(String.format("Exception shuting down service for Http requests:{%s}", e));
        } finally {
            if (!service.isTerminated()) {
                logger.warn("Not all tasks completed successfully post shutdown of Http processor");
            } else {
                logger.info("Executor service has been shut down successfully");
            }
        }
    }
    
    public FileChangeConsumer() {
        this(null, null);
    }
    
    public FileChangeConsumer(String fileName, CodecMapper mapper) {
        logger.info(String.format("Initializing file change consumer with watched dir path"));
        
        Environment environment = new Environment();
        this.codecMapper = new CodecMapper(environment.getCodecFilePath());
        String dsn = (String) this.codecMapper.getCodecMapper().get("DataSourceName");
        this.reader = new StructReader<>(dsn);
        logger.info(String.format("DataSourceName (DSN) from inside Codec Mapper : {%s}", dsn));
        
        /* Get watched directory for listening to file change events */
        if (StringUtils.isEmpty(fileName)) {
            this.dataSourceName = getWatchedDir(dsn);
        } else {
            this.dataSourceName = getWatchedDir(fileName);
        }
        logger.info(String.format("Watched directory name : {%s}", this.dataSourceName));
    }
    
    @Override
    public void run() {
        try {
            /* Create watched directory observer */
            fileChangeProcessor =
                new FileChangeProcessor(dataSourceName, StringUtils.EMPTY, this::consumeBatch, cyclicBarrier);
            
            /* Start observing watched dir for changes */
            service.submit(fileChangeProcessor);
            
            /* Start observing for file change events, gather and publish events */
            service.submit(this::poll);
            
            /* Barrier await */
            cyclicBarrier.await();
        } catch (Exception e) {
            logger.error(String.format("Exception encountered while awaiting on barrier : {%s}", e));
        }
    }
    
    @Override
    public void consume(Object fileEvent) {
    }
    
    @Override
    public void consumeBatch(Object[] events) {
        long modifyCount = Arrays.asList(events).stream()
            .filter(Objects::nonNull)
            .filter(e -> StringUtils.contains(e.toString(), FileEventType.IN_MODIFY.name()))
            .count();
        
        /* Modification notifications are always sent regardless of whether modified is currently set to true or false */
        if (modifyCount > 0) {
            modified = true;
            logger.info(String.format("File update events have been detected"));
        }
    }
    
    private String getWatchedDir(String dataSourceName) {
        File file = new File(dataSourceName);
        if (file.isDirectory()) {
            return dataSourceName;
        } else {
            return getWatchedDir(file.getParent());
        }
    }
    
    private void poll() {
        logger.debug(String.format("Starting thread to poll for file changes"));
        
        while (true) {
            try {
                if (!modified) {
                    Thread.sleep(MAX_SLEEP_TIME_MILLISECONDS);
                } else {
                    /* Read serialized records from file*/
                    modified = false;
                    reader.get();
                }
            } catch (Exception e) {
                logger.warn(String.format("Exception while polling file for changes : %s", e));
            } finally {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    logger.warn(String.format("Exception while barrier await action : %s", e));
                }
            }
        }
    }
}
