package com.zeepos.streetbox.ui.main.profile

import android.util.Log
import com.zeepos.domain.interactor.ChangePasswordUseCase
import com.zeepos.domain.interactor.profile.GetAllProfileUseCase
import com.zeepos.domain.interactor.syncdata.DownloadReportUseCase
import com.zeepos.domain.interactor.user.CheckSessionUserUseCase
import com.zeepos.domain.interactor.user.GetMerchantInfoUseCase
import com.zeepos.domain.interactor.user.GetUserUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
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
    private val getAllProfileUseCase: GetAllProfileUseCase,
    private val getMerchantInfoUseCase: GetMerchantInfoUseCase,
    private val downloadReportUseCase: DownloadReportUseCase
) : BaseViewModel<ProfileViewEvent>() {
//    fun getProfile() {
//        val profileDisposable = getAllProfileUseCase.execute(None())
//            .subscribe({ values ->
//                handleCallback(ProfileViewEvent.GetProfileSuccess(values.data))
//            }, { error ->
//                handleCallback(ProfileViewEvent.GetProfileFailed(error.message))
//            })
//
//        addDisposable(profileDisposable)
//    }

//    private fun handleCallback(useCase: ProfileViewEvent) {
//        when (useCase) {
//            is ProfileViewEvent.GetProfileSuccess -> {
//                useCase.data?.let {
//                    viewEventObservable.postValue(useCase)
//                }
//            }
//            is ProfileViewEvent.GetProfileFailed -> {
//                useCase.message?.let {
//                    viewEventObservable.postValue(useCase)
//                }
//            }
//        }
//    }

    fun getUser(): User? {
        return userRepository.getCurrentUser()
    }

    fun changePassword(password: String) {
        val changePasswordDisposable =
            changePasswordUseCase.execute(ChangePasswordUseCase.Params(password))
                .subscribe(
                    { value ->
                        if (value.isSuccess())
                            viewEventObservable.postValue(
                                ProfileViewEvent.GetChangePassword(
                                    value?.error?.message ?: "Login Success"
                                )
                            )
                        else
                            viewEventObservable.postValue(
                                ProfileViewEvent.GetChangePasswordFailed(
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


    fun getUserLocal(): User? {
        return userRepository.getCurrentUser()
    }

    fun getMerchantProfile() {
        val disposable = getMerchantInfoUseCase.execute(None())
            .subscribe({
                if (it.isSuccess()) {
                    val user = it.data!!
                    viewEventObservable.postValue(ProfileViewEvent.GetProfileSuccess(user))
                } else {
                    viewEventObservable.postValue(ProfileViewEvent.GetProfileFailed("Get Profile Failed!"))
                }
            }, {
                viewEventObservable.postValue(ProfileViewEvent.GetProfileFailed(it.message))
            })
        addDisposable(disposable)
    }

    fun deleteSession() {
        localPreferencesRepository.deleteSession()
    }

    fun downloadReport(month: String, year: String, url: String) {
        val disposable = downloadReportUseCase.execute(DownloadReportUseCase.Params(month, year, url))
            .subscribe({
                Log.d(ConstVar.TAG, "success $it")
                viewEventObservable.postValue(ProfileViewEvent.DownloadReportSuccess(it))
            }, {
                it.printStackTrace()
                viewEventObservable.postValue(ProfileViewEvent.DownloadReportFailed)
            })
        addDisposable(disposable)
    }


}