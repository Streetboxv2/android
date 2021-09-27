package com.zeepos.domain.interactor.parkingspace

import com.zeepos.domain.interactor.base.SynchronousUseCase
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.models.master.ParkingSpace
import javax.inject.Inject

/**
 * Created by Arif S. on 5/18/20
 */
class GetParkingSpaceUseCase @Inject constructor(
    private val parkingSpaceRepository: ParkingSpaceRepository
) :
    SynchronousUseCase<ParkingSpace?, GetParkingSpaceUseCase.Params> {

    override fun execute(params: Params): ParkingSpace? {
        return parkingSpaceRepository.getParkingSpace(params.id)
    }

    data class Params(val id: Long)
}