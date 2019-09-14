package com.livequery.agent.filesystem.core;

import com.livequery.agent.storagenode.core.CodecMapper;
import com.livequery.common.AbstractNode;
import com.livequery.common.Environment;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileChangeConsumer extends AbstractNode implements IFileChangeConsumer<Object> {
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());
    
    /**
     * Load native library
     */
    private static final String NATIVE_LIB_NAME = "poll";
    
    /** Load native libaray */
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
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_OF_THREADS);
    
    @Override
    protected void pre() {
    }
    
    @Override
    protected void post() {
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
        Function<Object[], Void> process = events -> {
            consumeBatch(events);
            return null;
        };
        
        /* Create watched directory observer */
        fileChangeProcessor = new FileChangeProcessor(dataSourceName, StringUtils.EMPTY, process, cyclicBarrier);
        
        /* Start observing watched dir for changes */
        new Thread(fileChangeProcessor).start();
        
        try {
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
        Arrays.asList(events).stream().forEach(e -> logger.info(String.format("Event detected : {%s}", e)));
    }
    
    private String getWatchedDir(String dataSourceName) {
        File file = new File(dataSourceName);
        if (file.isDirectory()) {
            return dataSourceName;
        } else {
            return getWatchedDir(file.getParent());
        }
    }
}
