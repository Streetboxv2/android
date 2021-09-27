package com.zeepos.models.response


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseAddressPrimary(
    @SerializedName("data")
    val data: DataAddress
) : Parcelable