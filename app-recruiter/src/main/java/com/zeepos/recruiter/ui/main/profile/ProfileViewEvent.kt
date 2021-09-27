package com.zeepos.recruiter.ui.main.profile

import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent

sealed class ProfileViewEvent : BaseViewEvent {
    data class GetProfileSuccess(val data: User?) : ProfileViewEvent()
    data class GetProfileFailed(val message: String?) : ProfileViewEvent()
    data class GetUserId(val userId: String) : ProfileViewEvent()
}