package com.zeepos.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseGetStatusCallFoodTruck(

    @field:SerializedName("data")
    val data: List<DataItemGetStatusCallFoodTruck?>? = null
) : Parcelable

@Parcelize
data class DataItemGetStatusCallFoodTruck(

    @field:SerializedName("notif_id")
    val notifId: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("foodtruck_id")
    val foodtruckId: Int? = null,

    @field:SerializedName("queue_no")
    val queueNo: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("customer_id")
    val customerId: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("longitude_foodtruck")
    val longitudeFoodtruck: Double? = null,

    @field:SerializedName("latitude_foodtruck")
    val latitudeFoodtruck: Double? = null,

    @field:SerializedName("longitude_user")
    val longitudeEndUser: Double? = null,

    @field:SerializedName("latitude_user")
    val latitudeEndUser: Double? = null,

    @field:SerializedName("profile_picture")
    val profile_picture: String? = null
) : Parcelable
