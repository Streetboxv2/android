package com.zeepos.domain.interactor.user

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/7/20
 */
class GetAvailableDateScheduleUseCase @Inject constructor(
    private val userRepository: UserRepository
) : SingleUseCase<List<Long>, GetAvailableDateScheduleUseCase.Params>() {
    data class Params(val parkingSloSalesId: Long, val foodTruckId: Long)

    override fun buildUseCaseSingle(params: Params): Single<List<Long>> {
        return userRepository.getAvailableDate(params.parkingSloSalesId, params.foodTruckId)
    }
}