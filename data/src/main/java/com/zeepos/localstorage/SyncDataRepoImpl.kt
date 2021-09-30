package com.zeepos.localstorage

import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.zeepos.domain.interactor.CloseOnlineOrderUseCase
import com.zeepos.domain.interactor.CreateSyncUseCase
import com.zeepos.domain.interactor.VoidOrderUseCase
import com.zeepos.domain.repository.OrderBillRepo
import com.zeepos.domain.repository.ProductRepo
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.BookHomeVisit
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.*
import com.zeepos.models.transaction.*
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import com.zeepos.utilities.DateTimeUtil
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Function3
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.io.*
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

/**
 * Created by Arif S. on 6/17/20
 */
class SyncDataRepoImpl @Inject constructor(
    boxStore: BoxStore,
    private val retrofit: Retrofit,
    private val productRepo: ProductRepo,
    private val orderBillRepo: OrderBillRepo,
    private val gson: Gson
) : SyncDataRepo {
    private val box: Box<SyncData> by lazy {
        boxStore.boxFor(
            SyncData::class.java
        )
    }

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

//    private val boxTrx: Box<Trx> by lazy {
//        boxStore.boxFor(
//            Trx::class.java
//        )
//    }

    private val boxOrder: Box<Order> by lazy {
        boxStore.boxFor(
            Order::class.java
        )
    }

    private val boxTrx: Box<Trx> by lazy {
        boxStore.boxFor(
            Trx::class.java
        )
    }

    private val boxOrderBill: Box<OrderBill> by lazy {
        boxStore.boxFor(
            OrderBill::class.java
        )
    }

    private val boxPaymentSales: Box<PaymentSales> by lazy {
        boxStore.boxFor(
            PaymentSales::class.java
        )
    }

    private val boxProductSales: Box<ProductSales> by lazy {
        boxStore.boxFor(
            ProductSales::class.java
        )
    }

    private val boxTaxSales: Box<TaxSales> by lazy {
        boxStore.boxFor(
            TaxSales::class.java
        )
    }

    private val boxTax: Box<Tax> by lazy {
        boxStore.boxFor(
            Tax::class.java
        )
    }

    private val boxUser: Box<User> by lazy {
        boxStore.boxFor(
            User::class.java
        )
    }

    private val syncLocked: ReentrantLock by lazy { ReentrantLock() }

    @Synchronized
    override fun syncTransactionDataEndUser(orderUniqueId: String): Completable {
        val obs = Single.fromCallable {
            Log.d(ConstVar.TAG, "Thread sync processing -> ${Thread.currentThread().name}")
            val order = boxOrder.query().equal(Order_.uniqueId, orderUniqueId).build()
                .findFirst()

            if (order != null) {
                val data: HashMap<String, Any> = hashMapOf()
                data["order"] = order
                data["trx"] = order.trx
                data["orderBills"] = order.orderBill
                data["productSales"] = order.productSales
                data["paymentSales"] = order.paymentSales
                data["taxSales"] = order.taxSales
                data["trxId"] = order.trxId

                return@fromCallable data
            }

            throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
        }

        return obs.flatMapCompletable {
            service.syncTransactionDataEndUser(it)
                .doFinally {
//                    syncLocked.unlock()
                }
        }
    }

    @Synchronized
    override fun syncTransactionDataPOS(syncDataUniqueId: String): Completable {

//        syncLocked.lock()
        val syncDataUniqueIdSynchronized = syncDataUniqueId

        val obs = Single.fromCallable {
            Log.d(ConstVar.TAG, "Thread sync processing -> ${Thread.currentThread().name}")

            val syncData = box.query()
                .equal(SyncData_.uniqueId, syncDataUniqueIdSynchronized)
                .equal(SyncData_.status, ConstVar.SYNC_STATUS_NOT_SYNC.toLong())
                .build().findFirst()

            if (syncData != null) {
//                syncData.status = ConstVar.SYNC_STATUS_QUEUED
//                box.put(syncData)
                return@fromCallable syncData
            }

            SyncData()
        }

        return obs.flatMap {
            if (it.id <= 0) {
                //Data local sudah kehapus worker di complate supaya tidak retry
                return@flatMap Single.fromCallable {
                    SyncData()
                }
            }

            return@flatMap service.syncTransactionDataPOS(it)
//                .doOnError {
//                    val syncData =
//                        box.query().equal(SyncData_.uniqueId, syncDataUniqueIdSynchronized).build()
//                            .findFirst()
//
//                    if (syncData != null) {
//                        syncData.status = ConstVar.SYNC_STATUS_NOT_SYNC
//                        box.put(syncData)
//                    }
//                }
                .map { res ->
                    if (res.isSuccess()) {
                        val data = res.data!!
                        val syncData =
                            box.query().equal(SyncData_.uniqueId, data.uniqueId!!).build()
                                .findFirst()

                        if (syncData != null) {
                            syncData.status = ConstVar.SYNC_STATUS_SYNCED
                            syncData.syncDate = DateTimeUtil.getCurrentDateTime()
                            box.put(syncData)

                            return@map syncData
                        }
                    }

                    throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
                }
        }.doFinally {
//            syncLocked.unlock()
        }
            .ignoreElement()
    }

    override fun syncTransactionDataHomeVisit(data: String): Single<None> {
        val visitReq = gson.fromJson(data, BookHomeVisit::class.java)
        return service.syncHomeVisitTransactionData(visitReq)
            .map {
                if (it.isSuccess()) {
                    return@map it.data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun syncDataMaster(): Completable {
        val productObs = productRepo.getAllProductsCloud()
        val merchantInfo = getMerchantInfo()
        val taxSetting = getTaxSetting()
        val zip =
//            Single.zip(
//                productObs,
//                merchantInfo,
//                BiFunction<List<Product>, ResponseApi<User>, Boolean> { _, _ ->
//                    Log.d(ConstVar.TAG, "All master data synced")
//                    true
//                })

            Single.zip(
                productObs,
                merchantInfo,
                taxSetting,
                Function3<List<Product>, ResponseApi<User>, Tax, Boolean> { _, _, _ ->
                    true
                }
            )
        return zip.flatMapCompletable {
            if (it) {
                return@flatMapCompletable Completable.complete()
            }

            Completable.error(Throwable(ConstVar.DATA_NULL))
        }
    }

    override fun getByDataType(type: String): SyncData? {
        return box.query().equal(SyncData_.type, type).build().findFirst()
    }

    override fun insertUpdate(syncData: SyncData) {
        val data = getByDataType(syncData.type)
        if (data != null) {
            data.syncDate = syncData.syncDate
            data.nextPage = data.nextPage.inc()
            box.put(data)
        } else {
            box.put(syncData)
        }
    }

    override fun save(syncData: SyncData): SyncData {
        val id = box.put(syncData)
        return box.get(id)
    }

    override fun getHistory(
        startDate: Long,
        endDate: Long,
        keyword: String
    ): Single<List<Trx>> {
        val startDateMillis = DateTimeUtil.getCurrentDateWithoutTime(
            if (startDate > 0) startDate else DateTimeUtil.getCurrentDateWithoutTime()
        )
        val endDateMillis = DateTimeUtil.getCurrentDateWithoutTime(
            if (endDate > 0) endDate else DateTimeUtil.getCurrentDateWithoutTime()
        )

        val prevDate = DateTimeUtil.getPreviousDate(startDateMillis)
        val nextDate = DateTimeUtil.getNextDate(endDateMillis)

        val queryMap: HashMap<String, String> = hashMapOf()
        queryMap["startDate"] = DateTimeUtil.getDateWithFormat(startDate, "dd/MM/YYYY")
        queryMap["endDate"] = DateTimeUtil.getDateWithFormat(endDate, "dd/MM/YYYY")

        if (keyword.isNotEmpty()) {
            queryMap["keyword"] = keyword
        }

        return service.getHistory(queryMap)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(
                        it,
                        retrofit
                    )
                }
            }
            .map {
                if (it.isSuccess()) {
                    val data = it.data!!
                    val trx = data.trx

                    val trxs: MutableList<Trx> = mutableListOf()
                    Single.fromCallable { trx }

                    return@map trx
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))

            }

        /*return service.getHistory(queryMap)
            .map {
               val a = it
                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }*/
    }

    override fun getAllTransaction(
        startDate: Long,
        endDate: Long,
        keyword: String
    ): Single<List<Order>> {

        val startDateMillis = DateTimeUtil.getCurrentDateWithoutTime(
            if (startDate > 0) startDate else DateTimeUtil.getCurrentDateWithoutTime()
        )
        val endDateMillis = DateTimeUtil.getCurrentDateWithoutTime(
            if (endDate > 0) endDate else DateTimeUtil.getCurrentDateWithoutTime()
        )

        val prevDate = DateTimeUtil.getPreviousDate(startDateMillis)
        val nextDate = DateTimeUtil.getNextDate(endDateMillis)

        val queryMap: HashMap<String, String> = hashMapOf()
        queryMap["startDate"] = DateTimeUtil.getDateWithFormat(startDate, "dd/MM/YYYY")
        queryMap["endDate"] = DateTimeUtil.getDateWithFormat(endDate, "dd/MM/YYYY")

        if (keyword.isNotEmpty()) {
            queryMap["keyword"] = keyword
        }

        return service.getTransaction(queryMap)
            .map {
                if (it.isSuccess()) {
                    val res = it.data!!

                    res.order?.forEach { order ->
                        val existingOrder =
                            boxOrder.query().equal(Order_.uniqueId, order.uniqueId).build()
                                .find()
                        boxOrder.remove(existingOrder)//duplikat uniqueid di db di hapus

                        order.id = 0//reset id (will generate new one)
                        var orderId = boxOrder.put(order)
                        var newOrder = boxOrder.get(orderId)

                        val trxs: MutableList<Trx> = mutableListOf()
                        res.trx?.forEach { trx ->
                            if (newOrder.trxId == trx.trxId) {
                                val dataLocal = boxTrx.query()
                                    .equal(Trx_.trxId, trx.trxId).build().find()
                                boxTrx.remove(dataLocal)//duplikat uniqueid di db di hapus

//                                trx.id = 0.toString()
                                order.status = trx.status
                                order.address = trx.address
                                boxOrder.put(order)
                            }
                        }

                        boxTrx.put(trxs)

                        val orderBills: MutableList<OrderBill> = mutableListOf()
                        res.orderBills?.forEach { orderBill ->
                            if (newOrder.uniqueId == orderBill.orderUniqueId) {
                                val dataLocal = boxOrderBill.query()
                                    .equal(OrderBill_.uniqueId, orderBill.uniqueId).build().find()
                                boxOrderBill.remove(dataLocal)//duplikat uniqueid di db di hapus

                                orderBill.id = 0
                                orderBill.order.target = newOrder
                                orderBills.add(orderBill)
                            }
                        }

                        boxOrderBill.put(orderBills)

                        val productSalesList: MutableList<ProductSales> = mutableListOf()
                        res.productSales?.forEach { productSales ->
                            if (newOrder.uniqueId == productSales.orderUniqueId) {
                                val dataLocal = boxProductSales.query()
                                    .equal(ProductSales_.uniqueId, productSales.uniqueId).build()
                                    .find()
                                boxProductSales.remove(dataLocal)//duplikat uniqueid di db di hapus

                                productSales.id = 0
                                productSales.order.target = newOrder
                                productSalesList.add(productSales)
                            }
                        }
                        boxProductSales.put(productSalesList)

                        val taxSalesList: MutableList<TaxSales> = mutableListOf()
                        res.taxSales?.forEach { taxSales ->
                            if (newOrder.uniqueId == taxSales.orderUniqueId) {
                                val dataLocal = boxTaxSales.query()
                                    .equal(TaxSales_.uniqueId, taxSales.uniqueId).build().find()
                                boxTaxSales.remove(dataLocal)//duplikat uniqueid di db di hapus

                                taxSales.id = 0
                                taxSales.order.target = newOrder
                                taxSalesList.add(taxSales)
                            }
                        }
                        boxTaxSales.put(taxSalesList)

                        val paymentSalesList: MutableList<PaymentSales> = mutableListOf()
                        res.paymentSales?.forEach { ps ->
                            if (newOrder.uniqueId == ps.orderUniqueId) {
                                val db = boxPaymentSales.query()
                                    .equal(PaymentSales_.uniqueId, ps.uniqueId).build().find()
                                boxPaymentSales.remove(db)

                                ps.id = 0
                                ps.order.target = newOrder
                                paymentSalesList.add(ps)
                            }
                        }

                        boxPaymentSales.put(paymentSalesList)

                    }

                    return@map if (keyword.isNotEmpty()) {
                        boxOrder.query()
                            .equal(Order_.isClose, true)
                            .contains(Order_.trxId, keyword)
                            .or()
                            .contains(Order_.billNo, keyword)
                            .build()
                            .find()
                    } else {
                        boxOrder.query()
                            .equal(Order_.isClose, true)
                            .greater(
                                Order_.businessDate,
                                DateTimeUtil.getCurrentDateWithoutTime(prevDate)
                            )
                            .less(
                                Order_.businessDate,
                                DateTimeUtil.getCurrentDateWithoutTime(nextDate)
                            )
                            .orderDesc(Order_.updatedAt)
                            .build()
                            .find()
                    }
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
            .onErrorResumeNext {
                val trxList = if (keyword.isNotEmpty()) {
                    boxOrder.query()
                        .equal(Order_.isClose, true)
                        .contains(Order_.trxId, keyword)
                        .or()
                        .contains(Order_.billNo, keyword)
                        .build()
                        .find()
                } else {
                    boxOrder.query()
                        .equal(Order_.isClose, true)
                        .greater(
                            Order_.businessDate,
                            DateTimeUtil.getCurrentDateWithoutTime(prevDate)
                        )
                        .less(Order_.businessDate, DateTimeUtil.getCurrentDateWithoutTime(nextDate))
                        .orderDesc(Order_.updatedAt)
                        .build()
                        .find()
                }

                Single.fromCallable { trxList }
            }
    }


    override fun removeAll() {
//        boxTrx.removeAll()
        box.removeAll()
        boxTaxSales.removeAll()
        boxOrder.removeAll()
        boxOrderBill.removeAll()
        boxProductSales.removeAll()
    }

    override fun remove(type: String) {
        box.query().equal(SyncData_.type, type).build().remove()
    }


    override fun createSync(param: CreateSyncUseCase.Params): Completable {
        //TODO: if failed(internet not connected), save sync data will be re sync later
        return service.createSync(
            ObjectFactory.createSync(
                param.type,
                param.data.toString(),
                param.businessDate
            )
        )
    }

    override fun createSyncData(order: Order) {
        val data: HashMap<String, Any> = hashMapOf()
        data["order"] = order
        data["orderBills"] = order.orderBill
        data["productSales"] = order.productSales
        data["paymentSales"] = order.paymentSales
        data["taxSales"] = order.taxSales
        data["trxId"] = order.trxId

        val syncData =
            ObjectFactory.createSyncData(Order::class.java.simpleName, gson.toJson(data))

        box.put(syncData)
    }

    override fun getTransactionOnlineOrder(): Single<ResponseApi<OnlineOrder>> {
        return service.getTransactionOnlineOrder()
            .map {
                if (it.isSuccess()) {
                    val res = it.data!!
                    res.order?.forEach { order ->
                        val existingOrder =
                            boxOrder.query().equal(Order_.uniqueId, order.uniqueId).build().find()
                        boxOrder.remove(existingOrder)//duplikat uniqueid di db di hapus

//                        val mOrder = boxOrder.query()
//                            .equal(Order_.businessDate, DateTimeUtil.getCurrentDateWithoutTime())
//                            .orderDesc(Order_.createdAt).build()
//                            .findFirst()
//                        val orderNo = mOrder?.orderNo?.toInt()?.inc()?.toString() ?: "1"

                        order.id = 0//reset id (will generate new one)
                        order.types = 1 //flag online order
                        val orderId = boxOrder.put(order)
                        val newOrder = boxOrder.get(orderId)

                        val orderBills: MutableList<OrderBill> = mutableListOf()
                        res.orderBills?.forEach { orderBill ->
                            if (newOrder.uniqueId == orderBill.orderUniqueId) {
                                val dataLocal = boxOrderBill.query()
                                    .equal(OrderBill_.uniqueId, orderBill.uniqueId).build().find()
                                boxOrderBill.remove(dataLocal)//duplikat uniqueid di db di hapus

                                orderBill.id = 0
                                orderBill.order.target = newOrder
                                orderBills.add(orderBill)
                            }
                        }

                        boxOrderBill.put(orderBills)

                        val productSalesList: MutableList<ProductSales> = mutableListOf()
                        res.productSales?.forEach { productSales ->
                            if (newOrder.uniqueId == productSales.orderUniqueId) {
                                val dataLocal = boxProductSales.query()
                                    .equal(ProductSales_.uniqueId, productSales.uniqueId).build()
                                    .find()
                                boxProductSales.remove(dataLocal)//duplikat uniqueid di db di hapus

                                productSales.id = 0
                                productSales.order.target = newOrder
                                productSalesList.add(productSales)
                            }
                        }
                        boxProductSales.put(productSalesList)

                        val taxSalesList: MutableList<TaxSales> = mutableListOf()
                        res.taxSales?.forEach { taxSales ->
                            if (newOrder.uniqueId == taxSales.orderUniqueId) {
                                val dataLocal = boxTaxSales.query()
                                    .equal(TaxSales_.uniqueId, taxSales.uniqueId).build().find()
                                boxTaxSales.remove(dataLocal)//duplikat uniqueid di db di hapus

                                taxSales.id = 0
                                taxSales.order.target = newOrder
                                taxSalesList.add(taxSales)
                            }
                        }
                        boxTaxSales.put(taxSalesList)

                    }

//                    it.data?.let {
//
//                        if (it.order != null) {
//                            it.order?.indices?.forEach { i ->
//                                val mOrder = it.order!![i]
//                                val existingOrder = boxOrder.query().equal(Order_.uniqueId,mOrder.uniqueId).build().find()
//                                if (existingOrder.isNotEmpty()) {
//                                    boxOrder.remove(existingOrder)//duplikat uniqueid sama di db di hapus
//                                }
//
//                                it.order!!.get(i).id = 0
//                                boxOrder.put(it.order!!.get(i))
//                            }
//                        }
//
//                        if (it.productSales != null) {
//                            it.productSales?.forEach { i ->
//                                i.id = 0
//                                it.order?.forEach { k ->
//                                    if(i.orderUniqueId .equals(k.uniqueId)){
//                                        val order = boxOrder.query().equal(Order_.uniqueId,k.uniqueId).build().findFirst()
//                                        if( order != null) {
//                                            i.order.target = order
//                                            boxProductSales.put(i)
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        if (it.paymentSales != null) {
//                            it.paymentSales?.forEach { i ->
//                                i.id = 0
//                                boxPaymentSales.put(i)
//                                it.order?.forEach { k ->
//                                    if(i.orderUniqueId .equals(k.uniqueId )){
//                                        val order = boxOrder.query().equal(Order_.uniqueId,k.uniqueId).build().findFirst()
//                                        if( order != null) {
//                                            i.order.target = order
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        if (it.orderBills != null) {
//                            it.orderBills?.forEach { i ->
//                                i.id = 0
//                                boxOrderBill.put(i)
//                                it.order?.forEach { k ->
//                                    if(i.orderUniqueId .equals(k.uniqueId )){
//                                        val order = boxOrder.query().equal(Order_.uniqueId,k.uniqueId).build().findFirst()
//                                        if( order != null) {
//                                            i.order.target = order
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        if (it.taxSales!!.size > 0) {
//                            it.taxSales?.forEach { i ->
//                                i.id = 0
//                                boxTaxSales.put(i)
//                                it.order?.forEach { k ->
//                                    if(i.orderUniqueId .equals(k.uniqueId )){
//                                        val order = boxOrder.query().equal(Order_.uniqueId,k.uniqueId).build().findFirst()
//                                        if( order != null) {
//                                            i.order.target = order
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//
//                    }
                }
                it
            }
    }

    override fun getMerchantInfo(): Single<ResponseApi<User>> {
        return service.getProfileMerchant()
            .map {
                if (it.isSuccess()) {
                    it.data?.let { user ->
                        user.roleName = ConstVar.USER_ROLE_MERCHANT

                        boxUser.put(user)
                    }
                }
                it
            }
    }

    override fun getTaxSetting(): Single<Tax> {
        return service.getTaxSetting()
            .map {

                if (it.isSuccess()) {
                    val data = it.data!!
                    boxTax.removeAll()
                    boxTax.put(data)
                    return@map data
                }

                return@map Tax()
//                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getTaxSetting(merchantId: Long): Single<Tax> {
        return service.getMerchantTax(merchantId)
            .map {
                if (it.isSuccess()) {
                    val data = it.data!!
                    boxTax.removeAll()
                    boxTax.put(data)
                    return@map data
                }
                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun createOrderBill(orderUniqueId: String): OrderBill {
        return boxOrderBill.query().equal(OrderBill_.orderUniqueId, orderUniqueId).build()
            .findFirst()!!
    }

    override fun createProductSales(orderUniqueId: String): ProductSales {
        return boxProductSales.query().equal(ProductSales_.orderUniqueId, orderUniqueId).build()
            .findFirst()!!
    }

    override fun getListOrder(keyword: String): Single<List<Order>> {
        val data =
            boxOrder.query().contains(Order_.trxId, keyword).or().contains(Order_.billNo, keyword)
                .build().find()

        return Single.fromCallable { data }

    }

    override fun closeOnlineOrder(params: CloseOnlineOrderUseCase.Params): Completable {
        return service.closeOnlineOrder(params.trxId)
    }

    override fun voidOrder(params: VoidOrderUseCase.Params): Completable {
        return service.voidOrder(params.trxId)
    }

    override fun downloadReport(month: String, year: String, url: String): Single<ResponseBody> {
        val mUrl = "${url}trx/report?month=$month&year=$year"
        return service.downloadReportFile(mUrl)
//            .map {
//                if (it.isSuccess()) {
//                    val data = it.data!!
//                    return@map data
//                }
//                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))


//                it
//            }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            // todo change the file location/name according to your needs
            val futureStudioIconFile =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "report"
                )
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d(
                        ConstVar.TAG,
                        "file download: $fileSizeDownloaded of $fileSize"
                    )
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

}