package com.zeepos.recruiter.ui.main.profile

import com.zeepos.domain.interactor.ChangePasswordUseCase
import com.zeepos.domain.interactor.profile.GetAllProfileUseCase
import com.zeepos.domain.interactor.user.CheckSessionUserUseCase
import com.zeepos.domain.interactor.user.GetUserUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.entities.None
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val getUserUseCase: GetUserUseCase,
    private val userRepository: UserRepository,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val checkSessionUserUseCase: CheckSessionUserUseCase,
    private val getAllProfileUseCase: GetAllProfileUseCase
) : BaseViewModel<ProfileViewEvent>() {
    fun getProfile() {
        val profileDisposable = getAllProfileUseCase.execute(None())
            .subscribe({ values ->
                handleCallback(ProfileViewEvent.GetProfileSuccess(values.data))
            }, { error ->
                handleCallback(ProfileViewEvent.GetProfileFailed(error.message))
            })

        addDisposable(profileDisposable)
    }

    private fun handleCallback(useCase: ProfileViewEvent) {
        when (useCase) {
            is ProfileViewEvent.GetProfileSuccess -> {
                useCase.data?.let {
                    viewEventObservable.postValue(useCase)
                }
            }
            is ProfileViewEvent.GetProfileFailed -> {
                useCase.message?.let {
                    viewEventObservable.postValue(useCase)
                }
            }
        }
    }

    fun changePassword(password: String) {
        val changePasswordDisposable =
            changePasswordUseCase.execute(ChangePasswordUseCase.Params(password))
                .subscribe(
                    { value ->
                        if (value.isSuccess())
                            viewEventObservable.postValue(ProfileViewEvent.GetProfileSuccess(value.data))
                        else
                            viewEventObservable.postValue(
                                ProfileViewEvent.GetProfileFailed(
                                    value?.error?.message ?: "Login Failed"
                                )
                            )
                    },
                    { error ->
                        viewEventObservable.postValue(
                            ProfileViewEvent.GetProfileFailed(
                                error.message ?: ""
                            )
                        )
                    }
                )
        addDisposable(changePasswordDisposable)
    }


    fun getUserLocal() :User?{
        return userRepository.getCurrentUserRecruiter()
    }

    fun deleteSession(){
        localPreferencesRepository.deleteSession()
    }


}