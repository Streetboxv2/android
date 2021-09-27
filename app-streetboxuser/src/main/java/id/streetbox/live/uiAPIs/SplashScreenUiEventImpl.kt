package id.streetbox.live.uiAPIs

import android.app.Activity
import android.content.Intent
import id.streetbox.live.ui.firsttimescreen.FirstTimeActivity
import id.streetbox.live.ui.main.MainActivity
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
    }

    override fun goToLoginScreen(activity: Activity) {
        activity.startActivity(LoginActivity.getIntent(activity))
    }

    override fun goToOperatorHomeScreen(activity: Activity) {
    }

    override fun goToFirstTimeAppScreen(activity: Activity) {
        activity.startActivity(FirstTimeActivity.getIntent(activity))
    }

    override fun goToMainActivity(activity: Activity, typeNotif: String): Intent {
        val intent = intentPageData(activity, MainActivity::class.java)
            .putExtra("typeNotif", typeNotif)
        activity.startActivity(intent)
        return intent
    }
}