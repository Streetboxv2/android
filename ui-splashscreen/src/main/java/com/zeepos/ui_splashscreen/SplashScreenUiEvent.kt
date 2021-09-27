package com.zeepos.ui_splashscreen

import android.app.Activity
import android.content.Intent

/**
 * Created by Arif S. on 6/25/20
 */
interface SplashScreenUiEvent {
    fun goToMainScreen(activity: Activity)
    fun goToOperatorMainScreen(activity: Activity)
    fun goToLoginScreen(activity: Activity)
    fun goToOperatorHomeScreen(activity: Activity)
    fun goToFirstTimeAppScreen(activity: Activity)
    fun goToMainActivity(activity: Activity, typenotif: String): Intent
}