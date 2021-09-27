package com.zeepos.domain.repository

import com.zeepos.domain.interactor.map.RequestDirectionUseCase
import com.zeepos.domain.interactor.map.RequestLiveTrackingUseCase
import com.zeepos.domain.interactor.map.RequestNearbyFoodTruckUseCase
import com.zeepos.domain.interactor.map.UpdateCurrentLocationToCloud
import com.zeepos.models.entities.MapData
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

/**
 * Created by Arif S. on 5/24/20
 */
interface MapRepo {
    fun searchNearByFoodTrucks(params: RequestNearbyFoodTruckUseCase.Params): Maybe<List<MapData>>
    fun searchNearByFoodTrucksMap(
        latitude: Double,
        longitude: Double,
        distance: String
    ): Maybe<List<MapData>>

    fun getFoodTruckDirection(params: RequestDirectionUseCase.Params): Observable<MapData>
    fun requestMapTrackingDirectionOperator(params: RequestLiveTrackingUseCase.Params): Observable<MapData>
    fun scheduleGetAllLocationFromServer(latitude: Double, longitude: Double): Observable<MapData>
    fun scheduleGetLocationFromServer(taskId: Long): Observable<MapData>
    fun getCheckInLocationFromServer(taskId: Long): Observable<MapData>
    fun updateLocationToCloud(params: UpdateCurrentLocationToCloud.Params): Completable
}