package com.zeepos.domain.repository

import com.zeepos.models.entities.AvailableHomeVisitBookDate
import com.zeepos.models.master.FoodTruck
import io.reactivex.Single

/**
 * Created by Arif S. on 7/15/20
 */
interface FoodTruckRepo {
    fun getAllFoodTruckHomeVisit(page: Int): Single<List<FoodTruck>>
    fun getFoodTruckHomeVisit(merchantId: Long): Single<List<AvailableHomeVisitBookDate>>
}