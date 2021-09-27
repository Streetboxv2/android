package com.zeepos.streetbox.uiAPIs

import android.app.Activity
import android.content.Intent
import com.zeepos.streetbox.ui.main.MainActivity
import com.zeepos.streetbox.ui.operator.main.OperatorFTActivity
import com.zeepos.streetbox.ui.operator.main.OperatorMainActivity
import com.zeepos.ui_login.LoginActivity
import com.zeepos.ui_splashscreen.SplashScreenUiEvent
import com.zeepos.utilities.intentPageData
import javax.inject.Inject

/**
 * Created by Arif S. on 6/25/20
 */
class SplashScreenUiEventImpl @Inject internal constructor() : SplashScreenUiEvent {
    override fun goToMainScreen(activity: Activity) {
        activity.startActivity(MainActivity.getIntent(activity))
    }

    override fun goToOperatorMainScreen(activity: Activity) {
        activity.startActivity(OperatorFTActivity.getIntent(activity))
    }

    override fun goToLoginScreen(activity: Activity) {
        activity.startActivity(LoginActivity.getIntent(activity))
    }

    override fun goToOperatorHomeScreen(activity: Activity) {
        activity.startActivity(OperatorMainActivity.getIntent(activity, 0.0, 0.0, 0))
    }

    override fun goToFirstTimeAppScreen(activity: Activity) {

    }

    override fun goToMainActivity(activity: Activity, typenotif: String): Intent {
        val intent = intentPageData(activity, MainActivity::class.java)
        activity.startActivity(MainActivity.getIntent(activity))
        return intent
    }

}