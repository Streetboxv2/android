package com.zeepos.localstorage

import com.zeepos.domain.repository.LogsRepo
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.Logs
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Arif S. on 7/1/20
 */
class LogsRepoImpl @Inject constructor(
    private val retrofit: Retrofit,
    private val syncDataRepo: SyncDataRepo
) : LogsRepo {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    override fun getLogs(isLoadMore: Boolean): Single<List<Logs>> {

        val queryMap: MutableMap<String, String> = mutableMapOf()
        val syncData = syncDataRepo.getByDataType(this::class.java.simpleName)

//        queryMap["sort"] = "created_at,desc"

        if (!isLoadMore) {
            queryMap["page"] = "1"
        } else {
            if (syncData != null) {
                queryMap["page"] = syncData.nextPage.toString()
            } else {
                queryMap["page"] = "1"
            }
        }

        return service.getLogs(queryMap)
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

                    if (data.isNotEmpty()) {
                        if (it.page == 1) {
                            syncDataRepo.remove(this::class.java.simpleName)
                        }

                        syncDataRepo.insertUpdate(
                            ObjectFactory.createSyncData(
                                this::class.java.simpleName,
                                it.nextPage
                            )
                        )
                    }

                    return@map data
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))

            }
    }
}