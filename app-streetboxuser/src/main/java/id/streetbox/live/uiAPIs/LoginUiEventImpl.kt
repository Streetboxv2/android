package id.streetbox.live.uiAPIs

import android.app.Activity
import id.streetbox.live.ui.main.MainActivity
import com.zeepos.ui_login.LoginUiEvent
import javax.inject.Inject

/**
 * Created by Arif S. on 5/3/20
 */
class LoginUiEventImpl @Inject internal constructor() :
    LoginUiEvent {
    override fun goToMainScreen(activity: Activity) {
        activity.startActivity(MainActivity.getIntent(activity))
    }

    override fun goToOperatorFTScreen(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun goToForgotPasswordScreen(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun goToLoginScreen(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun goToOperatorMainScreen(activity: Activity) {
        TODO("Not yet implemented")
    }

}