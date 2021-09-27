package com.zeepos.domain.interactor.paymentsales

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.transaction.PaymentSales
import io.reactivex.Single
import javax.inject.Inject


class GetPaymentSalesUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : SingleUseCase<ResponseApi<List<PaymentSales>>, None>() {
    override fun buildUseCaseSingle(params: None): Single<ResponseApi<List<PaymentSales>>> {
        return remoteRepository.getPaymentSales()
    }
}