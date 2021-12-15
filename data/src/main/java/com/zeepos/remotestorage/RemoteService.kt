package com.zeepos.remotestorage

import com.google.gson.JsonObject
import com.zeepos.domain.interactor.*
import com.zeepos.domain.interactor.user.LoginPosUseCase
import com.zeepos.models.entities.*
import com.zeepos.models.master.*
import com.zeepos.models.response.*
import com.zeepos.models.transaction.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*


/**
 * Created by Arif S. on 5/2/20
 */
@JvmSuppressWildcards
interface RemoteService {
    @Headers("Content-Type: application/json")
    @POST("login")
    fun login(@Body body: LoginUseCase.Params): Single<ResponseApi<User>>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginPOS(@Body body: LoginPosUseCase.Params): Single<ResponseApi<User>>

    @POST("login")
    fun refreshToken(@Body body: JsonObject): Single<ResponseApi<User>>

    @FormUrlEncoded
    @POST("payment/create-qrcode")
    fun getQRCodePayment(
        @Field("merchant_id") merchantId: Long?,
        @Field("amount") amount: Int,
        @Field("types") types: String,
        @Field("address") address: String,
        @Field("order") order: Order,
        @Field("orderJson") orderJson: String
    ): Single<ResponseApi<QRCodeResponse>>

   /* @FormUrlEncoded
    @POST("payment/create-qrcode")
    fun getQRCodePaymentNearby(
        @Field("merchant_id") merchantId: Long?,
        @Field("amount") amount: Int,
        @Field("types") types: String,
        @Field("address") address: String
    ): Single<QRCodeResponse>*/


    @POST("trx/order")
    fun syncTransactionDataEndUser(
        @Body body: HashMap<String, Any>
    ): Completable

    @GET("parkingspace")
    fun getParkingSpace(
        @QueryMap query: Map<String, String> = mapOf()
    ): Single<ResponseApi<List<ParkingSpace>>>

    @GET("consumer/home/map/schedules/{id}/parking-space")
    fun getParkingSpaceSchedule(
        @Path("id") parkingSpaceId: Long
    ): Single<ResponseApi<List<ParkingSchedule>>>

    @GET("consumer/home/schedules-regular/{typesId}")
    fun getMerchantParkingSpaceSchedule(
        @Path("typesId") typesId: Long
//        @Path("merchantId") merchantId: Long
    ): Single<ResponseApi<List<Schedule>>>

    @GET("parkingspace/sales/{id}")
    fun getParkingSlot(@Path("id") id: Long): Single<ResponseApi<List<ParkingSlot>>>

    @GET("trxsales/myparking")
    fun getParkingSales(@QueryMap query: Map<String, String> = mapOf()): Single<ResponseApi<List<ParkingSales>>>

    @GET("trxsales/myparking/slot/{id}")
    fun getParkingSalesSlot(
        @Path("id") parkingSalesId: Long
    ): Single<ResponseApi<List<ParkingSlotSales>>>

    @GET("operator")
    fun getOperator(): Maybe<ResponseApi<List<User>>>

    @GET("/tasks/regular/list")
    fun getOperatorTask(): Single<ResponseApi<List<TaskOperator>>>

    @GET("/tasks/nonregular/list")
    fun getOperatorFreeTask(): Single<ResponseApi<TaskOperator>>

    @POST("/tasks/shift-in")
    fun shiftin(@Body body: None): Single<ResponseApi<Shift>>

    @POST("/tasks/shift-out")
    fun shiftout(@Body body: None): Single<ResponseApi<ShiftOut>>

    @POST("/tasks/nonregular")
    fun createFreeTask(@Body body: None): Single<ResponseApi<Check>>

    @POST("/tasks/regular/check-in")
    fun checkin(@Body body: CheckUseCase.Params): Single<ResponseApi<Check>>

    @POST("/tasks/regular/check-out")
    fun checkout(@Body body: CheckOutUseCase.Params): Single<ResponseApi<Check>>

    @POST("/tasks/nonregular/check-in")
    fun checkinFreeTask(@Body body: CheckFreeTaskUseCase.Params): Single<ResponseApi<Check>>

