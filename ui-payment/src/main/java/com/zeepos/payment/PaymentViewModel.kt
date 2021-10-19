package com.zeepos.payment

import com.zeepos.domain.interactor.order.CloseOrderUseCase
import com.zeepos.domain.interactor.order.GetOrderByUniqueIdUseCase
import com.zeepos.domain.interactor.payment.GetPaymentMethodUseCase
import com.zeepos.domain.interactor.payment.GetQRCodePaymentUseCase
import com.zeepos.domain.interactor.paymentsales.CreatePaymentSalesUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.None
import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Arif S. on 5/18/20
 */
class PaymentViewModel @Inject constructor(
    private val getPaymentMethodUseCase: GetPaymentMethodUseCase,
    private val getQRCodePaymentUseCase: GetQRCodePaymentUseCase,
    private val getOrderByUniqueIdUseCase: GetOrderByUniqueIdUseCase,
    private val closeOrderUseCase: CloseOrderUseCase,
    private val createPaymentSalesUseCase: CreatePaymentSalesUseCase,
    val remoteRepository: RemoteRepository
) : BaseViewModel<PaymentViewEvent>() {

    fun getPaymentMethod() {
        val disposable = getPaymentMethodUseCase.execute(None())
            .subscribe({ data ->
                viewEventObservable.postValue(
                    PaymentViewEvent.GetPaymentMethodSuccess(data ?: emptyList())
                )
            }, {
                viewEventObservable.postValue(
                    PaymentViewEvent.GetPaymentMethodFailed(it.message ?: "Failed!")
                )
            })

        addDisposable(disposable)
    }

    /*fun getQrPaymentNearby(
        merchantId: Long,
        amount: Int,
        address: String,
        types: String
    ) {
        val disposable = remoteRepository.callApiPaymentNearby(merchantId, amount, types, address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccess()) {
                    val data = it.data
                    viewEventObservable.postValue(
                        PaymentViewEvent.GetQRCodePaymentSuccess(data!!)
                    )
                }
            }, {
                viewEventObservable.postValue(
                    PaymentViewEvent.GetQRCodePaymentFailed(
                        it.message.toString()
                    )
                )
            })
        addDisposable(disposable)
    }*/

    fun getQRCodePayment(merchantId: Long?, amount: Double, type: String, order: Order,orderJson:String) {
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
                    viewEventObservable.postValue(PaymentViewEvent.GetQRCodePaymentSuccess(it))
                }, {
                    it.message?.let { errorMessage ->
                        viewEventObservable.postValue(
                            PaymentViewEvent.GetQRCodePaymentFailed(
                                errorMessage
                            )
                        )
                    }
                })

        addDisposable(disposable)
    }

    fun getOrderByUniqueId(uniqueId: String) {
        val disposable =
            getOrderByUniqueIdUseCase.execute(GetOrderByUniqueIdUseCase.Params(uniqueId))
                .subscribe({
                    viewEventObservable.postValue(PaymentViewEvent.GetOrderSuccess(it))
                }, {
                    viewEventObservable.postValue(PaymentViewEvent.GetOrderFailed(it))
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
                viewEventObservable.postValue(PaymentViewEvent.GetOrderFailed(it))
            })
        addDisposable(disposable)
    }

    fun closeOrder(uniqueId: String) {
        val disposable = closeOrderUseCase.execute(CloseOrderUseCase.Params(uniqueId))
            .subscribe({
                viewEventObservable.postValue(PaymentViewEvent.CloseOrderSuccess)
            }, { it.printStackTrace() })

        addDisposable(disposable)
    }

}

