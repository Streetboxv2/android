package id.streetbox.live.ui.bookhomevisit

import com.zeepos.models.entities.AvailableHomeVisitBookDate
import com.zeepos.models.master.Address
import com.zeepos.models.response.ResponseAddressPrimary
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/25/20
 */
sealed class BookHomeVisitViewEvent : BaseViewEvent {
    data class GetFoodTruckHomeVisitDataSuccess(val data: List<AvailableHomeVisitBookDate>) :
        BookHomeVisitViewEvent()

    data class OnFailedAddress(val throwable: Throwable) : BookHomeVisitViewEvent()

    data class OnSuccessListAddress(val responseListAddress: ResponseAddressPrimary) :
        BookHomeVisitViewEvent()

    data class GetFoodTruckHomeVisitDataFailed(val errorMessage: String) :
        BookHomeVisitViewEvent()

    data class OnCalculateDone(val totalDeposit: Double) : BookHomeVisitViewEvent()

    data class GetAllAddressSuccess(val addressList: List<Address>) : BookHomeVisitViewEvent()
}