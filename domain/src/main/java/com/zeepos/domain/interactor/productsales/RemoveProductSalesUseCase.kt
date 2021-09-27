package com.zeepos.domain.interactor.productsales

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.ProductSalesRepo
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/2/20
 */
class RemoveProductSalesUseCase @Inject constructor(
    private val productSalesRepo: ProductSalesRepo
) : CompletableUseCase<RemoveProductSalesUseCase.Params>() {

    data class Params(val productSales: ProductSales)

    override fun buildUseCaseCompletable(params: Params): Completable {
        return productSalesRepo.remove(params.productSales)
    }
}