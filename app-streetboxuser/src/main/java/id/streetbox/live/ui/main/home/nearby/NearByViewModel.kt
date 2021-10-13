package id.streetbox.live.ui.main.home.nearby

import com.zeepos.domain.interactor.map.RequestNearbyFoodTruckUseCase
import com.zeepos.domain.interactor.user.GetUserInfoUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import com.zeepos.ui_base.ui.BaseViewModel
import id.streetbox.live.ui.orderreview.pickup.PickupOrderReviewViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Arif S. on 6/14/20
 */
class NearByViewModel @Inject constructor(
    private val nearbyFoodTruckUseCase: RequestNearbyFoodTruckUseCase,
    val remoteRepository: RemoteRepository,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel<NearByViewEvent>() {
    fun getNearByFoodTruck(lat: Double, lng: Double, page: Int, distance: String) {
        val disposable =
            nearbyFoodTruckUseCase.execute(
                RequestNearbyFoodTruckUseCase.Params(
                    ConstVar.MAP_NEARBY_CHECK_IN,
                    lat,
                    lng, page, distance
                )
            )
                .map {
                    val foodTruckList: MutableList<FoodTruck> = arrayListOf()
                    for (mapData in it) {
                        val foodTruck = mapData.exData as FoodTruck
                        foodTruckList.add(foodTruck)
                    }
                    foodTruckList
                }
                .subscribe({
                    viewEventObservable.postValue(NearByViewEvent.GetNearBySuccess(it))

                }, {
                    viewEventObservable.postValue(NearByViewEvent.GetNearByFailed)
                    it.printStackTrace()
                })
        addDisposable(disposable)

    }



    fun getDistanceKm() {
        val disposable = remoteRepository.callGetDistance()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(NearByViewEvent.GetSuccessDistanceKm(it))
            }, {
                viewEventObservable.postValue(
                    NearByViewEvent.GetFailedDistanceKm(it)
                )
            })
        addDisposable(disposable)
    }
}