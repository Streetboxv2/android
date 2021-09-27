package com.zeepos.streetbox.uiAPIs

import android.app.Activity
import android.os.Bundle
import com.zeepos.map.ui.MapUiEvent
import com.zeepos.streetbox.ui.operator.main.OperatorFTActivity
import com.zeepos.streetbox.ui.operator.main.OperatorMainActivity
import javax.inject.Inject

class MapUiEventImpl @Inject internal constructor() :
    MapUiEvent {

    override fun goToOperatorFTScreen(activity: Activity) {
        activity.startActivity(OperatorFTActivity.getIntent(activity))
    }

    override fun goToMerchantMenuScreen(activity: Activity, merchantId: Long, bundle: Bundle) {
        TODO("Not yet implemented")
    }

    override fun goToOperatorMainActivity(
        activity: Activity,
        latitude: Double,
        longitude: Double,
        type: Int
    ) {
        activity.startActivity(OperatorMainActivity.getIntent(activity,latitude,longitude,type))
    }

}