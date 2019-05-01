package com.livequery.common;

public interface IObserver<T> {

    /**
     * @param data
     */
    void onNext(T data);

    /**
     *
     */
    void onComplete();

    /**
     * @param throwable
     */
    void onError(Throwable throwable);
}
