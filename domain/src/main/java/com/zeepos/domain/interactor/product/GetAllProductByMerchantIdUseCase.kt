package com.zeepos.domain.interactor.product

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ProductRepo
import com.zeepos.models.master.Product
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/23/20
 */
class GetAllProductByMerchantIdUseCase @Inject constructor(
    private val productRepo: ProductRepo
) : SingleUseCase<List<Product>, GetAllProductByMerchantIdUseCase.Params>() {
    data class Params(val filter: String, val merchantId: Long)

    override fun buildUseCaseSingle(params: Params): Single<List<Product>> {
        return productRepo.getAllProductsCloud(params.filter, params.merchantId)
    }
}