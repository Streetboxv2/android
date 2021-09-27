package com.zeepos.map.ui

import android.app.Activity
import android.os.Bundle

interface MapUiEvent {
    fun goToOperatorFTScreen(activity: Activity)
    fun goToMerchantMenuScreen(activity: Activity, merchantId: Long, bundle: Bundle)
    fun goToOperatorMainActivity(activity: Activity, latitude: Double, longitude: Double, type: Int)
}