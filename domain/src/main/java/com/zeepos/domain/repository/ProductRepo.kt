package com.zeepos.domain.repository

import com.zeepos.models.master.Product
import io.reactivex.Single

/**
 * Created by Arif S. on 7/10/20
 */
interface ProductRepo {
    fun getAllProductsCloud(): Single<List<Product>>
    fun getAllProductsCloud(filter: String, merchantId: Long): Single<List<Product>>
    fun getAllProducts(): Single<List<Product>>
    fun insertUpdate(productList: List<Product>)
}