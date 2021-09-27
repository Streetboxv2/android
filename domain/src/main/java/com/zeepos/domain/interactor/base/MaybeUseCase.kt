package com.zeepos.domain.interactor.base

import com.zeepos.models.master.User
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Arif S. on 5/12/20
 */
abstract class MaybeUseCase<Results, in Params> {
    private val disposables = CompositeDisposable()

    /**
     * Builds an [Single] which will be used when executing the current [SingleUseCase].
     */
    abstract fun buildUseCaseMaybe(params: Params): Maybe<Results>

    /**
     * Executes the current use case.
     *
     * @param observer [DisposableSingleObserver] which will be listening to the observer build
     * by [buildUseCaseMaybe] method.
     * @param params Parameters (Optional) used to build/execute this use case.
     */
    fun execute(
        observer: DisposableMaybeObserver<Results> = EmptyMaybeObserver(),
        params: Params
    ) {
        val single = buildUseCaseMaybeWithSchedulers(params)
        addDisposable(single.subscribeWith(observer))
    }

    fun execute(params: Params): Maybe<Results> {
        return buildUseCaseMaybeWithSchedulers(params)
    }

    /**
     * Builds a [Single] which will be used when executing the current [SingleUseCase].
     * With provided Schedulers
     */
    private fun buildUseCaseMaybeWithSchedulers(params: Params): Maybe<Results> {
        return buildUseCaseMaybe(params)
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