package com.streetbox.pos.ui.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailViewEvent
import com.streetbox.pos.ui.receipts.ReceiptViewEvent
import com.zeepos.domain.interactor.CreateSyncUseCase
import com.zeepos.domain.interactor.GetAllTransactionSyncData
import com.zeepos.domain.interactor.GetReceiveMessage
import com.zeepos.domain.interactor.SendTokenUseCase
import com.zeepos.domain.interactor.order.GetRecentOpenOrCreateOrderUseCase
import com.zeepos.domain.interactor.productsales.CreateProductSalesUseCase
import com.zeepos.domain.interactor.syncdata.SyncDataPOSUseCase
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.master.Product
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 5/3/20
 */
class MainViewModel @Inject constructor(
    private val createProductSalesUseCase: CreateProductSalesUseCase,
    private val getAllTransactionSyncData: GetAllTransactionSyncData,
    private val getRecentOpenOrCreateOrderUseCase: GetRecentOpenOrCreateOrderUseCase,
    private val userRepository: UserRepository,
    private val sendTokenUseCase: SendTokenUseCase,
    private val getReceiveMessage: GetReceiveMessage,
    private val syncDataPOSUseCase: SyncDataPOSUseCase
) : BaseViewModel<MainViewEvent>() {

    val productSalesObserver = MutableLiveData<ProductSales>()
    val orderObserver = MutableLiveData<Order>()

    fun getRecentOrder() {
        val disposable =
            getRecentOpenOrCreateOrderUseCase.execute(GetRecentOpenOrCreateOrderUseCase.Params())
                .subscribe({
                    orderObserver.postValue(it)
                }, { viewEventObservable.postValue(MainViewEvent.OrderFailedCreated) })

        addDisposable(disposable)
    }

    fun getAllTransaction(startDate: Long, endDate: Long, keyword: String) {
        val disposable = getAllTransactionSyncData.execute(
            GetAllTransactionSyncData.Params(
                startDate,
                endDate,
                keyword
            )
        ).subscribe({
            viewEventObservable.postValue(MainViewEvent.GetAllTransactionSuccess(it))
        }, {
            it.printStackTrace()
        })
        addDisposable(disposable)
    }

    fun addItem(product: Product, order: Order) {
        val disposable =
            createProductSalesUseCase.execute(CreateProductSalesUseCase.Params(product, order))
                .subscribe({
                    productSalesObserver.value = it
                }, {  it.message?.let { errorMessage ->
                    viewEventObservable.postValue(
                        MainViewEvent.OnAddItemFailed(
                            errorMessage
                        )
                    )
                } })

        addDisposable(disposable)
    }

    fun getUserLocal() : User?{
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

    fun getReceivedMessage() {
        val disposable = getReceiveMessage.execute(None())
            .subscribe({
                Log.d(ConstVar.TAG, "get online order message success!")
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }

}