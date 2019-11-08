package com.livequery.common;

import java.util.function.Consumer;

public interface IObservable<T> {

    /**
     *
     * @param next
     * @param complete
     * @param error
     */
    void subscribe(
        Consumer<T> next,
        NoMoreValue complete,
        Consumer<Throwable> error);

    /**
     *
     */
    void unsubscribe(IObserver<T> observer);
}
