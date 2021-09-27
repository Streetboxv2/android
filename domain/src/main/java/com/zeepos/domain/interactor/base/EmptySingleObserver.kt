package com.zeepos.domain.interactor.base

import io.reactivex.observers.DisposableSingleObserver

/**
 * Created by Arif S. on 5/12/20
 */
open class EmptySingleObserver<T> : DisposableSingleObserver<T>() {
    override fun onSuccess(t: T) {}

    override fun onError(e: Throwable) {}
}