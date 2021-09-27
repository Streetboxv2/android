package com.zeepos.domain.interactor.base

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Arif S. on 5/12/20
 */
abstract class ObservableUseCase<Results, in Params> {

    /**
     * Builds an [Observable] which will be used when executing the current [ObservableUseCase].
     */
    abstract fun buildUseCaseObservable(params: Params): Observable<Results>

    /**
     * Executes the current use case.
     *
     * by [buildUseCaseObservable] method.
     * @param params Parameters (Optional) used to build/execute this use case.
     */

    fun execute(params: Params): Observable<Results> {
        return buildUseCaseObservableWithSchedulers(params)
    }

    /**
     * Builds an [Observable] which will be used when executing the current [ObservableUseCase].
     * With provided Schedulers
     */
    private fun buildUseCaseObservableWithSchedulers(params: Params): Observable<Results> {
        return buildUseCaseObservable(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}