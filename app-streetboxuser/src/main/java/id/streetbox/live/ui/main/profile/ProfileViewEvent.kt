package id.streetbox.live.ui.main.profile

import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/12/20
 */
sealed class ProfileViewEvent : BaseViewEvent {
    data class GetUserInfoSuccess(val user: User) : ProfileViewEvent()
    data class GetUserInfoFailed(val errorMessage: String) : ProfileViewEvent()
}