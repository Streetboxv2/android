package com.zeepos.domain.interactor.parkingsales

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSalesRepo
import com.zeepos.models.transaction.ParkingSales
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/17/20
 */
class GetParkingSalesCloudUseCase @Inject constructor(
    private val parkingSalesRepo: ParkingSalesRepo
) : SingleUseCase<List<ParkingSales>, GetParkingSalesCloudUseCase.Params>() {
    override fun buildUseCaseSingle(params: Params): Single<List<ParkingSales>> {
        return parkingSalesRepo.getParkingSalesCloud(params.isRefresh)
    }

    data class Params(val isRefresh: Boolean)

}