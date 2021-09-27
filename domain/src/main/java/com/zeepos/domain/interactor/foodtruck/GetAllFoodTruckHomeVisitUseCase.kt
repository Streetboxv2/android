package com.zeepos.domain.interactor.foodtruck

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.FoodTruckRepo
import com.zeepos.models.master.FoodTruck
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/15/20
 */
class GetAllFoodTruckHomeVisitUseCase @Inject constructor(
    private val foodTruckRepo: FoodTruckRepo
) : SingleUseCase<List<FoodTruck>, GetAllFoodTruckHomeVisitUseCase.Params>() {
    override fun buildUseCaseSingle(params: Params): Single<List<FoodTruck>> {
        return foodTruckRepo.getAllFoodTruckHomeVisit(params.page)
    }

    data class Params(val page: Int)
}