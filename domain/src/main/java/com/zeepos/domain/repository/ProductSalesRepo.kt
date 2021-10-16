package com.zeepos.domain.repository

import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Arif S. on 7/2/20
 */
interface ProductSalesRepo {
    fun createProductSales(product: Product, order: Order, isMerge: Boolean): Single<ProductSales>
    fun updateOrRemoveProductSales(productSales: ProductSales): Completable
    fun updateProductSales(product: Product, order: Order): Completable
    fun getRecentOrder(): Single<List<ProductSales>>
    fun getByUniqueId(uniqueId: String): ProductSales?
    fun insertUpdate(productSales: ProductSales)
    fun remove(productSales: ProductSales): Completable
    fun removeQty(productSales: ProductSales): Completable
    fun remove(product: Product, order: Order): Completable
}