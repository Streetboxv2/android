package com.zeepos.models.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseTermCondition(
    @SerializedName("data")
    val data: DataCondition?
) : Parcelable {
    @Parcelize
    data class DataCondition(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("key")
        val key: String?,
        @SerializedName("value")
        val value: String?
    ) : Parcelable
}