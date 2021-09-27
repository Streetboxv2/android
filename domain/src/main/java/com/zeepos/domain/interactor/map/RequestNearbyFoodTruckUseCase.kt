package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.MapRepo
import com.zeepos.models.entities.MapData
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 5/24/20
 */
class RequestNearbyFoodTruckUseCase @Inject constructor(
    private val mapRepo: MapRepo
) : MaybeUseCase<List<MapData>, RequestNearbyFoodTruckUseCase.Params>() {

    override fun buildUseCaseMaybe(params: Params): Maybe<List<MapData>> {
        return mapRepo.searchNearByFoodTrucks(params)
    }

    data class Params(
        val type: String,
        val latitude: Double,
        val longitude: Double,
        val page: Int,
        val distance: String
    )
}