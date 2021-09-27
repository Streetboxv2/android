package com.zeepos.localstorage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.auth0.android.jwt.JWT
import com.zeepos.domain.interactor.user.LoginPosUseCase
import com.zeepos.domain.repository.ParkingSlotSalesRepo
import com.zeepos.domain.repository.SyncDataRepo
import com.zeepos.domain.repository.TaskOperatorRepo
import com.zeepos.domain.repository.UserRepository
import com.zeepos.models.ConstVar
import com.zeepos.models.master.User
import com.zeepos.models.master.UserAuthData
import com.zeepos.models.master.User_
import com.zeepos.models.transaction.TaskOperator
import com.zeepos.remotestorage.RemoteService
import com.zeepos.remotestorage.RetrofitException
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.FileUtil
import io.objectbox.Box
import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject
import kotlin.math.min


/**
 * Created by Arif S. on 5/7/20
 */
class UserRepositoryImpl @Inject internal constructor(
    boxStore: BoxStore,
    private val retrofit: Retrofit,
    private val storage: Storage,
    private val taskOperatorRepo: TaskOperatorRepo,
    private val parkingSlotSalesRepo: ParkingSlotSalesRepo,
    private val syncDataRepo: SyncDataRepo
) : UserRepository {

    private val service: RemoteService by lazy {
        retrofit.create(
            RemoteService::class.java
        )
    }

    private val box: Box<User> by lazy {
        boxStore.boxFor(
            User::class.java
        )
    }

    override fun insertUpdateUser(user: User) {
        box.put(user)
    }

    override fun insertUpdateUsers(users: List<User>) {
        return box.put(users)
    }

    override fun getAll(): List<User> {
        return box.all
    }

    override fun getAllOperator(): List<User> {
        return box.query().equal(User_.roleName, ConstVar.USER_ROLE_OPERATOR).build().find()
    }

    override fun getAllOperatorCloud(): Maybe<List<User>> {
        return service.getMerchantOperators()
            .flatMap {
                service.getAllFoodTruckTask()
                    .map { res ->
                        if (res.isSuccess()) {
                            val taskOperators = res.data!!
                            if (taskOperators.isNotEmpty()) {
                                val taskUpdateList: MutableList<TaskOperator> = mutableListOf()

                                for (taskOperator in taskOperators) {
                                    val taskDbLocal = taskOperatorRepo.get(taskOperator.tasksId)

                                    if (taskDbLocal != null) {
                                        taskDbLocal.typesId = taskOperator.typesId
                                        taskDbLocal.name = taskOperator.name
                                        taskDbLocal.address = taskOperator.address
                                        taskDbLocal.startDate = taskOperator.startDate
                                        taskDbLocal.endDate = taskOperator.endDate
                                        taskDbLocal.scheduleDate = taskOperator.scheduleDate
                                        taskDbLocal.latParkingSpace = taskOperator.latParkingSpace
                                        taskDbLocal.lonParkingSpace = taskOperator.lonParkingSpace
                                        taskDbLocal.status = taskOperator.status
                                        taskDbLocal.types = taskOperator.types
                                        taskUpdateList.add(taskDbLocal)
                                    } else {
                                        taskUpdateList.add(taskOperator)
                                    }
                                }
                                taskOperatorRepo.insertUpdate(taskUpdateList)
                            }
                        }
                        it
                    }
            }
            .map {
                if (it.isSuccess()) {
                    it.data?.let { data ->
                        return@map handleOperatorResponse(data)
                    }
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getTaskOperator(): List<User> {
        TODO("Not yet implemented")
    }

    override fun getFirst(): User? {
        return box.query().build().findFirst()
    }

    override fun getProfile(): User? {
        return box.query().build().findFirst()
    }


    override fun deleteAllObs(): Completable {
        return Completable.fromAction { box.removeAll() }
    }

    override fun deleteAll() {
        box.removeAll()
    }

    override fun getCurrentUser(): User? {
        val userAuthData = storage[ConstVar.USER_AUTH, UserAuthData::class.java]
        return getUser(userAuthData?.accountId ?: 0)
    }

    override fun getUser(id: Long): User? {
        return box.get(id)
    }

    override fun getUserCloud(userId: Long): Single<User> {
        return service.getUser()
            .map {
                if (it.isSuccess()) {
                    it.data?.let { user ->
                        val userAuthData = storage[ConstVar.USER_AUTH, UserAuthData::class.java]
                        user.roleName = userAuthData?.role_name
                        val id = box.put(user)
                        val userDb = getUser(id)

                        if (userDb != null)
                            return@map userDb
                    }
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getCurrentUserRecruiter(): User? {
        return box.query().equal(User_.roleName, ConstVar.USER_ROLE_RECRUITER).build().findFirst()
    }

    override fun getAllAvailableOperator(): Maybe<List<User>> {

        return service.getMerchantOperators()
            .map {
                if (it.isSuccess()) {
                    it.data?.let { data ->
                        return@map handleOperatorResponse(data)
                    }
                }

                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun searchOperator(keyword: String): Single<List<User>> {
        val results = box.query()
            .contains(User_.name, keyword)
            .or()
            .contains(User_.platNo, keyword)
            .or()
            .contains(User_.userName, keyword)
            .build().find()

        return Single.fromCallable { results }
    }

    override fun getAvailableDate(parkingSlotSalesId: Long, foodTruckId: Long): Single<List<Long>> {
        val activeDate: MutableList<Long> = mutableListOf()
        val parkingSlotSales = parkingSlotSalesRepo.get(parkingSlotSalesId)

        if (parkingSlotSales != null) {
            val startDateMillis =
                DateTimeUtil.getDateFromString(parkingSlotSales.startDate)?.time ?: 0L
            val endDateMillis = DateTimeUtil.getDateFromString(parkingSlotSales.endDate)?.time ?: 0L
            val endDateWithoutTime = DateTimeUtil.getCurrentDateWithoutTime(endDateMillis)
            var nextDate = DateTimeUtil.getCurrentDateWithoutTime(startDateMillis)

            do {
                val currentDate = nextDate

                activeDate.add(currentDate)
                nextDate = DateTimeUtil.getNextDate(currentDate)
            } while (currentDate != endDateWithoutTime)
        }

        return Single.fromCallable { activeDate }
    }

    override fun loginPOS(params: LoginPosUseCase.Params): Single<UserAuthData> {
        return service.loginPOS(params)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(it, retrofit)
                }
            }
            .flatMap {
                if (it.isSuccess()) {
                    val user = it.data!!
                    val jwt = JWT(user.token!!)
                    val roleName = jwt.getClaim("role_name").asString()
                    val userId = jwt.getClaim("user_id").asString()
                    val exp = jwt.getClaim("exp").asLong()

                    val userAuthData = UserAuthData(
                        accessToken = user.token,
                        accountId = userId?.toLong() ?: 0,
                        user_id = userId,
                        exp = exp ?: 0,
                        accountUsername = roleName,
                        role_name = roleName
                    )

                    storage[ConstVar.USER_AUTH] = userAuthData

                    return@flatMap getUserCloud(userId?.toLong() ?: 0)
                        .map {
                            userAuthData
                        }
                        .flatMap {
                            syncDataRepo.syncDataMaster()
                                .toSingleDefault(userAuthData)
                        }
                }
                throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
            }
    }

    override fun getProfileMerchant(): User? {
        return box.query().equal(User_.roleName, ConstVar.USER_ROLE_MERCHANT).build().findFirst()
    }

    override fun getOperator(): User? {
        return box.query().equal(User_.roleName, ConstVar.USER_ROLE_OPERATOR).build().findFirst()
    }

    override fun updateUser(user: User, file: File?): Completable {

        var updatePhoto: MultipartBody.Part? = null

        if (file != null) {
            try {
                val bitmapFile = BitmapFactory.decodeFile(file.absolutePath)
                val resized = FileUtil.createScaledBitmap(bitmapFile, 600)
                file.outputStream().use { out ->
                    resized.compress(Bitmap.CompressFormat.JPEG, 75, out)
                    resized.recycle()
                }

                val requestFile = RequestBody.create(
                    MultipartBody.FORM,
                    file
                )

                updatePhoto =
                    MultipartBody.Part.createFormData("image", "jpg", requestFile)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        val name: RequestBody = RequestBody.create(
            MultipartBody.FORM, user.name!!
        )
        val address: RequestBody = RequestBody.create(
            MultipartBody.FORM, user.address!!
        )
        val phone: RequestBody = RequestBody.create(
            MultipartBody.FORM, user.phone!!
        )
        val email: RequestBody = RequestBody.create(
            MultipartBody.FORM, user.userName!!
        )

        return service.updateUser(name, address, phone, email, updatePhoto)
    }

    private fun handleOperatorResponse(data: List<User>): List<User> {
        data.forEach {
            it.roleName = ConstVar.USER_ROLE_OPERATOR
        }
        insertUpdateUsers(data)
        return getAllOperator()
    }
}