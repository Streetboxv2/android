package com.zeepos.remotestorage

import android.content.Context
import com.auth0.android.jwt.JWT
import com.google.gson.JsonObject
import com.zeepos.domain.interactor.*
import com.zeepos.domain.repository.*
import com.zeepos.localstorage.Storage
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.None
import com.zeepos.models.entities.RegisterParams
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.*
import com.zeepos.models.response.*
import com.zeepos.models.transaction.PaymentSales
import com.zeepos.models.transaction.QRCodeResponse
import com.zeepos.utilities.SharedPreferenceUtil
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.exceptions.Exceptions
import retrofit2.Retrofit
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.set

/**
 * Created by Arif S. on 5/2/20
 */

class RemoteRepositoryImpl @Inject internal constructor(
    private val retrofit: Retrofit,
    private val userRepository: UserRepository,
    private val parkingSpaceRepository: ParkingSpaceRepository,
    private val operatorRepo: TaskOperatorRepo,
    private val parkingSalesRepo: ParkingSalesRepo,
    private val paymentMethodRepo: PaymentMethodRepo,
    private val storage: Storage
) : RemoteRepository {

    private fun initHeaderToken(context: Context): Map<String?, String?> {
        val map: MutableMap<String?, String?> = HashMap()
        val token = SharedPreferenceUtil.getString(context, ConstVar.TOKEN, ConstVar.EMPTY_STRING)
            ?: ConstVar.EMPTY_STRING
        map["Authorization"] = token
        map["Accept"] = "application/json"
        map["Content-Type"] = "application/json"
        return map
    }

    private val service: RemoteService = retrofit.create(
        RemoteService::class.java
    )

    override fun login(param: LoginUseCase.Params): Single<UserAuthData> {
        return service.login(param)
            .onErrorResumeNext {
                Single.error {
                    RetrofitException.handleRetrofitException(it, retrofit)
                }
            }
            .flatMap {

                if (it.isSuccess()) {
                    val user = it.data

                    if (user != null) {
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
                            role_name = roleName,
                            userName = param.username,
                            password = param.password
                        )

                        storage[ConstVar.USER_AUTH] = userAuthData

                        return@flatMap userRepository.getUserCloud(userId?.toLong() ?: 0)
                            .map {
                                userAuthData
                            }

                    } else {
                        throw Exceptions.propagate(Throwable(ConstVar.DATA_NULL))
                    }
                }

                throw Exceptions.propagate(Throwable(it.error?.message))
            }
    }

    override fun registration(registerParams: RegisterParams): Single<UserAuthData> {
        return service.loginRegisterGoogle(registerParams)
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

                    return@flatMap userRepository.getUserCloud(userId?.toLong() ?: 0)
                        .map {
                            userAuthData
                        }
                }

                throw Exceptions.propagate(Throwable(it.error?.message))
            }
    }

    override fun changePassword(param: ChangePasswordUseCase.Params): Single<ResponseApi<User>> {
        return service.changePassword(param)

    }

    override fun resetPassword(param: ForgotPasswordUseCase.Params): Single<ResponseApi<User>> {
        return service.forgotpassword(param)
    }


    override fun shiftIn(param: None): Single<ResponseApi<Shift>> {
        return service.shiftin(param)
    }

    override fun checkshiftIn(): Single<ResponseApi<Shift>> {

        return service.checkStatus()
    }

    override fun shiftOut(param: None): Single<ResponseApi<ShiftOut>> {
        return service.shiftout(param)
    }

    /*override fun checkIn(param: CheckUseCase.Params): Single<ResponseApi<Check>> {
        val
        return service.checkin(param)
    }

    override fun checkOut(param: CheckUseCase.Params): Single<ResponseApi<Check>> {
        return service.checkout(param)
    }*/

//    override fun getParkingSpace(): Single<ResponseApi<List<ParkingSpace>>> {
//        val dataLocal = parkingSpaceRepository.getParkingSpace()
//
//        if (dataLocal.isNotEmpty()) {
//            val response = ResponseApi(
//                error = null,
//                data = dataLocal
//            )
//
//            return Single.fromCallable { response }
//        }

    //region dummy data
