package com.livequery.common;

import java.util.List;

/**
 * <code>IObserver</code> interface provides three continuations: onNext which
 * is invoked by observable whenever there is next data available, onComplete
 * which is invoked when observable is done transmitting data to observer and
 * onError which is invoked when observable encounters an error while
 * transmitting data to observer.
 */
public interface IObserver<T> {
    
    /**
     * Accept a list of records from observable instance.
     *
     * @param data List of records streamed from observable instance
     */
    void onNext(List<T> data);
    
    /**
     * <code>onComplete</code> is called when observable is done transmitting
     * data to observer.
     */
    void onComplete();
    
    /**
     * <code>onError</code> is called when observable encounters error while
     * transmitting data to observer.
     *
     * @param throwable Throwbale that is sent by observable instance
     */
    void onError(Throwable throwable);
}
