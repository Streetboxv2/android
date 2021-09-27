package com.zeepos.domain.interactor.base

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Arif S. on 5/12/20
 */
abstract class SingleUseCase<Results, in Params> {

    /**
     * Builds an [Single] which will be used when executing the current [SingleUseCase].
     */
    abstract fun buildUseCaseSingle(params: Params): Single<Results>

    /**
     * Executes the current use case.
     *
     * by [buildUseCaseSingle] method.
     */

    fun execute(params: Params): Single<Results> {
        return buildUseCaseSingleWithSchedulers(params)
    }

    /**
     * Builds a [Single] which will be used when executing the current [SingleUseCase].
     * With provided Schedulers
     */
    private fun buildUseCaseSingleWithSchedulers(params: Params): Single<Results> {
        return buildUseCaseSingle(params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}