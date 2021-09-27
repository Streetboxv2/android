package com.zeepos.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseGetLocFoodTruck(
	@field:SerializedName("data")
	val data: DataFoodTruck? = null
) : Parcelable

@Parcelize
data class DataFoodTruck(

	@field:SerializedName("foodtruck_id")
	val foodtruckId: Int? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
) : Parcelable
