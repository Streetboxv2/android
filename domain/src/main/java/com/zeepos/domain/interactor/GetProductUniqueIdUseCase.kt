package com.zeepos.domain.interactor

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Single
import javax.inject.Inject

class GetProductUniqueIdUseCase @Inject constructor(
    private val syncDataRepo: SyncDataRepo
) : SingleUseCase<ProductSales, GetProductUniqueIdUseCase.Params>() {
    data class Params(val orderUniqueId: String)

    override fun buildUseCaseSingle(params: Params): Single<ProductSales> {
        return Single.fromCallable { syncDataRepo.createProductSales(params.orderUniqueId) }
    }
}