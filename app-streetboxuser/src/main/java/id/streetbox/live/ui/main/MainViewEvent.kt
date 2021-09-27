package id.streetbox.live.ui.main

import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/9/20
 */
sealed class MainViewEvent : BaseViewEvent {
    data class GetProfileSuccess(val data: User) : BaseViewEvent
    data class GetProfileFailed(val message: String?) : BaseViewEvent
}