package com.zeepos.domain.interactor.parkingspace

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.models.master.ParkingSpace
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/15/20
 */
class GetAllParkingSpaceCloudUseCase @Inject constructor(
    private val parkingSpaceRepository: ParkingSpaceRepository
) : SingleUseCase<List<ParkingSpace>, GetAllParkingSpaceCloudUseCase.Params>() {
    override fun buildUseCaseSingle(params: Params): Single<List<ParkingSpace>> {
        return parkingSpaceRepository.getParkingSpaceCloud(params.isRefresh)
    }

    data class Params(val isRefresh: Boolean)
}