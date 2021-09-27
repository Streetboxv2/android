package com.zeepos.domain.interactor.parkingsales

import com.zeepos.domain.interactor.base.SynchronousUseCase
import com.zeepos.domain.repository.ParkingSalesRepo
import com.zeepos.models.transaction.ParkingSales
import javax.inject.Inject

/**
 * Created by Arif S. on 5/19/20
 */
class GetParkingSalesByParkingSpaceIdUseCase @Inject constructor(
    private val parkingSalesRepo: ParkingSalesRepo
) : SynchronousUseCase<ParkingSales?, GetParkingSalesByParkingSpaceIdUseCase.Params> {

    override fun execute(params: Params): ParkingSales? {
        return parkingSalesRepo.getByParkingSpaceId(params.parkingSpaceId)
    }

    data class Params(val parkingSpaceId: Long)

}