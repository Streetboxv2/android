package com.zeepos.domain.interactor.productsales

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ProductSalesRepo
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/2/20
 */
class CreateProductSalesUseCase @Inject constructor(
    private val productSalesRepo: ProductSalesRepo
) :
    SingleUseCase<ProductSales, CreateProductSalesUseCase.Params>() {
    data class Params(val product: Product, val order: Order, val isMerge: Boolean = true)

    override fun buildUseCaseSingle(params: Params): Single<ProductSales> {
        return productSalesRepo.createProductSales(params.product, params.order, params.isMerge)
    }
}