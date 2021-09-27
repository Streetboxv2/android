package com.zeepos.domain.interactor.parkingspace

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.models.entities.Schedule
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/18/20
 */
class GetMerchantParkingScheduleUseCase @Inject constructor(
    private val parkingSpaceRepository: ParkingSpaceRepository
) : SingleUseCase<List<Schedule>, GetMerchantParkingScheduleUseCase.Params>() {
    data class Params(val merchantId: Long, val typesId: Long)

    override fun buildUseCaseSingle(params: Params): Single<List<Schedule>> {
        return parkingSpaceRepository.getMerchantParkingSchedule(params.merchantId, params.typesId)
    }
}