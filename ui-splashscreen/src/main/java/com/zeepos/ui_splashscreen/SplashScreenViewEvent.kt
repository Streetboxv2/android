package com.zeepos.ui_splashscreen

import com.zeepos.ui_base.ui.BaseViewEvent

/**
 * Created by Arif S. on 6/15/20
 */
sealed class SplashScreenViewEvent : BaseViewEvent {
    object GoToMainScreen : SplashScreenViewEvent()
    object GoToOperatorMainScreen : SplashScreenViewEvent()
    object GoToLoginScreen : SplashScreenViewEvent()
}