package id.streetbox.live.ui.editprofile

import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/12/20
 */
sealed class EditProfileViewEvent : BaseViewEvent {
    object UpdateUserSuccess : EditProfileViewEvent()
    object UpdateUserFailed : EditProfileViewEvent()
}