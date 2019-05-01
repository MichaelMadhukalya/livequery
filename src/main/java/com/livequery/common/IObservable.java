package com.livequery.common;

import java.util.function.Function;

public interface IObservable<T> {

    /**
     *
     * @param next
     * @param complete
     * @param error
     */
    void subscribe(
        Function<T, Void> next,
        Function<Void, Void> complete,
        Function<Exception, Void> error);

    /**
     *
     */
    void unsubscribe(IObserver<T> observer);
}
