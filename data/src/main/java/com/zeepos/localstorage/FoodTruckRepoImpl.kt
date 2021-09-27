package com.zeepos.localstorage

import com.zeepos.domain.repository.FoodTruckRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.AvailableHomeVisitBookDate
import com.zeepos.models.master.FoodTruck
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import io.objectbox.BoxStore
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 7/15/20
 */
class FoodTruckRepoImpl @Inject constructor(
    private val retrofit: Retrofit,
    boxStore: BoxStore
) : FoodTruckRepo {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    override fun getAllFoodTruckHomeVisit(page: Int): Single<List<FoodTruck>> {
        val queryMap: HashMap<String, String> = hashMapOf()
        queryMap["page"] = "$page"

        return service.getFoodTruckHomeVisit(queryMap)
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
                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getFoodTruckHomeVisit(merchantId: Long): Single<List<AvailableHomeVisitBookDate>> {
        return service.getHomeVisitBookDate(merchantId)
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
                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }
}