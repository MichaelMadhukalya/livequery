package com.livequery.agent.filesystem.core;

import com.livequery.agent.filesystem.core.FileEvent.FileEventType;
import com.livequery.agent.storagenode.core.CodecMapper;
import com.livequery.common.AbstractNode;
import com.livequery.common.Environment;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileChangeConsumer<T extends FileEvent> extends AbstractNode implements IFileChangeConsumer {
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
    
    /* Cyclic barrier for synchronization across threads */
    private static final int NUM_OF_THREADS = 2;
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_OF_THREADS, this::post);
    
    /**
     * Executor service
     */
    private ExecutorService service = Executors.newSingleThreadExecutor();
    
    private volatile long updateCount = 0;
    private static final int MAX_SLEEP_TIME_MILLISECONDS = 5000;
    
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
        this(null);
    }
    
    public FileChangeConsumer(String fileName) {
        logger.info(String.format("Initializing file change consumer with watched dir path"));
        
        if (StringUtils.isEmpty(fileName)) {
            Environment environment = new Environment();
            
            CodecMapper codecMapper = new CodecMapper(environment.getCodecFilePath());
            String dsn = (String) codecMapper.getCodecMapper().get("DataSourceName");
            logger.info(String.format("DataSourceName (DSN) from inside Codec Mapper : {%s}", dsn));
            
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
                new FileChangeProcessor(dataSourceName, StringUtils.EMPTY, events -> consumeBatch(events), cyclicBarrier);
            
            /* Start observing watched dir for changes */
            service.submit(fileChangeProcessor);
            
            /* Start observing for file change events */
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
        
        if (modifyCount > 0L) {
            logger.info(String.format("Update events have been detected : %s", modifyCount));
            updateCount = modifyCount;
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
        
        try {
            while (true) {
                if (updateCount == 0L) {
                    Thread.sleep(MAX_SLEEP_TIME_MILLISECONDS);
                } else {
                    /* Read serialized records from file*/
                    updateCount = 0L;
                }
            }
        } catch (Exception e) {
            logger.warn(String.format("Exception while polling file for changes : %s", e));
        }
    }
}
