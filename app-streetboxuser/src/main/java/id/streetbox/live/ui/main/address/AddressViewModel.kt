package id.streetbox.live.ui.main.address

import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddressViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    val userRepository: UserRepository
) : BaseViewModel<AddressViewEvent>() {
    fun callAddAddress(map: MutableMap<String?, Any?>) {
        val disposable = remoteRepository.callReqAddress(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(AddressViewEvent.OnSuccessAddAddress)
            }, {
                viewEventObservable.postValue(
                    AddressViewEvent.OnFailedAddress(it)
                )
            })
        addDisposable(disposable)
    }

    fun getUserLocal(): User? {
        return userRepository.getCurrentUser()
    }

    fun callUpdateLocation(map: MutableMap<String, Double>) {
        val disposable = remoteRepository.callApiUpdateLoc(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(AddressViewEvent.OnSuccessCallFoodTruck(it))
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }


    fun callUpdateAddress(map: MutableMap<String?, Any?>) {
        val disposable = remoteRepository.callUpdateAddress(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(AddressViewEvent.OnSuccessAddAddress)
            }, {
                viewEventObservable.postValue(
                    AddressViewEvent.OnFailedAddress(it)
                )
            })
        addDisposable(disposable)
    }

    fun callApiDeleteAddress(id: String) {
        val disposable = remoteRepository.callDeleteAddress(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(AddressViewEvent.OnSuccessAddress)
            }, {
                viewEventObservable.postValue(
                    AddressViewEvent.OnFailedAddress(it)
                )
            })
        addDisposable(disposable)
    }


    fun callApiPrimaryAddress(id: String) {
        val disposable = remoteRepository.callPrimaryAddress(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(AddressViewEvent.OnSuccessAddress)
            }, {
                viewEventObservable.postValue(
                    AddressViewEvent.OnFailedAddress(it)
                )
            })
        addDisposable(disposable)
    }


    fun callGetAddress() {
        val disposable = remoteRepository.callGetAddress()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(AddressViewEvent.OnSuccessListAddress(it))
            }, {
                viewEventObservable.postValue(
                    AddressViewEvent.OnFailedAddress(it)
                )
            })
        addDisposable(disposable)
    }
}