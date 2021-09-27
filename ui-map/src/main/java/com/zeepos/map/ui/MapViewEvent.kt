package com.zeepos.map.ui

import android.os.Bundle
import com.zeepos.models.entities.MapData
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.models.master.Address
import com.zeepos.models.master.Check
import com.zeepos.models.master.OperatorTask
import com.zeepos.models.response.ResponseDistanceKm
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/22/20
 */
sealed class MapViewEvent : BaseViewEvent {
    data class ShowNearbyFoodTrucks(val mapDataList: List<MapData>) : MapViewEvent()

    object InformCabBooked : MapViewEvent()

    data class ShowPath(val mapData: MapData) : MapViewEvent()

    data class UpdateFoodTruckLocation(val mapData: MapData) : MapViewEvent()

    object InformFoodTruckIsArriving : MapViewEvent()

    object InformFoodTruckArrived : MapViewEvent()

    object InformTripStart : MapViewEvent()

    object InformLiveTrackStart : MapViewEvent()

    object InformTripEnd : MapViewEvent()

    object ShowRoutesNotAvailableError : MapViewEvent()

    data class ShowDirectionApiFailedError(val error: String) : MapViewEvent()

    data class GetAllParkingOperatorTaskSuccess(val data: MutableList<OperatorTask>) :
        MapViewEvent()

    data class GetAllParkingOperatorTaskFailed(val errorMessage: String) : MapViewEvent()

    data class GetCheckInSuccess(val data: Check) : MapViewEvent()
    data class GetCheckInFailed(val message: String?) : MapViewEvent()
    data class GoToMerchantMenuScreen(val merchantId: Long, val bundle: Bundle) : MapViewEvent()
    data class GetCheckInFreeTaskSuccess(val data: Check) : MapViewEvent()
    data class GetCheckInFreeTaskFailed(val message: String?) : MapViewEvent()
    data class GetCheckInHomeVisitSuccess(val data: Check) : MapViewEvent()
    data class GetCheckInHomeVisitFailed(val message: String?) : MapViewEvent()
    data class UndoSuccess(val data: String) : MapViewEvent()
    data class UndoFailed(val message: String?) : MapViewEvent()
    data class GetParkingSpaceScheduleSuccess(val parkingScheduleList: List<ParkingSchedule>) :
        MapViewEvent()

    data class GetParkingSpaceScheduleFailed(val errorMessage: String?) : MapViewEvent()
    data class OnSelectedAddress(val address: Address) : MapViewEvent()
    data class GetSuccessDistanceKm(val dataDistance: ResponseDistanceKm) : MapViewEvent()
    data class GetFailedDistanceKm(val throwable: Throwable) : MapViewEvent()
}