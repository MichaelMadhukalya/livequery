package com.livequery.common;

import java.util.concurrent.ThreadFactory;

/**
 * <p>
 * <code>AppThreadFactory</code> is used for creating daemon threads that can run inside a thread
 * pool. The benefits of running daemon threads inside a thread pool is that the pool does not
 * need to be explicitly shut down when JVM exists while responding to an external event such as
 * <code>SIGTERM</code>.
 * </p>
 */
public class AppThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    }
}
