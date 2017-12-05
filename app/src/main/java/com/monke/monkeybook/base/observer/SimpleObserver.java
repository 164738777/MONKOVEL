//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.base.observer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 作用：简化版Observer，预先实现onSubscribe, onComplete
 */
public abstract class SimpleObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {

    }
}
