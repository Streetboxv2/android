package com.zeepos.domain.interactor.base

import io.reactivex.observers.DisposableCompletableObserver

/**
 * Created by Arif S. on 5/12/20
 */
open class EmptyCompletableObserver : DisposableCompletableObserver() {

    override fun onComplete() {}

    override fun onError(e: Throwable) {}
}