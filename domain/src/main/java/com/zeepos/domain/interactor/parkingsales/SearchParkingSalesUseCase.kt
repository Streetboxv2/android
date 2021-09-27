package com.zeepos.domain.interactor.parkingsales

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.ParkingSalesRepo
import com.zeepos.models.transaction.ParkingSales
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 5/29/20
 */
class SearchParkingSalesUseCase @Inject constructor(
    private val parkingSalesRepo: ParkingSalesRepo
) : MaybeUseCase<List<ParkingSales>, SearchParkingSalesUseCase.Params>() {
    override fun buildUseCaseMaybe(params: Params): Maybe<List<ParkingSales>> {
        return parkingSalesRepo.searchParkingSales(params.keyword)
    }

    data class Params(val keyword: String)
}