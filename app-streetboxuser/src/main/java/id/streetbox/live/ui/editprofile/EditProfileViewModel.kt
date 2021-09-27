package id.streetbox.live.ui.editprofile

import com.zeepos.domain.interactor.user.UpdateUserUseCase
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import java.io.File
import javax.inject.Inject

/**
 * Created by Arif S. on 6/12/20
 */
class EditProfileViewModel @Inject constructor(
    private val updateUserUseCase: UpdateUserUseCase
) : BaseViewModel<EditProfileViewEvent>() {

    fun updateUser(user: User, file: File? = null) {
        val disposable = updateUserUseCase.execute(UpdateUserUseCase.Params(user, file))
            .subscribe({
                viewEventObservable.postValue(EditProfileViewEvent.UpdateUserSuccess)
            }, {
                viewEventObservable.postValue(EditProfileViewEvent.UpdateUserFailed)
            })

        addDisposable(disposable)
    }
}