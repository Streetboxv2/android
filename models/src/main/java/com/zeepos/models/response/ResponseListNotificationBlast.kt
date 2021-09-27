package com.zeepos.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseListNotificationBlast(

    @field:SerializedName("data")
    val data: List<DataItemNotificationBlast?>? = null
) : Parcelable

@Parcelize
data class DataItemNotificationBlast(

    @field:SerializedName("foodtruck_id")
    val foodtruckId: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("customer_id")
    val customerId: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("longitude_foodtruck")
    val longitudeFoodtruck: Double? = null,

    @field:SerializedName("latitude_foodtruck")
    val latitudeFoodtruck: Double? = null,

    @field:SerializedName("longitude_user")
    val longitudeEndUser: Double? = null,

    @field:SerializedName("latitude_user")
    val latitudeEndUser: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("logo")
    val logo: String? = null,

    @field:SerializedName("ig_account")
    val ig_account: String? = null,

    @field:SerializedName("plat_no")
    val platNomor: String? = null

) : Parcelable
