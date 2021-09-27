package com.zeepos.streetbox.ui.broadcast

import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BroadCastViewModel @Inject constructor(
    val remoteRepository: RemoteRepository,
    val userRepository: UserRepository
) : BaseViewModel<BroadCastViewEvent>() {

    fun callReqAutoBlastToggle() {
        val disposable = remoteRepository.callApiAutoBlastToggle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessReqAutoBlast(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callGetStatusCallEndUser(status: String) {
        val disposable = remoteRepository.callApiStatusAccepted(status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessList(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun getUser(): User? {
        return userRepository.getCurrentUser()
    }

    fun callFinishOrder(idCall: String) {
        val disposable = remoteRepository.callApiFinishOrder(idCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessListFinishOrder(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callUpdateLoc(map: MutableMap<String, Double>) {
        val disposable = remoteRepository.callApiUpdateLocFoodTruck(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessUpdateLoc(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callAcceptedOrder(idCall: String, status: String) {
        val disposable = remoteRepository.callApiAcceptedOrder(idCall, status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessStatusOrder(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callRejectOrder(idCall: String, status: String) {
        val disposable = remoteRepository.callApiRejectOrder(idCall, status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessStatusOrder(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callOnprocessOrder(idCall: String, status: String) {
        val disposable = remoteRepository.callApiOnprocessOrder(idCall, status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessStatusOrder(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callGetTimerBlast() {
        val disposable = remoteRepository.callApiTimerFoodTruck()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessTimerGetBlast(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailed(it)
                )
            })

        addDisposable(disposable)
    }

    fun callNotifBlastManual() {
        val disposable = remoteRepository.callApiNotifBlatManual()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BroadCastViewEvent.OnSuccessNotifBlastManual(it))
            }, {
                viewEventObservable.postValue(
                    BroadCastViewEvent.OnFailedBlastNotif(it)
                )
            })

        addDisposable(disposable)
    }

}