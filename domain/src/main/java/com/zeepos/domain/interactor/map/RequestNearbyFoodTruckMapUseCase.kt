package com.zeepos.domain.interactor.map

import com.zeepos.domain.interactor.base.MaybeUseCase
import com.zeepos.domain.repository.MapRepo
import com.zeepos.models.entities.MapData
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Arif S. on 7/13/20
 */
class RequestNearbyFoodTruckMapUseCase @Inject constructor(
    private val mapRepo: MapRepo
) : MaybeUseCase<List<MapData>, RequestNearbyFoodTruckMapUseCase.Params>() {

    data class Params(val latitude: Double, val longitude: Double, val distance: String)

    override fun buildUseCaseMaybe(params: Params): Maybe<List<MapData>> {
        return mapRepo.searchNearByFoodTrucksMap(params.latitude, params.longitude, params.distance)
    }
}