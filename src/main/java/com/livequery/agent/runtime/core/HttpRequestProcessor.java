package com.livequery.agent.runtime.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

public class HttpRequestProcessor {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /**
     * Port
     */
    private int portNum;

    public HttpRequestProcessor() {
        this(-1);
    }

    public HttpRequestProcessor(int portNum) {
        this.portNum = portNum;
    }

    public void process() {

        try (ServerSocket socket = new ServerSocket(portNum)) {

            /* Keep listening for new connections */
            while (true) {
                Socket service = socket.accept();
            }

        } catch (IOException e) {
        } catch (Exception e) {
        }
    }
}
