package com.livequery.agent.filesystem.core;

import java.util.concurrent.TimeUnit;

public interface IFileChangeProcessor {

    void poll();

    /**
     *
     * @param time
     * @param timeUnit
     * @return
     */
    boolean tryPoll(int time, TimeUnit timeUnit);
}
