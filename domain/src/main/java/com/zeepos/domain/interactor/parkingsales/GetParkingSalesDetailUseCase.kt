package com.zeepos.domain.interactor.parkingsales

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSalesRepo
import com.zeepos.models.transaction.ParkingSales
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/29/20
 */
class GetParkingSalesDetailUseCase @Inject constructor(
    private val parkingSalesRepo: ParkingSalesRepo
) :
    SingleUseCase<ParkingSales, GetParkingSalesDetailUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<ParkingSales> {
        return parkingSalesRepo.getParkingSalesDetail(params.parkingSalesId)
    }

    data class Params(val parkingSalesId: Long)
}