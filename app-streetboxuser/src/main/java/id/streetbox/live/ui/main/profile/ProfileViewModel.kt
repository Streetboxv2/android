package id.streetbox.live.ui.main.profile

import com.zeepos.domain.interactor.user.GetUserInfoUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import id.streetbox.live.ui.main.doortodoor.DoortoDoorViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Arif S. on 6/12/20
 */
class ProfileViewModel @Inject constructor(
    val remoteRepository: RemoteRepository,
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val orderRepo: OrderRepo,
    val userRepository: UserRepository
) : BaseViewModel<ProfileViewEvent>() {
    fun deleteSession() {
        localPreferencesRepository.deleteSession()
        orderRepo.removeAll()
    }

    fun getUserInfoCloud(id: Long = 0) {
        val disposable = getUserInfoUseCase.execute(GetUserInfoUseCase.Params(id))
            .subscribe({
                viewEventObservable.postValue(ProfileViewEvent.GetUserInfoSuccess(it))
            }, {
                it.message?.let { errorMessage ->
                    viewEventObservable.postValue(ProfileViewEvent.GetUserInfoFailed(errorMessage))
                }
            })
        addDisposable(disposable)
    }

    fun callGetListNotif() {
        val disposable = remoteRepository.callListNotifBlast()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(ProfileViewEvent.OnSuccessListNotif(it))
            }, {
                viewEventObservable.postValue(
                    ProfileViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun getUserLocal(): User? {
        return userRepository.getCurrentUser()
    }
}