package com.zeepos.localstorage

import com.zeepos.domain.repository.ProductRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.master.Product
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 7/10/20
 */
class ProductRepoImpl @Inject internal constructor(
    boxStore: BoxStore,
    private val retrofit: Retrofit
) : ProductRepo {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    private val box: Box<Product> by lazy {
        boxStore.boxFor(
            Product::class.java
        )
    }

    override fun getAllProductsCloud(): Single<List<Product>> {
        return service.getAllProducts()
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(it, retrofit)
                }
            }
            .map {
                if (it.isSuccess()) {
                    val data = it.data!!
                    box.removeAll()
                    insertUpdate(data)
                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getAllProductsCloud(filter: String, merchantId: Long): Single<List<Product>> {
        return service.getAllProducts(merchantId)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(it, retrofit)
                }
            }
            .map {
                if (it.isSuccess()) {
                    val data = it.data!!
                    box.removeAll()//remove previous data
                    insertUpdate(data)
                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }


    override fun getAllProducts(): Single<List<Product>> {
        return Single.fromCallable {
            box.all
        }
    }

    override fun insertUpdate(productList: List<Product>) {
        box.put(productList)
    }
}