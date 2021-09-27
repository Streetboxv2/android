package com.zeepos.streetbox.ui.main

import android.util.Log
import com.zeepos.domain.interactor.SendTokenUseCase
import com.zeepos.domain.interactor.user.GetUserTokenUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 5/3/20
 */
class MainViewModel @Inject constructor(
    private val getUserTokenUseCase: GetUserTokenUseCase,
    private val sendTokenUseCase: SendTokenUseCase,
    val userRepository: UserRepository
) :
    BaseViewModel<MainViewEvent>() {

    fun getCurrentUserToken(): String {
        return getUserTokenUseCase.execute(None()) ?: ConstVar.EMPTY_STRING
    }

    fun getUser(): User? {
        return userRepository.getCurrentUser()
    }


    fun sendToken(token: String) {
        val disposable = sendTokenUseCase.execute(
            SendTokenUseCase.Params(
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
}