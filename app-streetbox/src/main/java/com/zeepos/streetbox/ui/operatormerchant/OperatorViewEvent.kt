package com.zeepos.streetbox.ui.operatormerchant

import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/16/20
 */
sealed class OperatorViewEvent : BaseViewEvent {
    data class GetOperatorSuccess(val data: List<User>?) : OperatorViewEvent()
    data class GetOperatorFailed(val message: String?) : OperatorViewEvent()
    object CreateTaskOperatorSuccess : OperatorViewEvent()
    object CreateTaskOperatorHomeVisitSuccess : OperatorViewEvent()
    data class CreateTaskOperatorFailed(val errorMessage: String) : OperatorViewEvent()
    data class CreateTaskOperatorHomeVisitFailed(val errorMessage: String) : OperatorViewEvent()
}