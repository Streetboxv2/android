package id.streetbox.live.uiAPIs

import android.app.Activity
import android.os.Bundle
import com.zeepos.map.ui.MapUiEvent
import id.streetbox.live.ui.menu.MenuActivity
import javax.inject.Inject

/**
 * Created by Arif S. on 6/14/20
 */
class MapUiEventImpl @Inject internal constructor() :
    MapUiEvent {
    override fun goToOperatorFTScreen(activity: Activity) {
    }

    override fun goToMerchantMenuScreen(activity: Activity, merchantId: Long, bundle: Bundle) {
        activity.startActivity(MenuActivity.getIntent(activity, merchantId, bundle))
    }

    override fun goToOperatorMainActivity(
        activity: Activity,
        latitude: Double,
        longitude: Double,
        type: Int
    ) {
        TODO("Not yet implemented")
    }


}