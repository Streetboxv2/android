package com.streetbox.pos.ui.main.product

import com.zeepos.domain.interactor.GetAllTransactionSyncData
import com.zeepos.domain.interactor.GetOnlineOrderUseCase
import com.zeepos.domain.interactor.GetTaxUseCase
import com.zeepos.domain.interactor.product.GetAllProductCloudUseCase
import com.zeepos.domain.interactor.product.GetAllProductUseCase
import com.zeepos.domain.repository.LocalPreferencesRepository
import com.zeepos.domain.repository.OrderRepo
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.entities.None
import com.zeepos.ui_base.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Arif S. on 2019-11-02
 */
class ProductViewModel @Inject constructor(
    private val getAllProductUseCase: GetAllProductUseCase,
    private val getAllProductCloudUseCase: GetAllProductCloudUseCase,
    private val getAllTransactionSyncData: GetAllTransactionSyncData,
    private val localPreferencesRepository: LocalPreferencesRepository,
    private val getOnlineOrderUseCase: GetOnlineOrderUseCase,
    private val syncDataRepo: SyncDataRepo,
    private val orderRepo: OrderRepo
) : BaseViewModel<ProductViewEvent>() {

    fun getAllProducts() {
        val disposable = getAllProductUseCase.execute(None())
            .subscribe({
                viewEventObservable.postValue(ProductViewEvent.GetAllProductsSuccess(it))
            },
                { it.printStackTrace() })
        addDisposable(disposable)

    }

    fun getAllProductsCloud() {
        val disposable = getAllProductCloudUseCase.execute(None())
            .subscribe({
                viewEventObservable.postValue(ProductViewEvent.GetAllProductsSuccess(it))
            },
                { it.printStackTrace() })
        addDisposable(disposable)

    }

    fun getAllTransaction(){
//        val disposable = getAllTransactionSyncData.execute(None()).subscribe({
//            viewEventObservable.postValue(ProductViewEvent.GetAllTransactionSuccess(it))
//        }, {
//            it.printStackTrace()})
//        addDisposable(disposable)
    }

    fun deleteSession(){
        localPreferencesRepository.deleteSession()
        syncDataRepo.removeAll()
        orderRepo.removeAll()
    }


    fun getOnlineOrder(){
        val disposable =
            getOnlineOrderUseCase.execute(None())
                .subscribe({
                    viewEventObservable.postValue(ProductViewEvent.GetOnlineOrderSuccess(it.data!!))
                }, {
                    it.printStackTrace()})

        addDisposable(disposable)
    }

    fun getTax() {
//        val disposable = getTaxUseCase.execute(None())
//            .subscribe({
//                viewEventObservable.postValue(ProductViewEvent.GetTaxSuccess(it))
//            },
//                { it.printStackTrace() })
//        addDisposable(disposable)

    }


}
