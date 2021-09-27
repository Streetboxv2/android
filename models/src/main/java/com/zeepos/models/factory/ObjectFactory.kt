package com.zeepos.models.factory

import com.google.maps.model.LatLng
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.MapData
import com.zeepos.models.master.*
import com.zeepos.models.transaction.*
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.RandStringNumUtil
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Arif S. on 5/17/20
 */
object ObjectFactory {
    fun generateGUID(): String {
        return UUID.randomUUID().toString()
    }

    fun createMapData(
        type: String,
        exData: Any? = null,
        latLng: LatLng? = null,
        latLngList: List<LatLng> = arrayListOf()
    ): MapData {
        val mapData = MapData(type)
        mapData.uniqueId = generateGUID()
        mapData.exData = exData
        mapData.latLng = latLng
        mapData.latLngList = latLngList

        return mapData
    }

    fun createOrder(
        merchantId: Long,
        user: User,
        orderNo: String,
        foodTruck: FoodTruck? = null,
        userMerchant: User? = null
    ): Order {
        val order = Order()
        order.uniqueId = generateGUID()
        order.userId = user.id
        order.orderNo = orderNo.padStart(4, '0')

        if (foodTruck != null) {//online order
            order.billNo =
                "OL" + TimeUnit.MILLISECONDS.toHours(DateTimeUtil.getCurrentDateWithoutTime()) + RandStringNumUtil.generate()
        } else {
            order.billNo =
                "ODR" + TimeUnit.MILLISECONDS.toHours(DateTimeUtil.getCurrentDateWithoutTime()) + RandStringNumUtil.generate()
        }

        order.merchantId = merchantId
//        order.businessDate = DateTimeUtil.getCurrentDateTime()
        order.businessDate = DateTimeUtil.getCurrentLocalDateWithoutTime()
        order.createdAt = DateTimeUtil.getCurrentLocalDateTime()
        order.updatedAt = DateTimeUtil.getCurrentLocalDateTime()

        if (foodTruck != null) {//online order
            order.merchantUsersId = foodTruck.merchantUsersId
            order.types = 1
            order.orderType = foodTruck.types
            order.titleToko = foodTruck.name
            order.address = foodTruck.address
        } else {
            order.merchantUsersId = userMerchant?.merchantUsersId ?: 0
        }

        return order
    }

    fun createOrderBill(order: Order): OrderBill {
        val orderBill = OrderBill()
        orderBill.uniqueId = generateGUID()
        orderBill.orderUniqueId = order.uniqueId
//        orderBill.businessDate = order.businessDate
        orderBill.billNo = order.billNo
        orderBill.businessDate = DateTimeUtil.getCurrentDateWithoutTime()
        orderBill.createdAt = DateTimeUtil.getCurrentDateTime()
        orderBill.updatedAt = DateTimeUtil.getCurrentDateTime()

        //update business date order
        order.businessDate = DateTimeUtil.getCurrentDateWithoutTime()
//        order.businessDate = DateTimeUtil.getCurrentDateTime()
        orderBill.order.target = order

        return orderBill
    }

    fun createProductSales(product: Product, order: Order, orderBill: OrderBill): ProductSales {
        val productSales = ProductSales()
        productSales.uniqueId = generateGUID()
        productSales.orderUniqueId = order.uniqueId
        productSales.orderBillUniqueId = orderBill.uniqueId
        productSales.productId = product.id
        productSales.name = product.name
        productSales.qty = 1
        productSales.qtyProduct = product.qty
        productSales.businessDate = DateTimeUtil.getCurrentDateWithoutTime()
        productSales.createdAt = DateTimeUtil.getCurrentDateTime()
        productSales.updatedAt = DateTimeUtil.getCurrentDateTime()
        productSales.photo = product.photo ?: ConstVar.EMPTY_STRING

        if (product.priceAfterDiscount > 0) {
//            productSales.price = product.priceAfterDiscount
            productSales.priceOriginal = product.price
            product.priceAfterDiscount = product.price - (product.price * product.discount / 100)
            productSales.priceAfterDiscount = product.priceAfterDiscount
            productSales.price = product.priceAfterDiscount
            productSales.discount = product.discount
        } else {
            productSales.price = product.price
        }

        productSales.order.target = order
        productSales.orderBill.target = orderBill

        return productSales
    }

    fun createTaxSales(tax: Tax, order: Order): TaxSales {
        val taxSales = TaxSales()
        taxSales.uniqueId = generateGUID()
        taxSales.merchantId = tax.merchantId
        taxSales.merchantTaxId = tax.id
        taxSales.name = tax.name!!
        taxSales.orderUniqueId = order.uniqueId
        taxSales.type = tax.type
        taxSales.isActive = tax.isActive
        taxSales.createdAt = DateTimeUtil.getCurrentDateTime()
        taxSales.updatedAt = DateTimeUtil.getCurrentDateTime()
        taxSales.order.target = order
        return taxSales
    }

