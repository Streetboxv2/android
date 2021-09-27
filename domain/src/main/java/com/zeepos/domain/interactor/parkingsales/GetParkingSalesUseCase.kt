package com.zeepos.domain.interactor.parkingsales

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ParkingSalesRepo
import com.zeepos.models.entities.None
import com.zeepos.models.transaction.ParkingSales
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 6/18/20
 */
class GetParkingSalesUseCase @Inject constructor(
    private val parkingSalesRepo: ParkingSalesRepo
) : SingleUseCase<List<ParkingSales>, None>() {
    override fun buildUseCaseSingle(params: None): Single<List<ParkingSales>> {
        return parkingSalesRepo.getParkingSales()
    }
}