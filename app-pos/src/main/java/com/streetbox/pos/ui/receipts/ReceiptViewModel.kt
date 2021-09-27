package com.streetbox.pos.ui.receipts

import androidx.lifecycle.MutableLiveData
import com.zeepos.domain.interactor.GetAllTransactionSyncData
import com.zeepos.domain.interactor.VoidOrderUseCase
import com.zeepos.domain.interactor.order.GetOrderByUniqueIdUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.ObservableSource
import javax.inject.Inject

/**
 * Created by Arif S. on 10/7/20
 */
class ReceiptViewModel @Inject constructor(
    private val getAllTransactionSyncData: GetAllTransactionSyncData,
    private val getOrderByUniqueIdUseCase: GetOrderByUniqueIdUseCase,
    private val voidOrderUseCase: VoidOrderUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<ReceiptViewEvent>() {

    val orderObs: MutableLiveData<Order> = MutableLiveData()

    fun getAllTransaction(startDate: Long, endDate: Long, keyword: String) {
        val disposable = getAllTransactionSyncData.execute(
            GetAllTransactionSyncData.Params(
                startDate,
                endDate,
                keyword
            )
        ).subscribe({
            viewEventObservable.postValue(ReceiptViewEvent.GetAllTransactionSuccess(it))
        }, {
            it.printStackTrace()
        })
        addDisposable(disposable)
    }

    fun searchTransaction(
        startDate: Long,
        endDate: Long,
        keyword: String
    ): ObservableSource<List<Order>> {
        return getAllTransactionSyncData.execute(
            GetAllTransactionSyncData.Params(
                startDate,
                endDate,
                keyword
            )
        )
            .doOnError { }
            .onErrorReturn {
                arrayListOf()
            }
            .toObservable()
    }

    fun getOrder(orderUniqueId: String) {
        val disposable =
            getOrderByUniqueIdUseCase.execute(GetOrderByUniqueIdUseCase.Params(orderUniqueId))
                .subscribe({
                    orderObs.postValue(it)
                }, { it.printStackTrace() })
        addDisposable(disposable)
    }

    fun voidOrder(trxId: String) {
        val disposable = voidOrderUseCase.execute(
            VoidOrderUseCase.Params(trxId)
        ).subscribe({
            viewEventObservable.postValue(ReceiptViewEvent.VoidOrderSuccess("success"))
        }, {
            it.printStackTrace()
        })
        addDisposable(disposable)
    }

    fun getProfileMerchantLocal() : User?{
        return userRepository.getProfileMerchant()
    }

    fun getOperator() : User?{
        return userRepository.getOperator()
    }

}