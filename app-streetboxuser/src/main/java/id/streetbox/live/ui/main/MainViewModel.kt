package id.streetbox.live.ui.main

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.zeepos.domain.interactor.GetTaxUseCase
import com.zeepos.domain.interactor.SendTokenUser
import com.zeepos.domain.interactor.profile.GetAllProfileUseCase
import com.zeepos.domain.interactor.user.GetMerchantInfoUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 6/9/20
 */
class MainViewModel @Inject constructor(
    private val sendTokenUser: SendTokenUser,
    private val getMerchantInfoUseCase: GetMerchantInfoUseCase,
    private val getAllProfileUseCase: GetAllProfileUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<MainViewEvent>() {
    val location: MutableLiveData<Location> = MutableLiveData()

    fun sendToken(token: String) {
        val disposable = sendTokenUser.execute(
            SendTokenUser.Params(
                token
            )
        )
            .subscribe({
                Log.d(ConstVar.TAG, "Send Token success!")
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }

    fun getUserLocal(): User? {
        return userRepository.getCurrentUser()
    }

}