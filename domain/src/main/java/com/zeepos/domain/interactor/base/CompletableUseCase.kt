package com.zeepos.domain.interactor.base

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Arif S. on 5/12/20
 */
abstract class CompletableUseCase<in Params> {

    private val disposables = CompositeDisposable()

    /**
     * Builds a [Completable] which will be used when executing the current [CompletableUseCase].
     */
    abstract fun buildUseCaseCompletable(params: Params): Completable

    /**
     * Executes the current use case.
     *
     * by [buildUseCaseCompletable] method.
     * @param params Parameters (Optional) used to build/execute this use case.
     */

    fun execute(params: Params): Completable {
        return buildUseCaseCompletableWithSchedulers(params)
    }

    /**
     * Builds a [Completable] which will be used when executing the current [CompletableUseCase].
     * With provided Schedulers
     */
    private fun buildUseCaseCompletableWithSchedulers(params: Params): Completable {
        return buildUseCaseCompletable(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    protected fun addDisposable(disposable: Disposable) {
        disposables.add(checkNotNull(disposable, { "disposable cannot be null!" }))
    }

    open fun dispose() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

}