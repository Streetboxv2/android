package com.zeepos.ui_password


import com.zeepos.domain.interactor.ForgotPasswordUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<ForgotPasswordViewEvent>() {

    fun resetPassword(user_name: String) {
        val changePasswordDisposable =
            forgotPasswordUseCase.execute(ForgotPasswordUseCase.Params(user_name))
                .subscribe(
                    { value ->
                        if (value.isSuccess())
                            viewEventObservable.postValue(ForgotPasswordViewEvent.GetPasswordSuccess(value.data))
                        else
                            viewEventObservable.postValue(
                                ForgotPasswordViewEvent.GetPasswordFailed(
                                    value?.error?.message ?: "Login Failed"
                                )
                            )
                    },
                    { error ->
                        viewEventObservable.postValue(
                            ForgotPasswordViewEvent.GetPasswordFailed(
                                error.message ?: ""
                            )
                        )
                    }
                )
        addDisposable(changePasswordDisposable)
    }

    fun getUserLocal() : User?{
        return userRepository.getCurrentUser()
    }

}
