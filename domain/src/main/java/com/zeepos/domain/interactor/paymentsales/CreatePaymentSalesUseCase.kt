package com.zeepos.domain.interactor.paymentsales

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.PaymentSalesRepo
import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.PaymentSales
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/28/20
 */
class CreatePaymentSalesUseCase @Inject constructor(
    private val paymentSalesRepo: PaymentSalesRepo
) : SingleUseCase<PaymentSales, CreatePaymentSalesUseCase.Params>() {
    data class Params(val paymentMethod: PaymentMethod, val status: String, val order: Order)

    override fun buildUseCaseSingle(params: Params): Single<PaymentSales> {
        return paymentSalesRepo.generatePaymentSales(
            params.paymentMethod,
            params.status,
            params.order
        )
    }
}