package com.zeepos.localstorage

import android.util.Log
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import com.zeepos.domain.interactor.map.RequestDirectionUseCase
import com.zeepos.domain.interactor.map.RequestLiveTrackingUseCase
import com.zeepos.domain.interactor.map.RequestNearbyFoodTruckUseCase
import com.zeepos.domain.interactor.map.UpdateCurrentLocationToCloud
import com.zeepos.domain.repository.MapRepo
import com.zeepos.domain.repository.ParkingSpaceRepository
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.FoodTruckLocation
import com.zeepos.models.entities.MapData
import com.zeepos.models.entities.OperatorLocation
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.remotestorage.RemoteService
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.BiFunction
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/24/20
 */
class MapRepoImpl @Inject constructor(
    retrofit: Retrofit,
    private val geoApiContext: GeoApiContext,
    private val parkingSpaceRepository: ParkingSpaceRepository,
    private val taskOperatorRepo: TaskOperatorRepo
) : MapRepo {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    override fun searchNearByFoodTrucks(params: RequestNearbyFoodTruckUseCase.Params): Maybe<List<MapData>> {

        val queryMap: HashMap<String, String> = hashMapOf()
        queryMap["limit"] = "20"
        queryMap["page"] = params.page.toString()
        queryMap["distance"] = params.distance

        return service.searchNearByFoodTrucks(
            params.latitude,
            params.longitude,
            queryMap
        )
            .map {
                if (it.isSuccess()) {
                    val foodTrucks = it.data!!
                    val data = ArrayList<MapData>()

                    for (foodTruck in foodTrucks) {
                        if (foodTruck.types == ConstVar.FOODTRUCK_TYPE_HOMEVISIT) continue
                        val latLng = LatLng(foodTruck.latitude, foodTruck.longitude)
                        val mapData = MapData(ConstVar.MAP_TYPE_NEAR_BY)
                        mapData.latLng = latLng
                        mapData.exData = foodTruck
                        data.add(mapData)
                    }

                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun searchNearByFoodTrucksMap(
        latitude: Double,
        longitude: Double,
        distance: String
    ): Maybe<List<MapData>> {

        val queryMap: HashMap<String, String> = hashMapOf()
        queryMap["distance"] = distance

        val checkInFoodTruck: Maybe<List<MapData>> = service.searchNearByFoodTrucks(
//            activity = ConstVar.MAP_NEARBY_CHECK_IN,
            latitude = latitude,
            longitude = longitude,
            query = queryMap
        )
            .map {
                if (it.isSuccess()) {
                    val foodTrucks = it.data!!
                    val data = ArrayList<MapData>()

                    for (foodTruck in foodTrucks) {
//                        foodTruck.status = ConstVar.FOOD_TRUCK_STATUS_CHECK_IN
                        val latLng = LatLng(foodTruck.latitude, foodTruck.longitude)
                        val mapData = MapData(ConstVar.MAP_TYPE_NEAR_BY)
                        mapData.latLng = latLng
                        mapData.exData = foodTruck
                        data.add(mapData)
                    }

                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }

        val checkOutFoodTruck: Maybe<List<MapData>> = service.searchNearByFoodTrucks(
//            activity = ConstVar.MAP_NEARBY_CHECK_OUT,
            latitude = latitude,
            longitude = longitude,
            query = queryMap
        )
            .map {
                if (it.isSuccess()) {
                    val foodTrucks = it.data!!
                    val data = ArrayList<MapData>()

                    for (foodTruck in foodTrucks) {
                        foodTruck.status = ConstVar.FOOD_TRUCK_STATUS_CHECK_OUT
                        val latLng = LatLng(foodTruck.latitude, foodTruck.longitude)
                        val mapData = MapData(ConstVar.MAP_TYPE_NEAR_BY)
                        mapData.latLng = latLng
                        mapData.exData = foodTruck
                        data.add(mapData)
                    }

                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }

        val nearByParkingSpace: Maybe<List<MapData>> = service.searchNearByParkingSpaceMaps(
            latitude,
            longitude, "10"
        ).map {
            if (it.isSuccess()) {
                val parkingSpaceList = it.data!!
                val data = ArrayList<MapData>()

                for (parkingSpace in parkingSpaceList) {

                    val latLng = LatLng(parkingSpace.latitude, parkingSpace.longitude)
                    val mapData = MapData(ConstVar.MAP_TYPE_NEAR_BY)
                    mapData.latLng = latLng
                    mapData.exData = parkingSpace
                    data.add(mapData)
                }

                return@map data
            }

            throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
        }

//        return Maybe.zip(
//            nearByParkingSpace, checkInFoodTruck, checkOutFoodTruck,
//            Function3<List<MapData>, List<MapData>, List<MapData>, List<MapData>> { data1, data2, data3 ->
//                val data: MutableList<MapData> = mutableListOf()
//                data.addAll(data1)
//                data.addAll(data2)
//                data.addAll(data3)
//
//                return@Function3 data
//            })

        return Maybe.zip(
            nearByParkingSpace, checkInFoodTruck,
            BiFunction<List<MapData>, List<MapData>, List<MapData>> { data1, data2 ->
                val data: MutableList<MapData> = mutableListOf()
                data.addAll(data1)
                data.addAll(data2)

                return@BiFunction data
            })
    }

    override fun getFoodTruckDirection(params: RequestDirectionUseCase.Params): Observable<MapData> {

        val taskOperator = taskOperatorRepo.get(params.taskId)
            ?: return Observable.error(Throwable(ConstVar.DATA_NULL))

        return getGoogleMapDirection(
            params.currentLat, params.currentLng,
            taskOperator.latParkingSpace, taskOperator.lonParkingSpace
        )
            .map {
                return@map ObjectFactory.createMapData(
                    ConstVar.MAP_TYPE_DIRECTION,
                    exData = taskOperator,
                    latLngList = it
                )
            }
    }

    override fun requestMapTrackingDirectionOperator(params: RequestLiveTrackingUseCase.Params): Observable<MapData> {
        val taskOperator = taskOperatorRepo.get(params.taskId)
            ?: return Observable.error(Throwable(ConstVar.DATA_NULL))

        return getGoogleMapDirection(
            params.latitude, params.longitude,
            taskOperator.latParkingSpace, taskOperator.lonParkingSpace
        )
            .map {
                return@map ObjectFactory.createMapData(
                    ConstVar.MAP_TYPE_DIRECTION,
                    exData = taskOperator,
                    latLngList = it
                )
            }
    }

    override fun scheduleGetAllLocationFromServer(
        latitude: Double,
        longitude: Double
    ): Observable<MapData> {

//        return service.getNearByFoodTruckLive(latitude, longitude)
//            .flatMapObservable {
//                val observableList: MutableList<Observable<MapData>> = arrayListOf()
//
//                if (it.isSuccess()) {
//                    val nearByFoodTruckList = it.data!!
//
//                    nearByFoodTruckList.forEach { foodTruck ->
//                        val observable = Observable.interval(0, 10, TimeUnit.SECONDS)
//                            .flatMap { _ ->
//                                return@flatMap service.getLatestOperatorLocation(foodTruck.tasksId)
//                                    .map { responseApi ->
//                                        if (responseApi.isSuccess()) {
//                                            val data = responseApi.data!!
//                                            val mTaskOperator = taskOperatorRepo.get(data.tasksId)
//
//                                            return@map ObjectFactory.createMapData(
//                                                ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION,
//                                                exData = mTaskOperator!!,
//                                                latLng = LatLng(data.latitude, data.longitude)
//                                            )
//                                        }
//
//                                        return@map ObjectFactory.createMapData(ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION)
//                                    }
//
//                            }
//                            .map {
//                                var mTaskOperator = taskOperatorRepo.get(foodTruck.tasksId)
//                                if (mTaskOperator == null) {
//                                    val newTaskOp = TaskOperator()
//                                    newTaskOp.tasksId = foodTruck.tasksId
//                                    newTaskOp.name = foodTruck.name
//                                    newTaskOp.address = foodTruck.address
//
//                                    mTaskOperator = newTaskOp
//                                }
//
//                                ObjectFactory.createMapData(
//                                    ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION,
//                                    exData = mTaskOperator,
//                                    latLng = LatLng(foodTruck.latitude, foodTruck.longitude)
//                                )
//                            }
//
//                        observableList.add(observable)
//                    }
//                }
//                return@flatMapObservable Observable.merge(observableList)
//            }

        return Observable.interval(0, 10, TimeUnit.SECONDS)//unlimited loop if not dispose
            .flatMap {
                return@flatMap service.getNearByFoodTruckLive(latitude, longitude, "10")
                    .flatMapObservable { res ->

                        val observableList: MutableList<Observable<MapData>> = arrayListOf()

                        if (res.isSuccess()) {
                            val nearByFoodTruckList = res.data!!

                            nearByFoodTruckList.forEach {
                                val observable = Observable.just(it)
                                    .map { foodTruck ->
                                        var mTaskOperator = taskOperatorRepo.get(foodTruck.tasksId)
                                        if (mTaskOperator == null) {
                                            if (foodTruck.status == ConstVar.FOOD_TRUCK_STATUS_ONGOING) {
                                                val newTaskOp = TaskOperator()
                                                newTaskOp.tasksId = foodTruck.tasksId
                                                newTaskOp.types = foodTruck.types
                                                newTaskOp.merchantId = foodTruck.merchantId
                                                newTaskOp.merchantName = foodTruck.merchantName
                                                newTaskOp.merchantIG = foodTruck.merchantIG
                                                newTaskOp.logo = foodTruck.logo
                                                newTaskOp.banner = foodTruck.banner
                                                newTaskOp.status = foodTruck.status

                                                mTaskOperator = newTaskOp
                                            }
                                        }

                                        ObjectFactory.createMapData(
                                            ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION,
                                            exData = mTaskOperator,
                                            latLng = LatLng(foodTruck.latitude, foodTruck.longitude)
                                        )
                                    }
                                observableList.add(observable)
                            }

                        }

                        Observable.merge(observableList)
                    }
            }
    }

    override fun scheduleGetLocationFromServer(taskId: Long): Observable<MapData> {
        var latlng:List<LatLng> = ArrayList()
        return Observable.interval(0, 10, TimeUnit.SECONDS)
            .flatMap {
                return@flatMap service.getLatestOperatorLocation(taskId)
                    .map {

                        if (it.isSuccess()) {

                            val data = it.data!!
                            val mTaskId = if (data.tasksId > 0) {
                                data.tasksId
                            } else {
                                taskId
                            }

                            var mTaskOperator = taskOperatorRepo.get(mTaskId)
                            latlng.forEach {  }

                            if (mTaskOperator != null) {

                                if (data.status >= ConstVar.TASK_STATUS_ARRIVED) {//food truck arrived
                                    mTaskOperator.status = data.status
                                    taskOperatorRepo.insertUpdate(mTaskOperator)
                                }

                            } else {
                                val newTaskOperator = TaskOperator()
                                newTaskOperator.tasksId = data.tasksId
                                taskOperatorRepo.insertUpdate(newTaskOperator)
                                mTaskOperator = newTaskOperator
                            }

                            return@map ObjectFactory.createMapData(
                                ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION,
                                exData = mTaskOperator,
                                latLng = LatLng(data.latitude, data.longitude)
                            )
                        }

                        return@map ObjectFactory.createMapData(ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION,it.data,latLng = LatLng(it.data!!.latitude, it.data!!.longitude))
                    }
            }
    }

    override fun getCheckInLocationFromServer(taskId: Long): Observable<MapData> {
        return service.getLatestOperatorLocation(taskId)
            .map {

                if (it.isSuccess()) {
                    val data = it.data!!
                    val mTaskId = if (data.tasksId > 0) {
                        data.tasksId
                    } else {
                        taskId
                    }

                    val mTaskOperator = taskOperatorRepo.get(mTaskId)

                    if (mTaskOperator != null) {
                        return@map ObjectFactory.createMapData(
                            ConstVar.MAP_TYPE_CHECK_IN_LOCATION,
                            exData = mTaskOperator,
                            latLng = LatLng(data.latitude, data.longitude)
                        )
                    }
                }
                val taskOperator = TaskOperator()
                taskOperator.latParkingSpace = it.data!!.latitude
                taskOperator.lonParkingSpace = it.data!!.longitude
                taskOperator.tasksId = it.data!!.tasksId
                taskOperator.status = it.data!!.status
                return@map ObjectFactory.createMapData(ConstVar.MAP_TYPE_CHECK_IN_LOCATION,taskOperator ,LatLng(it.data!!.latitude, it.data!!.longitude))
            }
    }

    override fun updateLocationToCloud(params: UpdateCurrentLocationToCloud.Params): Completable {
        val msg = FoodTruckLocation()
        msg.taskId = params.taskId
        msg.latitude = params.currentLat
        msg.longitude = params.currentLng
        msg.tag = "cloud"

        EventBus.getDefault().post(msg)

        return service.updateLocation(
            OperatorLocation(
                params.taskId,
                params.currentLat,
                params.currentLng
            )
        )
    }

    private fun getGoogleMapDirection(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double
    ): Observable<List<LatLng>> {

        val pickUpLocation = LatLng(originLat, originLng)
        val destinationLocation = LatLng(destinationLat, destinationLng)
        val directionsApiRequest = DirectionsApiRequest(geoApiContext)

        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(pickUpLocation)
        directionsApiRequest.destination(destinationLocation)

        return Observable.create { emitter ->
            directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onFailure(e: Throwable) {
                    Log.d(ConstVar.TAG, "onResult : ${e.message}")
                    emitter.onError(e)
                }

                override fun onResult(result: DirectionsResult) {
                    Log.d(ConstVar.TAG, "onResult : $result")
                    val routeList = result.routes

                    if (routeList.isNotEmpty()) {
                        val directionPath = arrayListOf<LatLng>()
                        for (route in routeList) {
                            val path = route.overviewPolyline.decodePath()
                            directionPath.addAll(path)
                        }

                        emitter.onNext(directionPath)
                        emitter.onComplete()

                    }
                }

            })
        }
    }

    private fun getDirectionPath(
        result: DirectionsResult
    ): List<LatLng> {
        val routeList = result.routes
        val directionPath = ArrayList<LatLng>()

        for (route in routeList) {
            val path = route.overviewPolyline.decodePath()
            directionPath.addAll(path)
        }

        return directionPath
    }

    private fun getFakeNearbyFoodTruckLocations(
        latitude: Double,
        longitude: Double
    ): List<LatLng> {
        val nearbyFoodTruckLocations = arrayListOf<LatLng>()
        val size = (4..6).random()

        for (i in 1..size) {
            val randomOperatorForLat = (0..1).random()
            val randomOperatorForLng = (0..1).random()
            var randomDeltaForLat = (10..50).random() / 10000.00
            var randomDeltaForLng = (10..50).random() / 10000.00
            if (randomOperatorForLat == 1) {
                randomDeltaForLat *= -1
            }
            if (randomOperatorForLng == 1) {
                randomDeltaForLng *= -1
            }
            val randomLatitude = (latitude + randomDeltaForLat).coerceAtMost(90.00)
            val randomLongitude = (longitude + randomDeltaForLng).coerceAtMost(180.00)
            nearbyFoodTruckLocations.add(LatLng(randomLatitude, randomLongitude))
        }

        return nearbyFoodTruckLocations
    }

    private fun getRandomFakePosition(latitude: Double, longitude: Double, size: Int): LatLng {
        val nearbyFoodTruckLocations = arrayListOf<LatLng>()
        for (i in 1..size) {
            val randomOperatorForLat = (0..1).random()
            val randomOperatorForLng = (0..1).random()
            var randomDeltaForLat = (10..50).random() / 10000.00
            var randomDeltaForLng = (10..50).random() / 10000.00
            if (randomOperatorForLat == 1) {
                randomDeltaForLat *= -1
            }
            if (randomOperatorForLng == 1) {
                randomDeltaForLng *= -1
            }
            val randomLatitude = (latitude + randomDeltaForLat).coerceAtMost(90.00)
            val randomLongitude = (longitude + randomDeltaForLng).coerceAtMost(180.00)
            nearbyFoodTruckLocations.add(LatLng(randomLatitude, randomLongitude))
        }

        return nearbyFoodTruckLocations[nearbyFoodTruckLocations.size - 1]
    }
}