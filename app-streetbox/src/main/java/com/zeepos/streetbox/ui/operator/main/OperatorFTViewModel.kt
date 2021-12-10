package com.zeepos.streetbox.ui.operator.main

import android.util.Log
import com.zeepos.domain.interactor.CheckStatusShiftUseCase
import com.zeepos.domain.interactor.SendTokenUseCase
import com.zeepos.domain.interactor.ShiftUseCase
import com.zeepos.domain.interactor.operator.GetTaskOperatorUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.Shift
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class OperatorFTViewModel @Inject constructor(
    private val getTaskOperatorUseCase: GetTaskOperatorUseCase,
    private val shiftUseCase: ShiftUseCase,
    private val checkStatusShiftUseCase: CheckStatusShiftUseCase,
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val sendTokenUseCase: SendTokenUseCase

    ) : BaseViewModel<OperatorFTViewEvent>() {
    fun getTaskOperator(userId: Long) {
        val operatorDisposable =
            getTaskOperatorUseCase.execute(GetTaskOperatorUseCase.Params(userId))
                .subscribe({
                    handleCallback(OperatorFTViewEvent.GetTaskOperatorSuccess(it))
                }, {
                    handleCallback(OperatorFTViewEvent.GetTaskOperatorFailed(it.message))
                })

        addDisposable(operatorDisposable)
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
    fun shiftInOperatorTask() {
        addDisposable(
            shiftUseCase.execute(None())
                .subscribe(

                    {
                        viewEventObservable.postValue(
                            OperatorFTViewEvent.GetShiftSuccess(it.data!!)
                        )
                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFTViewEvent.GetShiftFailed(it)
                            )
                        }
                    }
                )
        )
    }

    fun checkStatusShiftIn() {
        addDisposable(
            checkStatusShiftUseCase.execute(None())
                .subscribe(
                    {
                        /*viewEventObservable.postValue(
                            OperatorFTViewEvent.CheckStatusShiftIn(it.data!!)*/
                            handleStatus(it!!)

                    },
                    { error ->
                        error.message?.let {
                            viewEventObservable.postValue(
                                OperatorFTViewEvent.CheckStatusShiftFailed(it)
                            )
                        }
                    }
                )
        )
    }

    private fun handleStatus(shift: ResponseApi<Shift>){
        if(shift.data!!.shiftIn == false)
            {
                viewEventObservable.value = OperatorFTViewEvent.ShiftFalse
            }
        if(shift.data!!.shiftIn == true)  {
                viewEventObservable.value = OperatorFTViewEvent.ShiftTrue
            }
        }

    private fun handleCallback(useCase: OperatorFTViewEvent) {
        when (useCase) {
            is OperatorFTViewEvent.GetTaskOperatorSuccess -> {
                useCase.data?.let {
                    viewEventObservable.postValue(useCase)
                }
            }
            is OperatorFTViewEvent.GetTaskOperatorFailed -> {
                useCase.message?.let {
                    viewEventObservable.postValue(useCase)
                }
            }
        }
    }

    fun logout(){
        localPreferencesRepository.deleteSession()
    }
}


