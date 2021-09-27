package com.zeepos.streetbox.uiAPIs

import android.app.Activity
import androidx.fragment.app.Fragment
import com.zeepos.streetbox.ui.main.MainActivity
import com.zeepos.streetbox.ui.operator.main.OperatorFTActivity
import com.zeepos.streetbox.ui.operator.main.OperatorMainActivity
import com.zeepos.ui_login.LoginActivity
import com.zeepos.ui_login.LoginUiEvent
import com.zeepos.ui_password.ForgotPasswordActivity
import javax.inject.Inject

/**
 * Created by Arif S. on 5/3/20
 */
class LoginUiEventImpl @Inject internal constructor() :
    LoginUiEvent {
    override fun goToMainScreen(activity: Activity) {
        activity.startActivity(MainActivity.getIntent(activity))
    }

    override fun goToForgotPasswordScreen(activity: Activity) {
        activity.startActivity(ForgotPasswordActivity.getIntent(activity))
    }

    override fun goToLoginScreen(activity: Activity) {
        activity.startActivity(LoginActivity.getIntent(activity))
    }

    override fun goToOperatorMainScreen(activity: Activity) {
        activity.startActivity(OperatorMainActivity.getIntent(activity,0.0,0.0,0))
    }

    override fun goToOperatorFTScreen(activity: Activity) {
        activity.startActivity(OperatorFTActivity.getIntent(activity))
    }

}