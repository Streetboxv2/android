package id.streetbox.live.ui.main.doortodoor

import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import id.streetbox.live.ui.bookhomevisit.BookHomeVisitViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DoortoDoorViewModel @Inject constructor(
    val remoteRepository: RemoteRepository,
    val userRepository: UserRepository
) : BaseViewModel<DoortoDoorViewEvent>() {
    fun callGetStatusCallEndUser(status: String) {
        val disposable = remoteRepository.callApiStatusCallEndUser(status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(DoortoDoorViewEvent.OnSuccessList(it))
            }, {
                viewEventObservable.postValue(
                    DoortoDoorViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callGetLocFoodTruck(id: String) {
        val disposable = remoteRepository.callApiGetLocFoodtruck(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(DoortoDoorViewEvent.OnSuccessGetLocFoodTruck(it))
            }, {
                viewEventObservable.postValue(
                    DoortoDoorViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callGetAddressPrimary() {
        val disposable = remoteRepository.callGetAddressPrimary()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(DoortoDoorViewEvent.OnSuccessListAddress(it))
            }, {
                viewEventObservable.postValue(
                    DoortoDoorViewEvent.OnFailedAddress(it)
                )
            })
        addDisposable(disposable)
    }

    fun getUserLocal(): User? {
        return userRepository.getCurrentUser()
    }

    fun callGetListNotif() {
        val disposable = remoteRepository.callListNotifBlast()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(DoortoDoorViewEvent.OnSuccessListNotif(it))
            }, {
                viewEventObservable.postValue(
                    DoortoDoorViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }


    fun callReqFoodTruck(idCall: String) {
        val disposable = remoteRepository.callApiFoodTruck(idCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(DoortoDoorViewEvent.OnSuccessCallFoodTruck(it))
            }, {
                viewEventObservable.postValue(
                    DoortoDoorViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }


}