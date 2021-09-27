package com.zeepos.ui_splashscreen

import com.zeepos.domain.interactor.user.CheckSessionUserUseCase
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.master.UserAuthData
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 6/15/20
 */
class SplashScreenViewModel @Inject constructor(
    private val checkSessionUserUseCase: CheckSessionUserUseCase
) : BaseViewModel<SplashScreenViewEvent>() {
    fun checkUserSession() {
        val userAuth = checkSessionUserUseCase.execute(None())
        if (userAuth != null) {
            handleLogin(userAuth)
        } else {
            viewEventObservable.postValue(SplashScreenViewEvent.GoToLoginScreen)
        }
    }

    private fun handleLogin(userAuth: UserAuthData) {
        when (userAuth.role_name) {
            ConstVar.USER_ROLE_MERCHANT -> {
                viewEventObservable.postValue(SplashScreenViewEvent.GoToMainScreen)
            }
            ConstVar.USER_ROLE_OPERATOR -> {
                viewEventObservable.postValue(SplashScreenViewEvent.GoToOperatorMainScreen)
            }
            ConstVar.USER_ROLE_CUSTOMER -> {
                viewEventObservable.postValue(SplashScreenViewEvent.GoToMainScreen)
            }
        }
    }
}