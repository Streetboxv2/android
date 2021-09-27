package id.streetbox.live.ui.main.home.homevisit

import com.zeepos.models.master.FoodTruck
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/14/20
 */
sealed class HomeVisitViewEvent : BaseViewEvent {
    data class GetFoodTruckHomeVisitSuccess(val data: List<FoodTruck>) : HomeVisitViewEvent()
    data class GetFoodTruckHomeVisitFailed(val errorMessage: String) : HomeVisitViewEvent()
}