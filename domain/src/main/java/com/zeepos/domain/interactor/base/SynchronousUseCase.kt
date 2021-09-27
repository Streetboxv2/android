package com.zeepos.domain.interactor.base

/**
 * Created by Arif S. on 5/12/20
 */
interface SynchronousUseCase<out Results, in Params> {
    /**
     * Executes the current use case and returns the result immediately.
     * If this should not return anything then use [Unit] as [Results].
     */
    fun execute(params: Params): Results

}