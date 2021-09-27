package com.zeepos.map.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.zeepos.domain.interactor.CheckFreeTaskUseCase
import com.zeepos.domain.interactor.CheckInHomeVisitUseCase
import com.zeepos.domain.interactor.CheckUseCase
import com.zeepos.domain.interactor.UndoUseCase
import com.zeepos.domain.interactor.map.*
import com.zeepos.domain.interactor.parkingsales.GetParkingOperatorTaskUseCase
import com.zeepos.domain.interactor.parkingspace.GetMerchantParkingScheduleUseCase
import com.zeepos.domain.interactor.parkingspace.GetParkingSpaceScheduleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.map.utils.MapUtils
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.MapData
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.models.entities.Schedule
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/22/20
 */
class MapViewModel @Inject constructor(
    private val nearbyFoodTruckUseCase: RequestNearbyFoodTruckUseCase,
    private val requestNearbyFoodTruckMapUseCase: RequestNearbyFoodTruckMapUseCase,
    private val requestLiveTrackingUseCase: RequestLiveTrackingUseCase,
    private val requestDirectionUseCase: RequestDirectionUseCase,
    private val getParkingOperatorTaskUseCase: GetParkingOperatorTaskUseCase,
    private val updateCurrentLocationToCloud: UpdateCurrentLocationToCloud,
    private val scheduledGetAllLocationFromServerUseCase: ScheduleGetAllLocationFromServerUseCase,
    private val scheduleGetLocationFomServerUseCase: ScheduleGetLocationFomServerUseCase,
    private val getCurrentFoodTruckCheckInUseCase: GetCurrentFoodTruckCheckInUseCase,
    private val checkUseCase: CheckUseCase,
    private val checkInHomeVisitUseCase: CheckInHomeVisitUseCase,
    private val checkFreeTaskUseCase: CheckFreeTaskUseCase,
    private val taskOperatorRepo: TaskOperatorRepo,
    private val undoUseCase: UndoUseCase,
    private val getParkingSpaceScheduleUseCase: GetParkingSpaceScheduleUseCase,
    private val getMerchantParkingScheduleUseCase: GetMerchantParkingScheduleUseCase,
    val remoteRepository: RemoteRepository
) : BaseViewModel<MapViewEvent>() {

    val parkingScheduleObs: MutableLiveData<List<ParkingSchedule>> = MutableLiveData()
    val merchantScheduleObs: MutableLiveData<List<Schedule>> = MutableLiveData()

    fun getCurrentTaskDirection(taskId: Long, latitude: Double, longitude: Double) {
        val disposable =
            requestDirectionUseCase.execute(
                RequestDirectionUseCase.Params(taskId, latitude, longitude)
            )
                .subscribe({
                    viewEventObservable.value = MapViewEvent.ShowPath(it)
                }, {
                    it.printStackTrace()
                })
        addDisposable(disposable)
    }

    fun requestDirection(taskId: Long, currentLat: Double, currentLng: Double) {
        val disposable = requestDirectionUseCase.execute(
            RequestDirectionUseCase.Params(taskId, currentLat, currentLng)
        )
            .subscribe({
                viewEventObservable.value = MapViewEvent.ShowPath(it)
//                fakeScheduledGetLocationFromServer(it)
            }, {
                it.printStackTrace()
            }, {
                viewEventObservable.value = MapViewEvent.InformTripStart
            })

        addDisposable(disposable)
    }

    fun requestNearbyFoodTruck(latLng: LatLng, distance: String) {
        val disposable = requestNearbyFoodTruckMapUseCase.execute(
            RequestNearbyFoodTruckMapUseCase.Params(
                latLng.latitude,
                latLng.longitude,
                distance
            )
        )
            .subscribe({
                viewEventObservable.postValue(MapViewEvent.ShowNearbyFoodTrucks(it))
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }

    fun getDistanceKm() {
        val disposable = remoteRepository.callGetDistance()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(MapViewEvent.GetSuccessDistanceKm(it))
            }, {
                viewEventObservable.postValue(
                    MapViewEvent.GetFailedDistanceKm(it)
                )
            })
        addDisposable(disposable)
    }

    fun checkInOperatorTask(
        latitude: Double,
        longitude: Double,
        parkingSpaceName: String,
        tasksId: Long,
        typesId: Long
    ) {
        addDisposable(
            checkUseCase.execute(
                CheckUseCase.Params(
                    latitude,
                    longitude,
                    parkingSpaceName,
                    tasksId,
                    typesId
                )
            )
                .subscribe(
                    {
                        updateLocationToCloud(
                            tasksId,
                            latitude,
                            longitude,
                            ConstVar.TASK_STATUS_ARRIVED
                        )

                        viewEventObservable.postValue(
                            MapViewEvent.GetCheckInSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                MapViewEvent.GetCheckInFailed(it)
                            )
                        }
                    }
                )
        )
    }


    fun checkInOperatorTaskHomeVisit(
        customerName: String,
        latitude: Double,
        longitude: Double,
        tasksId: Long,
        typesId: Long
    ) {
        addDisposable(
            checkInHomeVisitUseCase.execute(
                CheckInHomeVisitUseCase.Params(
                    customerName,
                    latitude,
                    longitude,
                    tasksId,
                    typesId
                )
            )
                .subscribe(
                    {
                        updateLocationToCloud(
                            tasksId,
                            latitude,
                            longitude,
                            ConstVar.TASK_STATUS_ARRIVED
                        )

                        viewEventObservable.postValue(
                            MapViewEvent.GetCheckInHomeVisitSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                MapViewEvent.GetCheckInHomeVisitFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun checkInOperatorFreeTask(
        address: String,
        latitude: Double,
        longitude: Double,
        tasksId: Long
    ) {
        addDisposable(
            checkFreeTaskUseCase.execute(
                CheckFreeTaskUseCase.Params(
                    address,
                    latitude,
                    longitude,
                    tasksId
                )
            )
                .subscribe(
                    {
                        updateLocationToCloud(
                            tasksId,
                            latitude,
                            longitude,
                            ConstVar.TASK_STATUS_ARRIVED
                        )
                        viewEventObservable.postValue(
                            MapViewEvent.GetCheckInFreeTaskSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                MapViewEvent.GetCheckInFreeTaskFailed(it)
                            )
                        }
                    }
                )
        )
    }


    fun updateLocationToCloud(taskId: Long, latitude: Double, longitude: Double, status: Int = 0) {
        val disposable = updateCurrentLocationToCloud.execute(
            UpdateCurrentLocationToCloud.Params(
                taskId,
                latitude,
                longitude,
                status
            )
        )
            .subscribe({
                Log.d(ConstVar.TAG, "Update status success!")
//                updateCurrentFoodTruckPosition(
//                    taskId,
//                    latitude,
//                    longitude
//                )
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }

    fun scheduledGetAllNearByLatestLocationFromServer(latitude: Double, longitude: Double) {
        val disposable = scheduledGetAllLocationFromServerUseCase.execute(
            ScheduleGetAllLocationFromServerUseCase.Params(latitude, longitude)
        )
            .subscribe(
                {
                    if (it.latLng != null)
                        viewEventObservable.value = MapViewEvent.UpdateFoodTruckLocation(it)
                }, {
                    it.printStackTrace()
                    scheduledGetAllNearByLatestLocationFromServer(latitude, longitude)//call again
                }, {
                    Log.d(ConstVar.TAG, "complete")
                }
            )

        addDisposable(disposable)
    }

    fun scheduledGetLatestLocationFromServer(taskId: Long) {
        val disposable = scheduleGetLocationFomServerUseCase.execute(
            ScheduleGetLocationFomServerUseCase.Params(taskId)
        )
            .subscribe(
                {
                    if (it.latLng != null) {
                        viewEventObservable.value = MapViewEvent.UpdateFoodTruckLocation(it)
                    }

                    val taskOperator = it.exData as TaskOperator
                    if (taskOperator.status == ConstVar.TASK_STATUS_ARRIVED) {
                        viewEventObservable.postValue(MapViewEvent.InformFoodTruckArrived)
                    }
                }, {
                    it.printStackTrace()
                }, {
                    Log.d(ConstVar.TAG, "get latest location complete")
                }
            )

        addDisposable(disposable)
    }

    fun getCurrentFoodTruckCheckInLocationFromServer(taskId: Long) {
        val disposable = getCurrentFoodTruckCheckInUseCase.execute(
            GetCurrentFoodTruckCheckInUseCase.Params(taskId)
        )
            .subscribe(
                {
                    if (it.latLng != null) {
                        viewEventObservable.postValue(MapViewEvent.UpdateFoodTruckLocation(it))
                    }
                }, {
                    it.printStackTrace()
                }, {
                    Log.d(ConstVar.TAG, "get latest location complete")
                }
            )

        addDisposable(disposable)
    }

    fun informTripEndToServer(taskId: Long) {
        val disposable = scheduleGetLocationFomServerUseCase.execute(
            ScheduleGetLocationFomServerUseCase.Params(taskId)
        )
            .subscribe(
                {
                    if (it.latLng != null)
                        viewEventObservable.value = MapViewEvent.UpdateFoodTruckLocation(it)
                }, {
                    it.printStackTrace()
                }, {
                    Log.d(ConstVar.TAG, "complete")
                }
            )

        addDisposable(disposable)
    }

    fun updateCurrentFoodTruckPosition(taskId: Long, currentLat: Double, currentLng: Double) {
        val taskOperator = taskOperatorRepo.get(taskId)

        taskOperator?.let {
            val mapData = MapData(
                ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION
            )
            mapData.latLng = com.google.maps.model.LatLng(currentLat, currentLng)
            mapData.exData = taskOperator

            viewEventObservable.value = MapViewEvent.UpdateFoodTruckLocation(mapData)

            val distance =
                MapUtils.getDistance(currentLat, currentLng, it.latParkingSpace, it.lonParkingSpace)

            Log.d(ConstVar.TAG, "distance $distance meters")

            if (distance < 50)
                viewEventObservable.value = MapViewEvent.InformTripEnd
        }
    }

    fun updateFreeTaskCurrentFoodTruckPosition(
        taskId: Long,
        currentLat: Double,
        currentLng: Double
    ) {
        var taskOperator = taskOperatorRepo.get(taskId)

        if (taskOperator == null) {
            val mTaskOperator = TaskOperator()
            mTaskOperator.tasksId = taskId
            taskOperator = mTaskOperator
        }

        taskOperator?.let {
            val mapData = MapData(
                ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION
            )
            mapData.latLng = com.google.maps.model.LatLng(currentLat, currentLng)
            mapData.exData = taskOperator

            viewEventObservable.value = MapViewEvent.UpdateFoodTruckLocation(mapData)

        }
    }

    fun undo(tasksId: Long) {
        addDisposable(
            undoUseCase.execute(UndoUseCase.Params(tasksId))
                .subscribe(
                    {
                        viewEventObservable.postValue(
                            MapViewEvent.UndoSuccess(it.toString())
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                MapViewEvent.UndoFailed(it)
                            )
                        }
                    }
                )
        )
    }

    private fun fakeScheduledGetLocationFromServer(mapData: MapData) {
        val latLngList = mapData.latLngList
        val disposable = Observable.fromIterable(latLngList)
            .subscribeOn(Schedulers.io())
            .concatMap {
                val delay: Int = Random().nextInt(10)
                Observable.just(it)
                    .delay(delay.toLong(), TimeUnit.SECONDS)
                    .map { latLng ->
                        val mapDataTmp = MapData(
                            ConstVar.MAP_ACTION_TYPE_TRIP_LOCATION
                        )
                        mapData.uniqueId = mapData.uniqueId
                        mapDataTmp.latLng = latLng
                        mapDataTmp.exData = mapData.exData
                        mapDataTmp
                    }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.value = MapViewEvent.UpdateFoodTruckLocation(it)
            }, { it.printStackTrace() }, {
                viewEventObservable.value = MapViewEvent.InformTripEnd
            })

        addDisposable(disposable)
    }

    fun getParkingSchedule(parkingSpaceId: Long) {
        val disposable = getParkingSpaceScheduleUseCase.execute(
            GetParkingSpaceScheduleUseCase.Params(parkingSpaceId)
        )
            .subscribe({
                parkingScheduleObs.postValue(it)
            }, {
                it.message?.let {
                    viewEventObservable.postValue(MapViewEvent.GetParkingSpaceScheduleFailed(it))
                }
            })
        addDisposable(disposable)
    }

    fun getMerchantParkingSchedule(merchantId: Long, typesId: Long) {
        val disposable = getMerchantParkingScheduleUseCase.execute(
            GetMerchantParkingScheduleUseCase.Params(merchantId, typesId)
        )
            .subscribe({
                merchantScheduleObs.postValue(it)
            }, {
                it.message?.let { errorMessage ->
                    viewEventObservable.postValue(
                        MapViewEvent.GetParkingSpaceScheduleFailed(
                            errorMessage
                        )
                    )
                }
            })
        addDisposable(disposable)
    }

}