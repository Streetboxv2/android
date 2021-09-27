package com.zeepos.domain.interactor.parkingspace

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.models.entities.ParkingSchedule
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/15/20
 */
class GetParkingSpaceScheduleUseCase @Inject constructor(
    private val parkingSpaceRepository: ParkingSpaceRepository
) :
    SingleUseCase<List<ParkingSchedule>, GetParkingSpaceScheduleUseCase.Params>() {
    data class Params(val parkingSpaceId: Long)

    override fun buildUseCaseSingle(params: Params): Single<List<ParkingSchedule>> {
        return parkingSpaceRepository.getParkingSchedule(params.parkingSpaceId)
    }
}