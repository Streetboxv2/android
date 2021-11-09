package id.streetbox.live.ui.menu

import com.zeepos.domain.interactor.GetTaxUseCase
import com.zeepos.domain.interactor.foodtruck.GetFoodTruckHomeVisitUseCase
import com.zeepos.domain.interactor.order.GetRecentOpenOrCreateOrderUseCase
import com.zeepos.domain.interactor.order.UpdateOrderUseCase
import com.zeepos.domain.interactor.orderbill.CalculateOrderUseCase
import com.zeepos.domain.interactor.parkingspace.GetMerchantParkingScheduleUseCase
import com.zeepos.domain.interactor.product.GetAllProductByMerchantIdUseCase
import com.zeepos.domain.interactor.productsales.CreateProductSalesUseCase
import com.zeepos.domain.interactor.productsales.RemoveProductSalesByProductUseCase
import com.zeepos.domain.interactor.productsales.UpdateOrRemoveProductSalesUseCase
import com.zeepos.domain.interactor.productsales.UpdateProductSalesUseCase
import com.zeepos.domain.interactor.user.GetUserInfoUseCase
import com.zeepos.models.entities.None
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.payment.PaymentViewEvent
import com.zeepos.ui_base.ui.BaseViewModel
import id.streetbox.live.ui.main.MainViewEvent
import id.streetbox.live.ui.orderreview.pickup.PickupOrderReviewViewEvent
import javax.inject.Inject

/**
 * Created by Arif S. on 6/20/20
 */
class MenuViewModel @Inject constructor(
    private val getAllProductByMerchantIdUseCase: GetAllProductByMerchantIdUseCase,
    private val getRecentOpenOrCreateOrderUseCase: GetRecentOpenOrCreateOrderUseCase,
    private val calculateOrderUseCase: CalculateOrderUseCase,
    private val createProductSalesUseCase: CreateProductSalesUseCase,
    private val removeProductSalesByProductUseCase: RemoveProductSalesByProductUseCase,
    private val updateOrRemoveProductSalesUseCase: UpdateOrRemoveProductSalesUseCase,
    private val updateProductSalesUseCase: UpdateProductSalesUseCase,
    private val getFoodTruckHomeVisitUseCase: GetFoodTruckHomeVisitUseCase,
    private val getMerchantParkingScheduleUseCase: GetMerchantParkingScheduleUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getTaxUseCase: GetTaxUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase
) : BaseViewModel<MenuViewEvent>() {

    fun getRecentOrder(merchantId: Long, foodTruck: FoodTruck?) {
        val disposable = getRecentOpenOrCreateOrderUseCase.execute(
            GetRecentOpenOrCreateOrderUseCase.Params(merchantId, foodTruck)
        )
            .subscribe({
                viewEventObservable.postValue(MenuViewEvent.GetOrCreateOrderSuccess(it))
            }, { viewEventObservable.postValue(MenuViewEvent.OrderFailedCreated) })

        addDisposable(disposable)
    }


    fun getUserInfoCloud(id: Long = 0) {
        val disposable = getUserInfoUseCase.execute(GetUserInfoUseCase.Params(id))
            .subscribe({
                viewEventObservable.postValue(MenuViewEvent.GetUserInfoSuccess(it))
            }, {
                it.message?.let { errorMessage ->
                    viewEventObservable.postValue(MenuViewEvent.GetUserInfoFailed(errorMessage))
                }
            })
        addDisposable(disposable)
    }


    fun getProduct(filter: String, merchantId: Long) {
        val disposable = getAllProductByMerchantIdUseCase.execute(
            GetAllProductByMerchantIdUseCase.Params(filter, merchantId)
        )
            .subscribe({
                viewEventObservable.value = MenuViewEvent.GetProductSuccess(it)
            }, {
                it.printStackTrace()
                viewEventObservable.value = MenuViewEvent.GetProductFailed(it.message.toString())
            })

        addDisposable(disposable)
    }

    fun addItem(product: Product, order: Order) {
        val disposable =
            createProductSalesUseCase.execute(
                CreateProductSalesUseCase.Params(
                    product,
                    order,
                    true
                )
            ).subscribe({
                viewEventObservable.postValue(MenuViewEvent.AddItemSuccess(it))
            }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun removeProductSales(product: Product, order: Order) {
        val disposable =
            removeProductSalesByProductUseCase.execute(
                RemoveProductSalesByProductUseCase.Params(
                    product, order
                )
            )
                .subscribe {
                    viewEventObservable.postValue(MenuViewEvent.OnRemoveProductSuccess)
                }

        addDisposable(disposable)
    }

    fun updateProductSales(productSales: ProductSales) {
        val disposable =
            updateOrRemoveProductSalesUseCase.execute(
                UpdateOrRemoveProductSalesUseCase.Params(
                    productSales
                )
            )
                .subscribe({
                    viewEventObservable.postValue(
                        MenuViewEvent.UpdateProductSalesSuccess
                    )
                }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun calculateOrder(order: Order) {
        val disposable = calculateOrderUseCase.execute(CalculateOrderUseCase.Params(order))
            .subscribe({
                viewEventObservable.postValue(MenuViewEvent.OnCalculateDone(it))
            }, {
                it.printStackTrace()
            })

        addDisposable(disposable)
    }

    fun updateOrder(order: Order) {
        val disposable = updateOrderUseCase.execute(UpdateOrderUseCase.Params(order))
            .subscribe({
                viewEventObservable.postValue(MenuViewEvent.UpdateOrderSuccess)
            }, {
                it.printStackTrace()
            })
        addDisposable(disposable)
    }


    fun getMerchantTax(merchantId: Long) {
        val disposable = getTaxUseCase.execute(GetTaxUseCase.Params(merchantId))
            .subscribe({
                viewEventObservable.postValue(MenuViewEvent.GetTaxSuccess(it))
            },
                {
                    it.printStackTrace()

                })
        addDisposable(disposable)
    }

    fun getMerchantParkingSchedule(merchantId: Long, typeId: Long) {
        val disposable = getMerchantParkingScheduleUseCase.execute(
            GetMerchantParkingScheduleUseCase.Params(merchantId, typeId)
        )
            .subscribe({
                viewEventObservable.value = MenuViewEvent.GetMerchantScheduleSuccess(it)
            }, {
                it.message?.let { errorMessage ->
                    viewEventObservable.value =
                        MenuViewEvent.GetMerchantScheduleFailed(
                            errorMessage
                        )
                }
            })
        addDisposable(disposable)
    }

    fun getBookAvailableDate(foodTruckId: Long) {
        val disposable =
            getFoodTruckHomeVisitUseCase.execute(GetFoodTruckHomeVisitUseCase.Params(foodTruckId))
                .subscribe({
                    viewEventObservable.value =
                        MenuViewEvent.GetFoodTruckHomeVisitDataSuccess(
                            it
                        )
                }, {
                    it.message?.let { errorMessarge ->
                        viewEventObservable.value =
                            MenuViewEvent.GetFoodTruckHomeVisitDataFailed(
                                errorMessarge
                            )
                    }
                })
        addDisposable(disposable)

    }

}