//        val parkingSpaceList: MutableList<ParkingSpace> = ArrayList()
//
//        for (i in 1..5) {
//            val parkingSpace = ParkingSpace(i.toLong())
//            parkingSpace.name = "Parking Space $i"
//            parkingSpace.address =
//                "Jl. Sarinah utama no. 38 kav. III Jendral Sudirman DKI Jakarta 21170"
//            parkingSpace.description =
//                "Pinggir jalan raya sudirman persis bundaran, depan hotel harris tugu pancoran sangat ramai pengunjung dimalam hari"
//            parkingSpaceList.add(parkingSpace)
//
//            for (j in 1..3) {
//                val parkingSlot = ParkingSlot(parkingSpaceId = parkingSpace.id)
//                parkingSlot.name = parkingSpace.name + " " + j
//                parkingSlot.point = 15000
//                parkingSlot.qty = 8
//                parkingSlot.schedule = "18-24 May"
//                parkingSlot.description = "Disini ada deskripsi untuk parking space"
//
//                parkingSlotRepo.insertUpdate(parkingSlot)
//
//                parkingSpaceRepository.attach(parkingSpace)//attached first before add relation
//                parkingSpace.parkingSlots.add(parkingSlot)
//            }
//        }
//
//        parkingSpaceRepository.insertUpdateParkingSpace(parkingSpaceList)
//
//        val response = ResponseApi(
//            error = null,
//            data = parkingSpaceRepository.getParkingSpace()
//        )
//
//        return Single.fromCallable { response }
    //endregion

