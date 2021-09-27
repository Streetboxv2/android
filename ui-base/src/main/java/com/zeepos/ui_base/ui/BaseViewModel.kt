package com.zeepos.ui_base.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Arif S. on 5/1/20
 */
abstract class BaseViewModel<VE : BaseViewEvent> : ViewModel() {

    private val disposables = CompositeDisposable()
    var viewEventObservable: MutableLiveData<VE> = MutableLiveData()
    var errorObservable: MutableLiveData<Throwable> = MutableLiveData()

    fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun dispose(disposable: Disposable) {
        disposables.remove(disposable)
    }

    override fun onCleared() {
        disposables.dispose()
    }
}