    fun createPaymentSales(
        paymentMethod: PaymentMethod,
        status: String,
        order: Order
    ): PaymentSales {
        val orderBill = if (order.orderBill.size > 0) order.orderBill[0] else null
        val orderBillUniqueId = orderBill?.uniqueId ?: order.uniqueId
        val paymentSales = PaymentSales()
        paymentSales.uniqueId = generateGUID()
        paymentSales.orderUniqueId = order.uniqueId
        paymentSales.orderBillUniqueId = orderBillUniqueId
        paymentSales.paymentMethodId = paymentMethod.id
        paymentSales.type = paymentMethod.type
        paymentSales.name = paymentMethod.name ?: ConstVar.EMPTY_STRING
        paymentSales.amount = orderBill?.grandTotal ?: 0.0
        paymentSales.status = status
        paymentSales.createdAt = DateTimeUtil.getCurrentDateTime()
        paymentSales.updatedAt = DateTimeUtil.getCurrentDateTime()

        paymentSales.order.target = order
        paymentSales.orderBill.target = orderBill

        return paymentSales
    }

    fun createParkingSales(
        order: Order,
        parkingSpace: ParkingSpace,
        parkingSlot: ParkingSlot
    ): ParkingSales {

        val parkingSales = ParkingSales()
        parkingSales.uniqueId = generateGUID()
        parkingSales.orderUniqueId = order.uniqueId
        parkingSales.parkingSpaceId = parkingSpace.id
        parkingSales.orderId = order.id
        parkingSales.name = parkingSpace.name ?: ConstVar.EMPTY_STRING
        parkingSales.address = parkingSpace.address ?: ConstVar.EMPTY_STRING
        parkingSales.price = parkingSpace.point.toDouble()
        parkingSales.businessDate = DateTimeUtil.getCurrentDateWithoutTime()
        parkingSales.createdAt = System.currentTimeMillis()
        parkingSales.updatedAt = System.currentTimeMillis()
        parkingSales.order.target = order

        return parkingSales
    }

    fun createParkingSlotSales(
        parkingSales: ParkingSales,
        parkingSlot: ParkingSlot
    ): ParkingSlotSales {
        val parkingSlotSales = ParkingSlotSales()

        parkingSlotSales.uniqueId = generateGUID()
        parkingSlotSales.parkingSlotId = parkingSlot.id
        parkingSlotSales.parkingSalesUniqueId = parkingSales.uniqueId
        parkingSlotSales.name = parkingSlot.name ?: ConstVar.EMPTY_STRING
        parkingSlotSales.qty = 1
        parkingSlotSales.price = parkingSlot.point.toDouble()
        parkingSlotSales.createdAt = System.currentTimeMillis()
        parkingSlotSales.updatedAt = System.currentTimeMillis()

        return parkingSlotSales
    }

    fun createInitialVoucherSales(order: Order): VoucherSales {
        return VoucherSales(
            uniqueId = generateGUID(),
            orderUniqueId = order.uniqueId
        )
    }

    fun createTaskOperator(
        scheduleDate: String,
        parkingSlotSales: ParkingSlotSales,
        parkingSales: ParkingSales,
        user: User
    ): TaskOperator {
        val taskOperator = TaskOperator()
        taskOperator.uniqueId = generateGUID()
        taskOperator.userId = user.id
        taskOperator.parkingSalesId = parkingSales.id
        taskOperator.parkingSlotSalesId = parkingSlotSales.id
        taskOperator.schedule = scheduleDate
        taskOperator.scheduleDate = scheduleDate

        val starTime = DateTimeUtil.getDateFromString(parkingSlotSales.startDate)?.time
            ?: DateTimeUtil.getCurrentDateTime()
        val endTime = DateTimeUtil.getDateFromString(parkingSlotSales.endDate)?.time
            ?: DateTimeUtil.getCurrentDateTime()

        taskOperator.startTime = starTime
        taskOperator.endTime = endTime
        taskOperator.operatorName = parkingSales.name
        taskOperator.note = ConstVar.EMPTY_STRING
        taskOperator.status = ConstVar.TASK_STATUS_OPEN
//        taskOperator.pickUpLat = -6.247964
//        taskOperator.pickUpLng = 106.8523116
        taskOperator.latParkingSpace = parkingSales.latitude
        taskOperator.lonParkingSpace = parkingSales.longitude
        taskOperator.createdAt = System.currentTimeMillis()
        taskOperator.updatedAt = System.currentTimeMillis()

        taskOperator.user.target = user

        return taskOperator
    }

    fun createSyncData(type: String, page: Int): SyncData {
        val syncData = SyncData()
        syncData.syncDate = System.currentTimeMillis()
        syncData.type = type
        syncData.nextPage = page

        return syncData
    }

    fun createSyncData(type: String, data: String): SyncData {
        val syncData = SyncData()
        syncData.uniqueId = generateGUID()
        syncData.type = type
        syncData.status = ConstVar.SYNC_STATUS_NOT_SYNC

        return syncData
    }

    fun createSync(type: String, data: String, businessDate: Long): SyncData {
        val syncData = SyncData()
        syncData.syncDate = System.currentTimeMillis()
        syncData.type = type
        syncData.data = data
        syncData.businessDate = businessDate
        syncData.status = 0
        syncData.uniqueId = generateGUID()

        return syncData
    }


}