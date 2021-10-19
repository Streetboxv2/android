package com.zeepos.domain.repository

import com.google.gson.JsonObject
import com.zeepos.domain.interactor.*
import com.zeepos.models.entities.None
import com.zeepos.models.entities.RegisterParams
import com.zeepos.models.entities.ResponseApi
import com.zeepos.models.master.*
import com.zeepos.models.response.*
import com.zeepos.models.transaction.PaymentSales
import com.zeepos.models.transaction.QRCodeResponse
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Arif S. on 5/7/20
 */
interface RemoteRepository {
    fun login(param: LoginUseCase.Params): Single<UserAuthData>
    fun registration(registerParams: RegisterParams): Single<UserAuthData>
    fun changePassword(param: ChangePasswordUseCase.Params): Single<ResponseApi<User>>
    fun resetPassword(param: ForgotPasswordUseCase.Params): Single<ResponseApi<User>>
    fun shiftIn(param: None): Single<ResponseApi<Shift>>
    fun checkshiftIn(): Single<ResponseApi<Shift>>
    fun shiftOut(param: None): Single<ResponseApi<ShiftOut>>

    //    fun getParkingSpace(): Single<ResponseApi<List<ParkingSpace>>>
    fun getOperatorTask(): Single<ResponseApi<List<OperatorTask>>>
    fun getUser(userId: Long): Single<ResponseApi<User>>
    fun getProfile(): Single<ResponseApi<User>>
    fun getPaymentMethod(): Single<ResponseApi<List<PaymentMethod>>>
    fun getSatusNonReguler(): Single<ResponseApi<StatusNonRegular>>
    fun getProfileMerchant(): Single<ResponseApi<User>>
    fun getPaymentSales(): Single<ResponseApi<List<PaymentSales>>>
    fun sendDataToken(params: SendTokenUseCase.Params): Completable
    fun sendDataTokenUser(params: SendTokenUser.Params): Completable
    fun getReceiveMessage(): Completable
    fun getDataTermCondition(): Single<ResponseTermCondition>
    fun callReqAddress(map: Map<String?, Any?>): Single<ResponseApi<JsonObject>>
    fun callUpdateAddress(map: Map<String?, Any?>): Single<ResponseApi<JsonObject>>
    fun callDeleteAddress(id: String): Single<ResponseApi<JsonObject>>
    fun callPrimaryAddress(id: String): Single<ResponseApi<JsonObject>>
    fun callGetAddress(): Single<ResponseListAddress>
    fun callGetDistance(): Single<ResponseDistanceKm>
    fun callGetAddressPrimary(): Single<ResponseAddressPrimary>

    //blast
    fun callApiStatusCallEndUser(status: String): Single<ResponseGetStatusCall>
    fun callListNotifBlast(): Single<ResponseListNotificationBlast>

    //blastFoodtruck
    fun callApiStatusAccepted(status: String): Single<ResponseGetStatusCallFoodTruck>
    fun callApiFinishOrder(callId: String): Single<JsonObject>
    fun callApiAcceptedOrder(callId: String, status: String): Single<JsonObject>
    fun callApiRejectOrder(callId: String, status: String): Single<JsonObject>
    fun callApiOnprocessOrder(callId: String, status: String): Single<JsonObject>
    fun callApiTimerFoodTruck(): Single<ResponseRuleBlast>
    fun callApiNotifBlatManual(): Single<JsonObject>
    fun callApiFoodTruck(idCall: String): Single<JsonObject>
    fun callApiUpdateLoc(map: MutableMap<String, Double>): Single<JsonObject>
    fun callApiUpdateLocFoodTruck(map: MutableMap<String, Double>): Single<JsonObject>
    fun callApiGetLocFoodtruck(id: String): Single<ResponseGetLocFoodTruck>
    fun callApiAutoBlastToggle(): Single<JsonObject>

  /*  fun callApiPaymentNearby(
        merchantId: Long, amount: Int,
        address: String, types: String
    ): Single<ResponseApi<QRCodeResponse>>*/

}