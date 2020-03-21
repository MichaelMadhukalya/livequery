package com.livequery.agent.filesystem.core;

import com.livequery.agent.filesystem.core.FileEvent.FileEventType;
import com.livequery.agent.storagenode.core.CodecMapper;
import com.livequery.common.AbstractNode;
import com.livequery.common.AppThreadFactory;
import com.livequery.common.Environment;
import com.livequery.common.IObservable;
import com.livequery.common.IObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileChangeConsumer<T extends FileEvent> extends AbstractNode implements IFileChangeConsumer, IObservable<Object> {
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
    
    /**
     * Thread pool size
     */
    private static final int POOL_SIZE = 4;
    private static final long POOL_SHUTDOWN_TIMEOUT_SEC = 5L;
    
    /**
     * Executor service
     */
    private ExecutorService service = Executors.newFixedThreadPool(POOL_SIZE, new AppThreadFactory());
    
    /**
     * Struct reader for reading application log file
     */
    private StructReader<String> reader;
    private static final long STRUCT_READER_TIMEOUT_SECS = 5L;
    
    /**
     * Codec mapper
     */
    private final CodecMapper codecMapper;
    
    /**
     * Observer
     */
    private IObserver observer;
    
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
                boolean shutDown = service.awaitTermination(POOL_SHUTDOWN_TIMEOUT_SEC, TimeUnit.SECONDS);
                
                if (!shutDown) {
                    logger.warn(String.format("Executor service wasn't shutdown. Re-attempting."));
                    
                    /* Shut down now */
                    List<?> tasks = service.shutdownNow();
                    logger.warn(String.format("Found %d tasks waiting when shutdown was initiated", tasks != null ?
                        tasks.size() : 0));
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
    
    @Override
    public void terminate() {
        post();
    }
    
    public FileChangeConsumer() {
        this(null, null);
    }
    
    public FileChangeConsumer(String fileName, CodecMapper mapper) {
        logger.info(String.format("Initializing file change consumer with watched dir path"));
        
        Environment environment = new Environment();
        this.codecMapper = new CodecMapper(environment.getCodecFilePath());
        String sourceName = (String) this.codecMapper.getCodecMapper().get("DataSourceName");
        this.reader = new StructReader<>(sourceName);
        logger.info(String.format("DataSourceName (DSN) from inside Codec Mapper : {%s}", sourceName));
        
        /* Get watched directory for listening to file change events */
        if (StringUtils.isEmpty(fileName)) {
            this.dataSourceName = getWatchedDir(sourceName);
        } else {
            this.dataSourceName = getWatchedDir(fileName);
        }
        logger.info(String.format("Watched directory name : {%s}", this.dataSourceName));
    }
    
    @Override
    public void run() {
        try {
            /* Create watched directory observer */
            fileChangeProcessor = new FileChangeProcessor(dataSourceName, StringUtils.EMPTY, this::consumeBatch);
            
            /* Start observing watched dir for changes */
            service.submit(fileChangeProcessor);
        } catch (Exception e) {
            logger.error(String.format("Exception encountered while awaiting on barrier : {%s}", e));
        }
    }
    
    @Override
    public void consume(Object fileEvent) {
    }
    
    @Override
    public void consumeBatch(Object[] events) {
        logger.debug(String.format("Received %d events from poller for processing", events == null ? 0 : events.length));
        Arrays.asList(events).stream()
            .filter(Objects::nonNull)
            .forEach(e -> logger.debug(String.format("File event = %s", e)));
        
        long modifyCount = Arrays.asList(events).stream()
            .filter(Objects::nonNull)
            .filter(e -> StringUtils.contains(e.toString(), FileEventType.IN_MODIFY.name()))
            .count();
        
        /* Stream changes to observer if file has been modified */
        if (modifyCount > 0) {
            logger.debug(String.format("%d file update events have been detected", modifyCount));
            service.submit(this::stream);
        }
    }
    
    private void stream() {
        CompletableFuture<List<Map<String, String>>> future = CompletableFuture.supplyAsync(reader::get, service);
        final List<Object> records = new ArrayList<>();
        
        try {
            List<?> data = future.get(STRUCT_READER_TIMEOUT_SECS, TimeUnit.SECONDS);
            data.stream().forEach(d -> records.add(d));
            
            if (records.size() > 0) {
                /* Asynchronous observer update */
                CompletableFuture.runAsync(() -> observer.onNext(records), service);
                logger.debug(String.format("Observers notified with %d records", records.size()));
            } else {
                logger.debug(String.format("Records not found for streaming to observers"));
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(String.format("Exception while reading records using struct reader : {%s}", e));
            CompletableFuture.runAsync(() -> observer.onError(e), service);
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
    
    @Override
    public void subscribe(IObserver<Object> observer) {
        this.observer = observer;
    }
    
    @Override
    public void unsubscribe(IObserver<Object> observer) {
        if (this.observer.equals(observer)) {
            observer = null;
        }
    }
}
