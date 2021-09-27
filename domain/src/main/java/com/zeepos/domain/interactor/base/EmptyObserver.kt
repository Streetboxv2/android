package com.zeepos.domain.interactor.base

import io.reactivex.annotations.NonNull
import io.reactivex.observers.DisposableObserver

/**
 * Created by Arif S. on 5/12/20
 */
open class EmptyObserver<T> : DisposableObserver<T>() {
    override fun onNext(@NonNull t: T) {}

    override fun onError(@NonNull e: Throwable) {}

    override fun onComplete() {}
}