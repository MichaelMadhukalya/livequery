package com.livequery.agent.runtime.core;

import com.livequery.common.AbstractNode;
import com.livequery.common.Environment;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class HttpRequestProcessor extends AbstractNode {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * Port
     */
    private int portNum;

    /**
     * Environment
     */
    private Environment environment = new Environment();

    /**
     * Thread pool service for handling Http requests
     */
    private ExecutorService executorService;

    public HttpRequestProcessor() {
        this(-1);
    }

    public HttpRequestProcessor(int portNum) {
        this.portNum = portNum;
    }

    public void process() {
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            /* Keep listening for new connections */
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(new HttpRequestHandler(socket));
            }
        } catch (IOException e) {
            logger.error(String.format("Exception while submitting Http request : {%s}", e));
        } catch (Exception e) {
            logger.error(String.format("Exception while submitting Http request : {%s}", e));
        }
    }

    @Override
    protected void pre() {
        /* Create a new fixed size thread pool for handling requests */
        int n = environment.getMaxConcurrentHttpRequests();
        executorService = Executors.newFixedThreadPool(n);
        logger.info(String.format("Started executor service with thread pool size : %d", n));

        /* Reset port num if not set already */
        if (portNum == -1 || portNum < 1024) {
            portNum = environment.getHttpPort();
            logger.info(String.format("Reseting http port number to : %d", portNum));
        }
    }

    @Override
    protected void post() {
        try {
            if (executorService.isShutdown()) {
                /* initiate shutdown */
                executorService.shutdown();
                boolean shutDown = executorService.awaitTermination(60, TimeUnit.SECONDS);

                if (!shutDown) {
                    /* Shut down now */
                    List<?> tasks = executorService.shutdownNow();
                    logger.warn(String.format("Found %d tasks waiting when shutdown was initiated",
                        tasks != null ? tasks.size() : 0));
                }
            }
        } catch (InterruptedException e) {
            logger.error(String.format("Exception shuting down service for Http requests:{%s}", e));
        } finally {
            if (!executorService.isTerminated()) {
                logger.warn("Not all tasks completed successfully post shutdown of Http processor");
            }
        }
    }
}
