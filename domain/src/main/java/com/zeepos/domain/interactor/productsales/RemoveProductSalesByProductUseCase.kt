package com.zeepos.domain.interactor.productsales

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.ProductSalesRepo
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/27/20
 */
class RemoveProductSalesByProductUseCase @Inject constructor(
    private val productSalesRepo: ProductSalesRepo
) : CompletableUseCase<RemoveProductSalesByProductUseCase.Params>() {
    data class Params(val product: Product, val order: Order)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return productSalesRepo.remove(params.product, params.order)
    }
}