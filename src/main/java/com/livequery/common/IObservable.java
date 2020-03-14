package com.livequery.common;

public interface IObservable<T> {
    
    /**
     * Adds an observer to an observable
     */
    void subscribe(IObserver<T> observer);
    
    /**
     * Removes an observer from an observable
     */
    void unsubscribe(IObserver<T> observer);
}
