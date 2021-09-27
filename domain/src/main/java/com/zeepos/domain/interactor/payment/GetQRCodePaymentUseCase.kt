package com.zeepos.domain.interactor.payment

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.PaymentMethodRepo
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.QRCodeResponse
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/28/20
 */
class GetQRCodePaymentUseCase @Inject constructor(
    private val paymentMethodRepo: PaymentMethodRepo
) : SingleUseCase<QRCodeResponse, GetQRCodePaymentUseCase.Params>() {
    data class Params(val merchantId: Long?, val amount: Double, val type: String, val order: Order)

    override fun buildUseCaseSingle(params: Params): Single<QRCodeResponse> {
        return paymentMethodRepo.getQRCodePayment(
            params.merchantId,
            params.amount,
            params.type,
            params.order
        )
    }
}