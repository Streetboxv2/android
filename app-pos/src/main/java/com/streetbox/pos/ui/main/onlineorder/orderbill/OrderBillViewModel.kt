package com.streetbox.pos.ui.main.onlineorder.orderbil
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailViewEvent
import com.streetbox.pos.ui.main.MainViewEvent
import com.streetbox.pos.ui.main.onlineorder.orderbill.OrderBillViewEvent
import com.streetbox.pos.ui.main.order.OrderViewEvent
import com.zeepos.domain.interactor.CloseOnlineOrderUseCase
import com.zeepos.domain.interactor.CreateSyncUseCase
import com.zeepos.domain.interactor.order.CloseOrderUseCase
import com.zeepos.domain.interactor.order.GetRecentOpenOrCreateOrderUseCase
import com.zeepos.domain.interactor.orderbill.GetOrderBillByUniqueIdUseCase
import com.zeepos.domain.interactor.payment.GetQRCodePaymentUseCase
import com.zeepos.domain.interactor.productsales.GetRecentOrderUseCase
import com.zeepos.domain.interactor.productsales.RemoveProductSalesUseCase
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.master.SyncData
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

class OrderBillViewModel @Inject constructor(
    private val getOrderBillByUniqueIdUseCase: GetOrderBillByUniqueIdUseCase,
    private val closeOnlineOrderUseCase: CloseOnlineOrderUseCase,
    private val userRepository: UserRepository,
    private val getQRCodePaymentUseCase: GetQRCodePaymentUseCase,
    private val closeOrderUseCase: CloseOrderUseCase,
    private val removeProductSalesUseCase: RemoveProductSalesUseCase,
    private val createSyncUseCase: CreateSyncUseCase,
    private val syncDataRepo: SyncDataRepo
    ) : BaseViewModel<OrderBillViewEvent>() {
    fun getOrderBill(uniqueId: String) {
        val disposable =
            getOrderBillByUniqueIdUseCase.execute(GetOrderBillByUniqueIdUseCase.Params(uniqueId))
                .subscribe({
                    viewEventObservable.postValue(OrderBillViewEvent.GetOrderBillSuccess(it))
                }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun saveSyncData(syncData: SyncData): SyncData {
        return syncDataRepo.save(syncData)
    }


    fun closeOnlineOrder(trxId: String) {
        val disposable = closeOnlineOrderUseCase.execute(
            CloseOnlineOrderUseCase.Params(
                trxId
            )
        )
            .subscribe({
                Log.d(ConstVar.TAG, "Close Online Order success!")
                viewEventObservable.postValue(OrderBillViewEvent.CloseOnlineOrderSuccess)
            }, {
                it.printStackTrace()
                viewEventObservable.postValue(
                    OrderBillViewEvent.CloseOnlineOrderFailed(
                        "failed"
                    )
                )
            })

        addDisposable(disposable)
    }

    fun getProfileMerchantLocal() : User?{
        return userRepository.getProfileMerchant()
    }

    fun getOperator() : User?{
        return userRepository.getOperator()
    }

    fun closeOrder(uniqueId: String) {
        val disposable = closeOrderUseCase.execute(CloseOrderUseCase.Params(uniqueId))
            .subscribe({
                viewEventObservable.postValue(OrderBillViewEvent.CloseOrderSuccess)
            }, {
                it.printStackTrace() })

        addDisposable(disposable)
    }

    fun removeProductSales(productSales: ProductSales) {
        val disposable =
            removeProductSalesUseCase.execute(RemoveProductSalesUseCase.Params(productSales))
                .subscribe {
                    viewEventObservable.postValue(OrderBillViewEvent.OnRemoveProductSuccess)
                }

        addDisposable(disposable)
    }

    fun createSync(type: String,businessDate: Long, data: String) {
        val disposable = createSyncUseCase.execute(
            CreateSyncUseCase.Params(
                type,
                businessDate,
                data
            )
        )
            .subscribe({
                Log.d(ConstVar.TAG, "Create sync success!")
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }

}