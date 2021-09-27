package id.streetbox.live.ui.main.home.homevisit

import com.zeepos.domain.interactor.foodtruck.GetAllFoodTruckHomeVisitUseCase
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 6/14/20
 */
class HomeVisitViewModel @Inject constructor(
    private val getAllFoodTruckHomeVisitUseCase: GetAllFoodTruckHomeVisitUseCase
) : BaseViewModel<HomeVisitViewEvent>() {
    fun getHomeVisitFoodTruck(page: Int) {

        val disposable =
            getAllFoodTruckHomeVisitUseCase.execute(GetAllFoodTruckHomeVisitUseCase.Params(page))
                .subscribe({
                    viewEventObservable.postValue(HomeVisitViewEvent.GetFoodTruckHomeVisitSuccess(it))
                }, {
                    val errorMessage = it.message ?: "Failed to get data"
                    viewEventObservable.postValue(
                        HomeVisitViewEvent.GetFoodTruckHomeVisitFailed(
                            errorMessage
                        )
                    )
                })

        addDisposable(disposable)

    }
}