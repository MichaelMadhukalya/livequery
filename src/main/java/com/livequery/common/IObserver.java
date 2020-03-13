package com.livequery.common;

import java.util.List;

public interface IObserver<T> {

    /**
     * @param data
     */
    void onNext(List<T> data);

    /**
     *
     */
    void onComplete();

    /**
     * @param throwable
     */
    void onError(Throwable throwable);
}
