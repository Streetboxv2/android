package com.zeepos.ui_login

import android.app.Activity

interface LoginUiEvent {
    fun goToMainScreen(activity: Activity)
    fun goToOperatorFTScreen(activity: Activity)
    fun goToForgotPasswordScreen(activity: Activity)
    fun goToLoginScreen(activity: Activity)
    fun goToOperatorMainScreen(activity: Activity)
}
