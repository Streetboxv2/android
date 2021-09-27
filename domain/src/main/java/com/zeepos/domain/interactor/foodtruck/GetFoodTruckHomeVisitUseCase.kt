package com.zeepos.domain.interactor.foodtruck

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.FoodTruckRepo
import com.zeepos.models.entities.AvailableHomeVisitBookDate
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 8/3/20
 */
class GetFoodTruckHomeVisitUseCase @Inject constructor(
    private val foodTruckRepo: FoodTruckRepo
) : SingleUseCase<List<AvailableHomeVisitBookDate>, GetFoodTruckHomeVisitUseCase.Params>() {

    data class Params(val merchantId: Long)

    override fun buildUseCaseSingle(params: Params): Single<List<AvailableHomeVisitBookDate>> {
        return foodTruckRepo.getFoodTruckHomeVisit(params.merchantId)
    }
}