    @POST("tasks/homevisit/check-in")
    fun checkinHomeVisit(@Body body: CheckInHomeVisitUseCase.Params): Single<ResponseApi<Check>>

    @POST("tasks/homevisit/check-out")
    fun checkoutHomeVisit(@Body body: CheckOutHomeVisitUseCase.Params): Single<ResponseApi<Check>>

    @POST("/tasks/nonregular/check-out")
    fun checkoutFreeTask(@Body body: CheckOutFreeTaskUseCase.Params): Single<ResponseApi<Check>>

    @GET("/tasks/shift-in/status")
    fun checkStatus(): Single<ResponseApi<Shift>>

    @GET("/tasks/nonregular/status")
    fun checkStatusNonRegular(): Single<ResponseApi<StatusNonRegular>>

    @GET("user")
    fun getAllUser(): Maybe<ResponseApi<List<User>>>

    @PUT("user/changepassword")
    fun changePassword(@Body body: ChangePasswordUseCase.Params): Single<ResponseApi<User>>

    @PUT("/tasks/undo/{tasksId}")
    fun undo(@Path("tasksId") tasksId: Long): Single<ResponseApi<String>>

    @PUT("trx/online-order/closed/{trxId}")
    fun closeOnlineOrder(@Path("trxId") trxId: String): Completable

    @PUT("trx/order/void/{trxId}")
    fun voidOrder(@Path("trxId") trxId: String): Completable

    @PUT("/tasks/ongoing/{tasksId}")
    fun taksOnGoing(@Path("tasksId") tasksId: Long): Single<ResponseApi<String>>

    @POST("forgotpassword")
    fun forgotpassword(@Body body: ForgotPasswordUseCase.Params): Single<ResponseApi<User>>

    @POST("/merchant/registration-token/{token}")
    fun sendToken(@Path("token") token: String): Completable

    @POST("/consumer/registration-token/{token}")
    fun sendTokenUser(@Path("token") token: String): Completable

    @POST("login/google")
    fun loginRegisterGoogle(@Body body: RegisterParams): Single<ResponseApi<User>>

    @POST("/trx/createsync")
    fun createSync(@Body body: SyncData): Completable

    @POST("/trx/createsync")
    fun syncTransactionDataPOS(@Body body: SyncData): Single<ResponseApi<SyncResponse>>

    @POST("trx/homevisit")
    fun syncHomeVisitTransactionData(@Body body: BookHomeVisit): Single<ResponseApi<None>>

    @GET("/user/info")
    fun getProfile(): Single<ResponseApi<User>>

    @GET("/merchant/info")
    fun getProfileMerchant(): Single<ResponseApi<User>>

    @Multipart
    @PUT("consumer/update/userprofile")
    fun updateUser(
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part image: MultipartBody.Part?
    ): Completable

    @Multipart
    @POST("parkingspace")
    fun saveParkingspace(
        @Part("image1") image1: MultipartBody.Part,
        @Part("image2") image2: MultipartBody.Part,
        @Part("image3") image3: MultipartBody.Part,
        @Part("image4") image4: MultipartBody.Part,
        @Part("name") name: String,
        callback: Callback<Response<ParkingSpace>>
    )

    @GET("searchparkingspace")
    fun searchParkingSpace(@Query("keyword") keyword: String): Maybe<ResponseApi<List<ParkingSpace>>>

    @GET("searchparkingspacesales")
    fun searchParkingSpaceSales(@Query("keyword") keyword: String): Maybe<ResponseApi<List<ParkingSales>>>

    @GET("user/info")
    fun getUser(): Single<ResponseApi<User>>

    @GET("user/info/{id}")
    fun getUserInfoCloud(): Single<ResponseApi<User>>

    @GET("merchant/foodtruck/all")
    fun getMerchantOperators(): Maybe<ResponseApi<List<User>>>

    @FormUrlEncoded
    @POST("tasks/regular")
    fun createTaskOperator(
        @Field("trxSalesId") salesId: Long,
        @Field("usersId") usersId: Long,
        @Field("scheduleDate") scheduleDate: String
    ): Completable

