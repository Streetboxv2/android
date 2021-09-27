package com.zeepos.domain.interactor.parkingsales

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.ParkingSalesRepo
import com.zeepos.models.master.ParkingSlot
import com.zeepos.models.master.ParkingSpace
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 5/17/20
 */
class CreateOrUpdateParkingSalesUseCase @Inject constructor(
    private val parkingSalesRepo: ParkingSalesRepo
) : CompletableUseCase<CreateOrUpdateParkingSalesUseCase.Params>() {

    data class Params(
        val parkingSpace: ParkingSpace,
        val parkingSlot: ParkingSlot
    )

    override fun buildUseCaseCompletable(params: Params): Completable {
        return Completable.fromAction { parkingSalesRepo.createOrUpdateParkingSales(params) }
    }
}