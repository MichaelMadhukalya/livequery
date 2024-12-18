package com.livequery.agent.filesystem.core;

import com.livequery.agent.storagenode.core.CodecMapper;
import com.livequery.common.Document;
import com.livequery.common.Environment;
import com.livequery.common.IObserver;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class FileChangeObserver implements IObserver<Document> {
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    
    /* File that is watched for changes */
    private final String fileName;
    
    public FileChangeObserver() {
        this(StringUtils.EMPTY);
    }
    
    public FileChangeObserver(String fileName) {
        if (!StringUtils.isEmpty(fileName)) {
            this.fileName = fileName;
        } else {
            this.fileName = (String) new CodecMapper(new Environment().getCodecFilePath()).getCodecMapper()
                .get("DataSourceName");
        }
    }
    
    @Override
    public void onNext(List<Document> data) {
        logger.debug(String.format("Number of records read : {%d}", null == data ? 0 : data.size()));
    }
    
    @Override
    public void onComplete() {
        logger.info(String.format("End of data transmission message received"));
    }
    
    @Override
    public void onError(Throwable throwable) {
        logger.error(String.format("Error listening to file change events : {%s}", throwable));
    }
}
