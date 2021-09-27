package com.zeepos.domain.interactor.parkingspace

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.models.entities.None
import com.zeepos.models.master.ParkingSpace
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 6/18/20
 */
class GetAllParkingUseCase @Inject constructor(
    private val parkingSpaceRepository: ParkingSpaceRepository
) : SingleUseCase<List<ParkingSpace>, None>() {
    override fun buildUseCaseSingle(params: None): Single<List<ParkingSpace>> {
        return parkingSpaceRepository.getParkingSpace()
    }
}