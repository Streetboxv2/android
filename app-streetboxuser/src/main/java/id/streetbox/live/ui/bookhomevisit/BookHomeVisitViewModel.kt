package id.streetbox.live.ui.bookhomevisit

import com.zeepos.domain.interactor.address.GetAllAddressUseCase
import com.zeepos.domain.interactor.address.SaveAddressUseCase
import com.zeepos.domain.interactor.foodtruck.GetFoodTruckHomeVisitUseCase
import com.zeepos.domain.repository.RemoteRepository
import com.zeepos.models.entities.AvailableHomeVisitBookDate
import com.zeepos.models.entities.None
import com.zeepos.models.master.Address
import com.zeepos.ui_base.ui.BaseViewModel
import com.zeepos.utilities.DateTimeUtil
import id.streetbox.live.ui.main.address.AddressViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Arif S. on 6/25/20
 */
class BookHomeVisitViewModel @Inject constructor(
    private val getFoodTruckHomeVisitUseCase: GetFoodTruckHomeVisitUseCase,
    private val getAllAddressUseCase: GetAllAddressUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
    val remoteRepository: RemoteRepository
) : BaseViewModel<BookHomeVisitViewEvent>() {

    fun callGetAddressPrimary() {
        val disposable = remoteRepository.callGetAddressPrimary()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewEventObservable.postValue(BookHomeVisitViewEvent.OnSuccessListAddress(it))
            }, {
                viewEventObservable.postValue(
                    BookHomeVisitViewEvent.OnFailedAddress(it)
                )
            })
        addDisposable(disposable)
    }

    fun getBookAvailableDate(foodTruckId: Long) {

        val disposable =
            getFoodTruckHomeVisitUseCase.execute(GetFoodTruckHomeVisitUseCase.Params(foodTruckId))
                .subscribe({
                    viewEventObservable.value =
                        BookHomeVisitViewEvent.GetFoodTruckHomeVisitDataSuccess(
                            it
                        )
                }, {
                    it.message?.let { errorMessarge ->
                        viewEventObservable.value
                        BookHomeVisitViewEvent.GetFoodTruckHomeVisitDataFailed(
                            errorMessarge
                        )
                    }
                })
        addDisposable(disposable)

    }

    fun collectDate(data: List<AvailableHomeVisitBookDate>): Map<Long, List<AvailableHomeVisitBookDate>> {
        val results: MutableMap<Long, MutableList<AvailableHomeVisitBookDate>> = mutableMapOf()

        data.forEach {
            val startDateMillis = DateTimeUtil.getDateFromString(it.startDate)?.time ?: 0L
            val scheduledDate = DateTimeUtil.getCurrentDateWithoutTime(startDateMillis)

            it.scheduleDate = scheduledDate

            if (results.containsKey(scheduledDate)) {
                val availableDateList = results[scheduledDate]!!
                availableDateList.add(it)

            } else {
                val availableDateList: MutableList<AvailableHomeVisitBookDate> = mutableListOf()
                availableDateList.add(it)
                results[scheduledDate] = availableDateList
            }
        }

        return results

    }

    fun calculate(data: Set<AvailableHomeVisitBookDate>) {
        var totalDeposit = 0.0
        data.forEach {
            totalDeposit += it.deposit
        }

        viewEventObservable.postValue(BookHomeVisitViewEvent.OnCalculateDone(totalDeposit))

    }

    fun getAllAddress() {
        val disposable = getAllAddressUseCase.execute(None())
            .subscribe({
                viewEventObservable.value = BookHomeVisitViewEvent.GetAllAddressSuccess(it)
            }, { it.printStackTrace() })

        addDisposable(disposable)
    }

    fun saveAddress(address: Address) {
        val disposable = saveAddressUseCase.execute(SaveAddressUseCase.Params(address))
            .subscribe {}
        addDisposable(disposable)
    }
}