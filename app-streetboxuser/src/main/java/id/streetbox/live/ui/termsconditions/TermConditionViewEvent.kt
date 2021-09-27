package id.streetbox.live.ui.termsconditions

import com.zeepos.models.response.ResponseTermCondition
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/12/20
 */
sealed class TermConditionViewEvent : BaseViewEvent {
    data class OnSuccessTermCondition(val responseTermCondition: ResponseTermCondition) :
        TermConditionViewEvent()

    data class OnFailedTermCondition(val throwable: Throwable) : TermConditionViewEvent()
}