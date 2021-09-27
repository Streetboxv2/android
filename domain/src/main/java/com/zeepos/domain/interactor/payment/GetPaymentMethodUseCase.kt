package com.zeepos.domain.interactor.payment

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.PaymentMethodRepo
import com.zeepos.models.entities.None
import com.zeepos.models.master.PaymentMethod
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 5/18/20
 */
class GetPaymentMethodUseCase @Inject constructor(
    private val paymentMethodRepo: PaymentMethodRepo
) : SingleUseCase<List<PaymentMethod>, None>() {
    override fun buildUseCaseSingle(params: None): Single<List<PaymentMethod>> {
        return paymentMethodRepo.getPaymentMethodCloud(0)
    }
}