package com.zeepos.localstorage

import com.zeepos.domain.repository.OrderBillRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.Tax
import com.zeepos.models.master.Tax_
import com.zeepos.models.transaction.*
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Arif S. on 7/3/20
 */
class OrderBillRepoImpl @Inject constructor(
    boxStore: BoxStore
) : OrderBillRepo {

    private val box: Box<OrderBill> by lazy {
        boxStore.boxFor(OrderBill::class.java)
    }

    private val boxOrder: Box<Order> by lazy {
        boxStore.boxFor(Order::class.java)
    }

    private val boxTax: Box<Tax> by lazy {
        boxStore.boxFor(Tax::class.java)
    }

    private val boxTaxSales: Box<TaxSales> by lazy {
        boxStore.boxFor(TaxSales::class.java)
    }

    override fun insertOrder(order: Order) {
        var boxOrder = boxOrder.put(order)
    }


    override fun calculateOrder(order: Order): Single<OrderBill> {
        return Single.fromCallable {
            var orderBill = getByOrderUniqueId(order.uniqueId)
            val tax = boxTax.query().build().findFirst()

            if (orderBill == null) {
                orderBill = ObjectFactory.createOrderBill(order)
            }

            var subtotal = 0.0
            var totalTax = 0.0
            var grandTotalqty = 0

            for (productSales in orderBill.productSales) {
                subtotal += productSales.price * productSales.qty
                grandTotalqty += productSales.qty
            }

            orderBill.subTotal = subtotal
            order.grandTotalqty = grandTotalqty

            if (tax != null) {
                totalTax = subtotal * tax.amount / 100

                //generate tax sales
                val taxSalesDb = boxTaxSales.query()
                    .equal(TaxSales_.orderUniqueId, order.uniqueId).build().findFirst()

                if (taxSalesDb == null) {
                     val taxSales = ObjectFactory.createTaxSales(tax, order)
                    taxSales.amount = tax.amount
                    taxSales.type = tax.type
                    boxTaxSales.put(taxSales)
                } else {
                    taxSalesDb.amount = tax.amount
                    taxSalesDb.type = tax.type
                    boxTaxSales.put(taxSalesDb)
                }
            }

            orderBill.totalTax = totalTax

            if ( tax!!.type > 0 && tax.isActive == true) {
                orderBill.grandTotal = subtotal
                order.grandTotal = subtotal

            } else if (tax.type == 0 && tax.isActive == true) {
                orderBill.grandTotal = subtotal + totalTax
                order.grandTotal = subtotal + totalTax
            } else if (tax.isActive == false){
                orderBill.totalTax = 0.0
            }

            if (tax != null) {
                orderBill.taxType = tax.type
                orderBill.taxName = tax.name ?: ConstVar.EMPTY_STRING
                orderBill.totalTax = totalTax
                orderBill.grandTotal = order.grandTotal

            }

            box.put(orderBill)
            boxOrder.put(order)
            orderBill
        }
    }

    override fun getByOrderUniqueId(orderUniqueId: String): OrderBill? {
        return box.query().equal(OrderBill_.orderUniqueId, orderUniqueId).build().findFirst()
    }

    override fun getAllOrderBill(): Single<List<OrderBill>> {
        return Single.fromCallable { box.all }
    }

    override fun getAllOrderBillDesc(): OrderBill? {
        return box.query().equal(OrderBill_.isClose, false).orderDesc(OrderBill_.createdAt).build()
            .findFirst()
    }

    override fun insertUpdateOrderBill(orderBill: OrderBill):Completable {
        return Completable.fromCallable{box.put(orderBill)}
    }

    override fun insertUpdateOrderBill(orderBill: List<OrderBill>) {
        box.put(orderBill)
    }

}