package com.zeepos.ui_password

import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent
import com.zeepos.ui_login.LoginViewEvent

sealed class ForgotPasswordViewEvent : BaseViewEvent {
    data class GetPasswordSuccess(val data: User?) : ForgotPasswordViewEvent()
    data class GetPasswordFailed(val message: String?) : ForgotPasswordViewEvent()
}