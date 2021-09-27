package com.zeepos.models.response


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseDistanceKm(
    @SerializedName("data")
    val data: DataDistance?
) : Parcelable {
    @Parcelize
    data class DataDistance(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("key")
        val key: String?,
        @SerializedName("value")
        val value: String?
    ) : Parcelable
}