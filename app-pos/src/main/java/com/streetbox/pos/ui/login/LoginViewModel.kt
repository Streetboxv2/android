package com.streetbox.pos.ui.login

import com.zeepos.domain.interactor.registration.RegistrationUseCase
import com.zeepos.domain.interactor.user.CheckSessionUserUseCase
import com.zeepos.domain.interactor.user.GetMerchantInfoUseCase
import com.zeepos.domain.interactor.user.InsertUpdateUserUseCase
import com.zeepos.domain.interactor.user.LoginPosUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.entities.RegisterParams
import com.zeepos.models.master.UserAuthData
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 5/1/20
 */
class LoginViewModel @Inject constructor(
    private val loginPosUseCase: LoginPosUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val checkSessionUserUseCase: CheckSessionUserUseCase,
    private val userRepository: UserRepository,
    private val insertUpdateUserUseCase: InsertUpdateUserUseCase,
    private val getMerchantInfoUseCase: GetMerchantInfoUseCase
) : BaseViewModel<LoginViewEvent>() {

    fun login(username: String, password: String) {
        val loginDisposable =
            loginPosUseCase.execute(LoginPosUseCase.Params(username, password))
                .subscribe(
                    {
                        handleLogin(it)
                    }, { error ->
                        viewEventObservable.postValue(
                            LoginViewEvent.LoginFailed(
                                error.message ?: "Login Failed"
                            )
                        )
                    }
                )
        addDisposable(loginDisposable)
    }

    fun loginOrRegister(registerParams: RegisterParams) {
        val disposable =
            registrationUseCase.execute(RegistrationUseCase.Params(registerParams))
                .subscribe(
                    {
                        handleLogin(it)
                    }, { error ->
                        viewEventObservable.postValue(
                            LoginViewEvent.LoginFailed(
                                error.message ?: "Login Failed"
                            )
                        )
                    }
                )
        addDisposable(disposable)
    }

    fun checkIsLoggedIn() {
        val userAuth = checkSessionUserUseCase.execute(None())
        userAuth?.let {
            handleLogin(it)
        }
    }


    private fun handleLogin(userAuth: UserAuthData) {
        when (userAuth.role_name) {
            ConstVar.USER_ROLE_MERCHANT -> {

                viewEventObservable.postValue(LoginViewEvent.GoToMainScreen)
            }
            ConstVar.USER_ROLE_OPERATOR -> {
//                getProfileMerchant()
                viewEventObservable.postValue(LoginViewEvent.GoToMainScreen)
            }
            ConstVar.USER_ROLE_CUSTOMER -> {
            }
            else -> {
            }
        }
    }


}
