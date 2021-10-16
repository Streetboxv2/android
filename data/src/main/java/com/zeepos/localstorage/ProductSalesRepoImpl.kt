package com.zeepos.localstorage

import android.widget.Toast
import com.zeepos.domain.repository.OrderBillRepo
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.domain.repository.ProductSalesRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.models.transaction.ProductSales_
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import javax.inject.Inject

/**
 * Created by Arif S. on 7/2/20
 */
class ProductSalesRepoImpl @Inject constructor(
    boxStore: BoxStore,
    private val orderBillRepo: OrderBillRepo
) : ProductSalesRepo {

    private val box: Box<ProductSales> by lazy {
        boxStore.boxFor(ProductSales::class.java)
    }

    override fun createProductSales(
        product: Product,
        order: Order,
        isMerge: Boolean
    ): Single<ProductSales> {

        return Single.fromCallable {
            var orderBill = orderBillRepo.getByOrderUniqueId(order.uniqueId)

            if (orderBill == null) {
                orderBill = ObjectFactory.createOrderBill(order)
                order.orderBill.add(orderBill)
            }

            var productSales: ProductSales?

            if (isMerge) {
                productSales =
                    box.query()
                        .equal(ProductSales_.productId, product.id)
                        .equal(ProductSales_.orderUniqueId, order.uniqueId)
                        .build().findFirst()

                if (productSales != null) {
                    productSales.qty = productSales.qty + 1

                } else {
                    productSales = ObjectFactory.createProductSales(product, order, orderBill)
                }
            } else {
                productSales = ObjectFactory.createProductSales(product, order, orderBill)
            }


            if(productSales.qty <= product.qty){
                insertUpdate(productSales)
            }else{
                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }


            getByUniqueId(productSales.uniqueId)
        }
    }

    override fun updateOrRemoveProductSales(productSales: ProductSales): Completable {
        return Completable.fromCallable {
            if (productSales.qty > 0) {
                box.put(productSales)
            } else {
                box.remove(productSales)
            }
        }
    }

    override fun updateProductSales(product: Product, order: Order): Completable {
        return Completable.fromCallable {
        }
    }


    override fun getRecentOrder(): Single<List<ProductSales>> {
        return Single.fromCallable { arrayListOf<ProductSales>() }
    }

    override fun getByUniqueId(uniqueId: String): ProductSales? {
        return box.query().equal(ProductSales_.uniqueId, uniqueId).build().findFirst()
    }

    override fun insertUpdate(productSales: ProductSales) {
        box.put(productSales)
    }

    override fun remove(productSales: ProductSales): Completable {
        return Completable.fromCallable { box.remove(productSales) }
    }

    override fun remove(product: Product, order: Order): Completable {
        return Completable.fromCallable {
            val productSales =
                box.query()
                    .equal(ProductSales_.productId, product.id)
                    .equal(ProductSales_.orderUniqueId, order.uniqueId)
                    .build().findFirst()

            if (productSales != null) {
                if (productSales.qty > 1) {
                    productSales.qty = productSales.qty - 1
                    box.put(productSales)
                } else {
                    box.remove(productSales)
                }
            }
        }
    }

    override fun removeQty(productSales: ProductSales): Completable {
        return Completable.fromCallable {
            val ps =
                box.query().equal(ProductSales_.uniqueId, productSales.uniqueId).build().findFirst()

            if (ps != null) {
                if (ps.qty > 1) {
                    ps.qty = ps.qty - 1
                    box.put(ps)
                } else {
                    box.remove(ps)
                }
            }
        }
    }
}