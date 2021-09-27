package com.zeepos.localstorage

import com.zeepos.domain.repository.PaymentMethodRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.Order_
import com.zeepos.models.transaction.PaymentSales
import com.zeepos.models.transaction.QRCodeResponse
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 5/18/20
 */
class PaymentMethodRepoImpl @Inject constructor(
    private val retrofit: Retrofit,
    boxStore: BoxStore
) : PaymentMethodRepo {
    private val box: Box<PaymentMethod> = boxStore.boxFor(
        PaymentMethod::class.java
    )

    private val boxPaymentSales: Box<PaymentSales> = boxStore.boxFor(
        PaymentSales::class.java
    )

    private val boxOrder: Box<Order> = boxStore.boxFor(
        Order::class.java
    )

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    override fun getPaymentMethod(): List<PaymentMethod> {
        return box.all
    }

    override fun insertUpdate(paymentMethod: PaymentMethod) {
        box.put(paymentMethod)
    }

    override fun getPaymentMethodCloud(merchantId: Long): Single<List<PaymentMethod>> {
        return service.getPaymentMethod()
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(it, retrofit)
                }
            }
            .map {
                if (it.isSuccess()) {
                    val data = it.data!!
                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }

    }

    override fun getQRCodePayment(
        merchantId: Long?,
        amount: Double,
        type: String,
        order: Order
    ): Single<QRCodeResponse> {
        val address = order.address ?: ConstVar.EMPTY_STRING
        return service.getQRCodePayment(merchantId, amount.toInt(), type, address)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(it, retrofit)
                }
            }
            .map {
                if (it.isSuccess()) {
                    val data = it.data!!
                    val orderDb =
                        boxOrder.query().equal(Order_.uniqueId, order.uniqueId).build().findFirst()
                    if (orderDb != null) {
                        val paymentSales = orderDb.paymentSales
                        if (paymentSales.size > 0) {
                            paymentSales[0].qrCode = data.qrCode
                            boxPaymentSales.put(paymentSales)
                        }

                        orderDb.trxId = data.trxId!!
                        boxOrder.put(orderDb)//attached order trxId
                    }

                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }
}