//        return service.getParkingSpace()
//            .map {
//                if (it.isSuccess()) {
//                    val parkingSpaceList = it.data
//                    if (parkingSpaceList != null)
//                        parkingSpaceRepository.insertUpdateParkingSpace(parkingSpaceList)
//                }
//                it
//            }
//    }

    override fun getOperatorTask(): Single<ResponseApi<List<OperatorTask>>> {
        TODO("Not yet implemented")
    }

    override fun getProfile(): Single<ResponseApi<User>> {
        val dataLocal = userRepository.getProfile()
        if (dataLocal != null) {
            val response = ResponseApi(
                error = null,
                data = dataLocal
            )

            return Single.fromCallable { response }
        }


        return service.getProfile()
            .map {
                if (it.isSuccess()) {
                    it.data?.let { user ->
                        userRepository.insertUpdateUser(user)
                    }
                }
                it
            }
    }


    override fun getUser(userId: Long): Single<ResponseApi<User>> {
        val dataLocal = userRepository.getUser(userId)

        if (dataLocal != null) {
            val response = ResponseApi(
                error = null,
                data = dataLocal
            )

            return Single.fromCallable { response }
        }

        return service.getUser()
            .map {
                if (it.isSuccess()) {
                    it.data?.let { user ->
                        val userAuthData = storage[ConstVar.USER_AUTH, UserAuthData::class.java]
                        user.roleName = userAuthData?.role_name
                        userRepository.insertUpdateUser(user)
                    }
                }
                it
            }
    }

    override fun getPaymentMethod(): Single<ResponseApi<List<PaymentMethod>>> {
        val dataLocal = paymentMethodRepo.getPaymentMethod()

        if (dataLocal.isNotEmpty()) {
            val response = ResponseApi(
                error = null,
                data = dataLocal
            )

            return Single.fromCallable { response }
        }

        //dummy
        val paymentMethodList = ArrayList<String>()
        paymentMethodList.add("Point")
        paymentMethodList.add("Cash")
        paymentMethodList.add("Credit Card")
        paymentMethodList.add("Ovo")
        paymentMethodList.add("GoPay")

        val data: MutableList<PaymentMethod> = ArrayList()
        var i = 0
        for (item in paymentMethodList) {
            val paymentMethod = PaymentMethod()
            paymentMethod.name = item
            paymentMethod.type = i
            paymentMethod.isActive = true
            paymentMethod.createdAt = System.currentTimeMillis()
            paymentMethod.updatedAt = System.currentTimeMillis()
            i.inc()
            data.add(paymentMethod)
        }

        val response = ResponseApi(
            error = null,
            data = data.toList()
        )

        return Single.fromCallable { response }

    }

    override fun getSatusNonReguler(): Single<ResponseApi<StatusNonRegular>> {
        return service.checkStatusNonRegular()
    }

    override fun getProfileMerchant(): Single<ResponseApi<User>> {
        val dataLocal = userRepository.getProfileMerchant()
        if (dataLocal != null) {
            val response = ResponseApi(
                error = null,
                data = dataLocal
            )

            return Single.fromCallable { response }
        }


        return service.getProfileMerchant()
            .map {
                if (it.isSuccess()) {
                    it.data?.let { user ->
                        userRepository.insertUpdateUser(user)
                    }
                }
                it
            }
    }

    override fun getPaymentSales(): Single<ResponseApi<List<PaymentSales>>> {
        TODO("Not yet implemented")
    }

    override fun sendDataToken(params: SendTokenUseCase.Params): Completable {
        return service.sendToken(params.token)
    }

    override fun sendDataTokenUser(params: SendTokenUser.Params): Completable {
        return service.sendTokenUser(params.token)
    }

    override fun getReceiveMessage(): Completable {
        return service.getReceiveMessageOnlineOrder()
    }

    override fun getDataTermCondition(): Single<ResponseTermCondition> {
        return service.getTermCondition()
    }

    override fun callReqAddress(map: Map<String?, Any?>): Single<ResponseApi<JsonObject>> {
        return service.callAddAddress(map)
    }

    override fun callUpdateAddress(map: Map<String?, Any?>): Single<ResponseApi<JsonObject>> {
        return service.callUpdateAddress(map)
    }

    override fun callDeleteAddress(id: String): Single<ResponseApi<JsonObject>> {
        return service.callDeleteAddress(id)
    }

    override fun callPrimaryAddress(id: String): Single<ResponseApi<JsonObject>> {
        return service.callMarkPrimaryAddress(id)
    }

    override fun callGetAddress(): Single<ResponseListAddress> {
        return service.callGetAddress()
    }

    override fun callGetDistance(): Single<ResponseDistanceKm> {
        return service.callGetDistanace()
    }

    override fun callGetAddressPrimary(): Single<ResponseAddressPrimary> {
        return service.callGetAddressPrimary()
    }

    override fun callApiStatusCallEndUser(status: String): Single<ResponseGetStatusCall> {
        return service.callGetStatusCallEndUser()
    }

    override fun callListNotifBlast(): Single<ResponseListNotificationBlast> {
        return service.callGetlistNotifBlast()
    }

    override fun callApiStatusAccepted(status: String): Single<ResponseGetStatusCallFoodTruck> {
        return service.callGetStatusCallFoodTruck(status)
    }

    override fun callApiFinishOrder(callId: String): Single<JsonObject> {
        return service.callFinishOrder(callId)
    }

    override fun callApiAcceptedOrder(callId: String, status: String): Single<JsonObject> {
        return service.callAcceptedStatusFoodTruck(callId, status)
    }

    override fun callApiRejectOrder(callId: String, status: String): Single<JsonObject> {
        return service.callAcceptedStatusFoodTruck(callId, status)
    }

    override fun callApiOnprocessOrder(callId: String, status: String): Single<JsonObject> {
        return service.callUpdateStatusFoodTruck(callId, status)
    }

    override fun callApiTimerFoodTruck(): Single<ResponseRuleBlast> {
        return service.callTimerBlast()
    }

    override fun callApiNotifBlatManual(): Single<JsonObject> {
        return service.callBlastNotifManual()
    }

    override fun callApiFoodTruck(idCall: String): Single<JsonObject> {
        return service.callFoodTruckBlast(idCall)
    }

    override fun callApiUpdateLoc(map: MutableMap<String, Double>): Single<JsonObject> {
        return service.callUpdateLocEndUser(map)
    }

    override fun callApiUpdateLocFoodTruck(map: MutableMap<String, Double>): Single<JsonObject> {
        return service.callUpdateLocFoodTruck(map)
    }

    override fun callApiGetLocFoodtruck(id: String): Single<ResponseGetLocFoodTruck> {
        return service.callGetLocationFoodtruck(id)
    }

    override fun callApiAutoBlastToggle(): Single<JsonObject> {
        return service.callBlastAutoToggle()
    }

    override fun callApiPaymentNearby(
        merchantId: Long,
        amount: Int,
        address: String,
        types: String
    ): Single<ResponseApi<QRCodeResponse>> {
        return service.getQRCodePayment(merchantId, amount, types, address)
    }


    /*override fun getOperatorTask(): Single<ResponseApi<List<OperatorTask>>> {
        val dataLocal = parkingOperatorTaskRepository.getParkingOperatorTask()

        if (dataLocal.isNotEmpty()) {
            val response = ResponseApi(
                error = null,
                data = dataLocal
            )

            return Single.fromCallable { response }
        }

        return service.getOperatorTask()
            .map {
                if (it.isSuccess()) {
                    val parkingOperatorTaskList = it.data
                    if (parkingOperatorTaskList != null)
                        parkingOperatorTaskRepository.insertUpdateParkingOperator(parkingOperatorTaskList)

                }
                it
            }

    }*/

}