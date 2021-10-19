package com.streetbox.pos.ui.checkout.checkoutdetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.streetbox.pos.ui.main.MainViewEvent
import com.zeepos.domain.interactor.CreateSyncUseCase
import com.zeepos.domain.interactor.GetAllTransactionSyncData
import com.zeepos.domain.interactor.GetReceiveMessage
import com.zeepos.domain.interactor.SendTokenUseCase
import com.zeepos.domain.interactor.order.CloseOrderUseCase
import com.zeepos.domain.interactor.order.GetOrderByUniqueIdUseCase
import com.zeepos.domain.interactor.order.GetRecentOpenOrCreateOrderUseCase
import com.zeepos.domain.interactor.payment.GetQRCodePaymentUseCase
import com.zeepos.domain.interactor.paymentsales.CreatePaymentSalesUseCase
import com.zeepos.domain.interactor.productsales.CreateProductSalesUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.master.SyncData
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 7/12/20
 */
class CheckoutDetailViewModel @Inject constructor(
    private val getOrderByUniqueIdUseCase: GetOrderByUniqueIdUseCase,
    private val closeOrderUseCase: CloseOrderUseCase,
    private val userRepository: UserRepository,
    private val createSyncUseCase: CreateSyncUseCase,
    private val createPaymentSalesUseCase: CreatePaymentSalesUseCase,
    private val getQRCodePaymentUseCase: GetQRCodePaymentUseCase,
    private val getAllTransactionSyncData: GetAllTransactionSyncData,
    private val getRecentOpenOrCreateOrderUseCase: GetRecentOpenOrCreateOrderUseCase,
    private val sendTokenUseCase: SendTokenUseCase,
    private val getReceiveMessage: GetReceiveMessage,
    private val syncDataRepo: SyncDataRepo
) : BaseViewModel<CheckoutDetailViewEvent>() {
    val orderObserver = MutableLiveData<Order>()

    fun getOrder(uniqueId: String) {
        val disposable =
            getOrderByUniqueIdUseCase.execute(GetOrderByUniqueIdUseCase.Params(uniqueId))
                .subscribe({
                    viewEventObservable.postValue(CheckoutDetailViewEvent.GetOrderSuccess(it))
                }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun closeOrder(uniqueId: String) {
        val disposable = closeOrderUseCase.execute(CloseOrderUseCase.Params(uniqueId))
            .subscribe({
                viewEventObservable.postValue(CheckoutDetailViewEvent.CloseOrderSuccess)
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }

    fun getRecentOrder() {
        val disposable =
            getRecentOpenOrCreateOrderUseCase.execute(GetRecentOpenOrCreateOrderUseCase.Params())
                .subscribe({
                    orderObserver.postValue(it)
                }, { viewEventObservable.postValue(CheckoutDetailViewEvent.OrderFailedCreated) })

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
            viewEventObservable.postValue(CheckoutDetailViewEvent.GetAllTransactionSuccess(it))
        }, {
            it.printStackTrace()
        })
        addDisposable(disposable)
    }

    fun generatePaymentSales(paymentMethod: PaymentMethod, status: String, order: Order) {
        val disposable = createPaymentSalesUseCase.execute(
            CreatePaymentSalesUseCase.Params(
                paymentMethod,
                status,
                order
            )
        )
            .subscribe({
                closeOrder(order.uniqueId)
            }, {
                it.printStackTrace()
            })
        addDisposable(disposable)
    }

    fun getProfileMerchantLocal(): User? {
        return userRepository.getProfileMerchant()
    }

    fun getOperator(): User? {
        return userRepository.getOperator()
    }

    fun saveSyncData(syncData: SyncData): SyncData {
        return syncDataRepo.save(syncData)
    }


    fun createSync(type: String, businessDate: Long, data: String) {
        val disposable = createSyncUseCase.execute(
            CreateSyncUseCase.Params(
                type,
                businessDate,
                data
            )
        )
            .subscribe({
                Log.d(ConstVar.TAG, "Create sync success!")
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }


    fun getQRCodePayment(merchantId: Long, amount: Double, type: String, order: Order,orderJson:String) {
        val disposable =
            getQRCodePaymentUseCase.execute(
                GetQRCodePaymentUseCase.Params(
                    merchantId,
                    amount,
                    type,
                    order,
                    orderJson
                )
            )
                .subscribe({
                    viewEventObservable.postValue(CheckoutDetailViewEvent.GetQRCodePaymentSuccess(it))
                }, {
                    it.message?.let { errorMessage ->
                        viewEventObservable.postValue(
                            CheckoutDetailViewEvent.GetQRCodePaymentFailed(
                                errorMessage
                            )
                        )
                    }
                })

        addDisposable(disposable)
    }
}