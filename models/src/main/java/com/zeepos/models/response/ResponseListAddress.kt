package com.zeepos.models.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseListAddress(
    @SerializedName("data")
    val data: List<DataAddress?>?
) : Parcelable

@Parcelize
data class DataAddress(
    @SerializedName("address")
    val address: String?,
    @SerializedName("address_name")
    val addressName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("person")
    val person: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("primary")
    var primary: Boolean,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?
) : Parcelable