package com.zeepos.remotestorage
//
//import com.google.gson.JsonObject
//import com.zeepos.localstorage.Storage
//import com.zeepos.models.ConstVar
//import com.zeepos.models.master.UserAuthData
//import okhttp3.Authenticator
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.Route
//import retrofit2.Retrofit
//import javax.inject.Inject
//import javax.inject.Singleton
//
///**
// * Created by Arif S. on 5/18/20
// */
//@Singleton
//class TokenAuthenticator @Inject constructor(
//    private val storage: Storage,
//    private val tokenHolder: Lazy<TokenHolder>
//) : Authenticator {
//
//    override fun authenticate(route: Route?, response: Response): Request? {
//
//        val userAuthData = storage[ConstVar.USER_AUTH, UserAuthData::class.java]
//        var token = userAuthData?.accessToken ?: ConstVar.EMPTY_STRING
//
//        if (response.request().header("Authorization") != null || !response.request()
//                .header("Authorization").equals("Bearer $token")
//        ) {
//            //returning null means we have tried and the refresh failed so exit; this will also get you out of infinite loop resulting from retrying
//            return null
//        }
//
//        try {
//            val json = JsonObject()
//            json.addProperty("oldToken", token)
//            json.addProperty("username", userAuthData?.userName)
//            json.addProperty("password", userAuthData?.password)
//
//            val service = tokenHolder.value.remoteService!!
//
//            val disposable = service.refreshToken(json)
//                .map {
//                    if (it.isSuccess()) {
//                        val user = it.data!!
//
//                        if (userAuthData != null) {
//                            userAuthData.accessToken = user.token
//                            storage[ConstVar.USER_AUTH] = userAuthData
//
//                        }
//                    }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        //this is your new token
//        token = storage[ConstVar.USER_AUTH, UserAuthData::class.java]?.accessToken
//            ?: ConstVar.EMPTY_STRING
//
//        return response.request().newBuilder()
//            .header("Authorization", "Bearer $token").build()
//    }
//
//
//}