    @POST("tasks/homevisit")
    fun createTaskOperatorHomeVisit(
        @Body body: CreateOperatorHomeVisitUseCase.Params
    ): Completable


    @POST("tasks/tracking")
    fun updateLocation(@Body body: OperatorLocation): Completable

    @GET("tasks/tracking/{id}/foodtruck")
    fun getLatestOperatorLocation(@Path("id") taskId: Long): Observable<ResponseApi<OperatorLocation>>

    @GET("tasks/all")
    fun getAllFoodTruckTask(): Maybe<ResponseApi<List<TaskOperator>>>

    @GET("log-merchant")
    fun getLogs(@QueryMap query: Map<String, String> = mapOf()): Single<ResponseApi<List<Logs>>>

    @GET("task/myparking/{id}")
    fun getTaskOperatorByParkingSales(@Path("id") parkingSalesId: Long): Maybe<ResponseApi<List<TaskOperator>>>

    /**
     * QueryMap keys (optional):
     * - activity
     * - limit
     * - page
     * - distance
     */
    @GET("consumer/home/nearby/{lat}/{lon}")
    fun searchNearByFoodTrucks(
//        @Path("activity") activity: String = ConstVar.MAP_NEARBY_CHECK_IN,
        @Path("lat") latitude: Double,
        @Path("lon") longitude: Double,
        @QueryMap query: Map<String, String> = mapOf()
    ): Maybe<ResponseApi<List<FoodTruck>>>

    @GET("consumer/home/map/parking-space/{lat}/{lon}")
    fun searchNearByParkingSpaceMaps(
        @Path("lat") latitude: Double,
        @Path("lon") longitude: Double,
        @Query("distance") distance: String? = null
    ): Maybe<ResponseApi<List<ParkingSpace>>>

    @GET("merchant/list/menu")
    fun getAllProducts(): Single<ResponseApi<List<Product>>>

    @GET("merchant/pos/gettransaction")
    fun getTransaction(
        @QueryMap query: Map<String, String>
    ): Single<ResponseApi<AllTransaction>>

    @GET("/merchant/taxsetting/menu")
    fun getTaxSetting(): Single<ResponseApi<Tax>>

    @GET("/trx/online-order")
    fun getTransactionOnlineOrder(): Single<ResponseApi<OnlineOrder>>

    @GET("consumer/home/visit-sales")
    fun getFoodTruckHomeVisit(
        @QueryMap query: Map<String, String> = mapOf()
    ): Single<ResponseApi<List<FoodTruck>>>


    @GET("/merchant/receive-message/online-order")
    fun getReceiveMessageOnlineOrder(): Completable

    @GET("consumer/home/map/livetracking/{lat}/{lon}")
    fun getNearByFoodTruckLive(
        @Path("lat") latitude: Double,
        @Path("lon") longitude: Double,
        @Query("distance") distance: String? = null
    ): Single<ResponseApi<List<LiveFoodTruckTrack>>>

    @GET("consumer/merchant/menu/{merchantId}")
    fun getAllProducts(
        @Path("merchantId") merchantId: Long
    ): Single<ResponseApi<List<Product>>>

    @GET("consumer/payment-method")
    fun getPaymentMethod(): Single<ResponseApi<List<PaymentMethod>>>

    @GET("consumer/order/history")
    fun getOrderHistoryEndUser(
        @QueryMap query: Map<String, String> = mapOf()
    ): Single<ResponseApi<List<OrderHistory>>>

    @GET("consumer/order/history")
    fun getOrderHistoryEndUserOnGoing(
        @QueryMap query: Map<String, String> = mapOf()
    ): Single<ResponseApi<List<OrderHistory>>>

    @GET("consumer/order/historydetail/{trxid}")
    fun getOrderHistory(
        @Path("trxid") trxId: String
    ):Single<ResponseApi<OrderHistoryDetail>>

    @GET("/merchant/pos/getdetailtransaction")
    fun getOrderHistoryPos(
        @QueryMap query: Map<String, String>
    ):Single<ResponseApi<AllTransaction>>


