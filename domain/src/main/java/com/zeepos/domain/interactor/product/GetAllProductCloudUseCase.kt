package com.zeepos.domain.interactor.product

import com.zeepos.domain.interactor.base.SingleUseCase
import com.zeepos.domain.repository.ProductRepo
import com.zeepos.models.entities.None
import com.zeepos.models.master.Product
import io.reactivex.Single
import javax.inject.Inject

class GetAllProductCloudUseCase @Inject constructor(
    private val productRepo: ProductRepo
) : SingleUseCase<List<Product>, None>() {
    override fun buildUseCaseSingle(params: None): Single<List<Product>> {
        return productRepo.getAllProductsCloud()
    }

}