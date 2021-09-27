package com.zeepos.domain.interactor.productsales

import com.zeepos.domain.interactor.base.CompletableUseCase
import com.zeepos.domain.repository.ProductSalesRepo
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Arif S. on 7/27/20
 */
class UpdateOrRemoveProductSalesUseCase @Inject constructor(
    private val productSalesRepo: ProductSalesRepo
) : CompletableUseCase<UpdateOrRemoveProductSalesUseCase.Params>() {
    data class Params(
        val productSales: ProductSales
    )

    override fun buildUseCaseCompletable(params: Params): Completable {
        return productSalesRepo.updateOrRemoveProductSales(
            params.productSales
        )
    }
}