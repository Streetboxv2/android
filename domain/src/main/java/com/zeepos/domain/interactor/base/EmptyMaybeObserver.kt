package com.zeepos.domain.interactor.base

import io.reactivex.observers.DisposableMaybeObserver

/**
 * Created by Arif S. on 5/12/20
 */
open class EmptyMaybeObserver<T> : DisposableMaybeObserver<T>() {
    override fun onSuccess(t: T) {
    }

    override fun onComplete() {
    }

    override fun onError(e: Throwable) {
    }
}