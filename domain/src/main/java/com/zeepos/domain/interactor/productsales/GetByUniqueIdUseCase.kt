//package com.zeepos.domain.interactor.productsales
//
//import com.zeepos.domain.interactor.base.SingleUseCase
//import com.zeepos.domain.repository.ProductSalesRepo
//import com.zeepos.models.entities.None
//import com.zeepos.models.transaction.ProductSales
//import io.reactivex.Single
//import javax.inject.Inject
//
//class GetByUniqueIdUseCase @Inject constructor(
//    private val productSalesRepo: ProductSalesRepo
//) : SingleUseCase<List<ProductSales>, None>() {
//    override fun buildUseCaseSingle(params: None): Single<List<ProductSales>> {
//        return productSalesRepo.getByUniqueId()
//    }
//}