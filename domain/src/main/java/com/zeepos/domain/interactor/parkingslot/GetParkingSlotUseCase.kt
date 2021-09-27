package com.zeepos.domain.interactor.parkingslot

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.ParkingSlotRepo
import com.zeepos.models.master.ParkingSlot
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 5/23/20
 */
class GetParkingSlotUseCase @Inject constructor(
    private val parkingSlotRepo: ParkingSlotRepo
) : MaybeUseCase<ParkingSlot, GetParkingSlotUseCase.Params>() {

    override fun buildUseCaseMaybe(params: Params): Maybe<ParkingSlot> {
        return parkingSlotRepo.getWithObs(params.id)
    }

    data class Params(val id: Long)
}