    @GET("consumer/home/visit-sales/detail/{merchantId}")
    fun getHomeVisitBookDate(
        @Path("merchantId") merchantId: Long
    ): Single<ResponseApi<List<AvailableHomeVisitBookDate>>>

    @GET("consumer/merchant/tax/{merchantId}")
    fun getMerchantTax(
        @Path("merchantId") merchantId: Long
    ): Single<ResponseApi<Tax>>

    @GET("consumer/merchant/tax/{merchantId}")
    fun getMerchantTaxSales(
        @Path("merchantId") merchantId: Long
    ): Single<ResponseApi<TaxSales>>

    @Streaming
    @GET
    fun downloadReportFile(@Url fileUrl: String): Single<ResponseBody>

    @GET("appsetting/get-by-key/term_condition")
    fun getTermCondition(
    ): Single<ResponseTermCondition>

    @POST("user/address")
    fun callAddAddress(
        @Body map: Map<String?, Any?>
    ): Single<ResponseApi<JsonObject>>

    @PUT("user/address")
    fun callUpdateAddress(
        @Body map: Map<String?, Any?>
    ): Single<ResponseApi<JsonObject>>

    @GET("user/address")
    fun callGetAddress(
    ): Single<ResponseListAddress>

    @DELETE("user/{id}/address")
    fun callDeleteAddress(
        @Path("id") id: String
    ): Single<ResponseApi<JsonObject>>

    @PUT("user/address/{id}/switch")
    fun callMarkPrimaryAddress(
        @Path("id") id: String
    ): Single<ResponseApi<JsonObject>>

    @GET("appsetting/get-by-key/nearby_radius")
    fun callGetDistanace(
    ): Single<ResponseDistanceKm>

    @GET("user/address/primary")
    fun callGetAddressPrimary(
    ): Single<ResponseAddressPrimary>


    @POST("directions/json")
    fun requestDirectionMaps(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("key") apiKey: String?
    ): Call<ResponseDirectionsMaps?>?

    //blast

    @POST("canvassing/blast")
    fun callBlastNotifManual(
    ): Single<JsonObject>


    @POST("canvassing/call/{id_notif}")
    fun callFoodTruckBlast(
        @Path("id_notif") idNotif: String
    ): Single<JsonObject>


    @PUT("canvassing/users/location")
    fun callUpdateLocEndUser(
        @Body map: MutableMap<String, Double>
    ): Single<JsonObject>


    @PUT("canvassing/foodtruck/location")
    fun callUpdateLocFoodTruck(
        @Body map: MutableMap<String, Double>
    ): Single<JsonObject>

    @GET("canvassing/notifications")
    fun callGetlistNotifBlast(
    ): Single<ResponseListNotificationBlast>

    @GET("canvassing/foodtruck/calls")
    fun callGetStatusCallFoodTruck(
        @Query("status") status: String
    ): Single<ResponseGetStatusCallFoodTruck>

    @GET("canvassing/calls")
    fun callGetStatusCallEndUser(
    ): Single<ResponseGetStatusCall>

    @PUT("canvassing/call/{id_call}/{status}")
    fun callAcceptedStatusFoodTruck(
        @Path("id_call") idCall: String,
        @Path("status") status: String
    ): Single<JsonObject>

    @PUT("canvassing/call-status/{id_call}/{status}")
    fun callUpdateStatusFoodTruck(
        @Path("id_call") idCall: String,
        @Path("status") status: String
    ): Single<JsonObject>

    @PUT("canvassing/finish/{id_call}")
    fun callFinishOrder(
        @Path("id_call") idCall: String
    ): Single<JsonObject>

    @GET("canvassing/foodtruck")
    fun callTimerBlast(
    ): Single<ResponseRuleBlast>

    @GET("canvassing/foodtruck/location/{id_foodtruck}")
    fun callGetLocationFoodtruck(
        @Path("id_foodtruck") id: String
    ): Single<ResponseGetLocFoodTruck>

    @PUT("canvassing/toggle/auto")
    fun callBlastAutoToggle(
    ): Single<JsonObject>
}