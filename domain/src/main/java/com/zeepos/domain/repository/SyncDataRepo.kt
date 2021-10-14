package com.zeepos.domain.repository

import com.zeepos.domain.interactor.CloseOnlineOrderUseCase
import com.zeepos.domain.interactor.CreateSyncUseCase
import com.zeepos.domain.interactor.VoidOrderUseCase
import com.zeepos.models.entities.None
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.SyncData
import com.zeepos.models.master.Tax
import com.zeepos.models.master.User
import com.zeepos.models.transaction.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody

/**
 * Created by Arif S. on 6/17/20
 */
interface SyncDataRepo {
    fun syncTransactionDataEndUser(orderUniqueId: String): Completable
    fun syncTransactionDataPOS(syncDataUniqueId: String): Completable
    fun syncTransactionDataHomeVisit(data: String): Single<None>
    fun syncDataMaster(): Completable
    fun getByDataType(type: String): SyncData?
    fun insertUpdate(syncData: SyncData)
    fun save(syncData: SyncData): SyncData
    fun getAllTransaction(startDate: Long, endDate: Long, keyword: String): Single<List<Order>>
    fun removeAll()
    fun remove(type: String)
    fun createSync(params: CreateSyncUseCase.Params): Completable
    fun createSyncData(order: Order)
    fun getTransactionOnlineOrder(): Single<ResponseApi<OnlineOrder>>
    fun getMerchantInfo(): Single<ResponseApi<User>>
    fun getTaxSetting(): Single<Tax>
    fun getTaxSetting(merchantId: Long): Single<Tax>
    fun getTaxSettingSales(merchantId: Long): Single<TaxSales>
    fun createOrderBill(orderUniqueId: String): OrderBill
    fun createProductSales(orderUniqueId: String): ProductSales
    fun getListOrder(keyword: String): Single<List<Order>>
    fun closeOnlineOrder(params: CloseOnlineOrderUseCase.Params): Completable
    fun voidOrder(params: VoidOrderUseCase.Params): Completable
    fun downloadReport(month: String, year: String, url: String): Single<ResponseBody>
}