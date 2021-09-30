package id.streetbox.live.ui.main.profile

import com.zeepos.domain.interactor.user.GetUserInfoUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 6/12/20
 */
class ProfileViewModel @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val orderRepo: OrderRepo
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
}