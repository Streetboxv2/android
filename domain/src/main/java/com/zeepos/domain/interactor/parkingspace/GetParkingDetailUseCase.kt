package com.zeepos.domain.interactor.parkingspace

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSlotRepo
import com.zeepos.models.master.ParkingSlot
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/22/20
 */
class GetParkingDetailUseCase @Inject constructor(
    private val parkingSlotRepo: ParkingSlotRepo
) : SingleUseCase<List<ParkingSlot>, GetParkingDetailUseCase.Params>() {

    override fun buildUseCaseSingle(params: Params): Single<List<ParkingSlot>> {
        return parkingSlotRepo.getParkingDetail(params.id)
    }

    data class Params(val id: Long)
}