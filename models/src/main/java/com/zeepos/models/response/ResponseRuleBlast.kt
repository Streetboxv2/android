package com.zeepos.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseRuleBlast(

    @field:SerializedName("data")
    val data: DataRuleBlast? = null
) : Parcelable

@Parcelize
data class DataRuleBlast(

    @field:SerializedName("is_active")
    val isActive: Boolean? = null,

    @field:SerializedName("is_auto_blast")
    val isAutoBlast: Boolean? = null,

    @field:SerializedName("expire")
    val expire: Int? = null,

    @field:SerializedName("cooldown")
    val cooldown: Int? = null,

    @field:SerializedName("interval")
    val interval: Int? = null,

    @field:SerializedName("merchant_id")
    val merchantId: Int? = null,

    @field:SerializedName("radius")
    val radius: Double? = null,

    @field:SerializedName("last_auto_blast")
    val lastAutoBlast: String? = null,

    @field:SerializedName("last_blast")
    val lastManualBlast: String? = null
) : Parcelable
