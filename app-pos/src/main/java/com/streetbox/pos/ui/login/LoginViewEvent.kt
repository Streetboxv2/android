package com.streetbox.pos.ui.login

import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 5/1/20
 */
sealed class LoginViewEvent : BaseViewEvent {
    object GoToMainScreen : LoginViewEvent()
    object GoToOperatorMainScreen : LoginViewEvent()
    object DismissLoading : LoginViewEvent()
    data class LoginFailed(val errorMessage: String) : LoginViewEvent()

}