package com.zeepos.map.ui.dialogs.merchantinfo

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.request.RequestOptions
import com.zeepos.map.R
import com.zeepos.map.ui.MapViewEvent
import com.zeepos.map.ui.MapViewModel
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.ui_base.ui.BaseDialogFragment
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.dialog_merchant_info.*
import kotlinx.android.synthetic.main.fragment_merchant_instagram.*

/**
 * Created by Arif S. on 9/24/20
 */
class MerchantInfoNonRegDialog : BaseDialogFragment() {
    private var foodTruckDataStr: String = ConstVar.EMPTY_STRING
    private var merchantLogo: String? = null
    private var viewModel: MapViewModel? = null
    private var viewPagerAdapter: MerchantInfoPagerAdapter? = null
    private lateinit var parkingSchedule: ParkingSchedule
    private var merchantId: Long = 0
    private var parkingSpaceId: Long = 0
    private var typesId: Long = 0
    private var merchantIg: String = ""

    override fun initResourceLayout(): Int {
        return R.layout.dialog_merchant_non_reg_info
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        activity?.let {
            viewModel = ViewModelProvider(it).get(MapViewModel::class.java)

        }

        val bundle = arguments

        if (bundle != null) {
            merchantId = bundle.getLong("merchantId")
            parkingSpaceId = bundle.getLong("parkingSpaceId")
            merchantLogo = bundle.getString("logo")
            merchantIg = bundle.getString("merchantIg") ?: ""
            typesId = bundle.getLong("typesId", 0)
            tv_name.text = bundle.getString("name")
            foodTruckDataStr = bundle.getString("foodTruckData", ConstVar.EMPTY_STRING)

            val address = bundle.getString("address", ConstVar.EMPTY_STRING)
            tv_address.text = address

            if (address.isEmpty()) {
                tv_address.visibility = View.INVISIBLE
                iv_location.visibility = View.INVISIBLE
            }

            if (merchantId > 0) {
                viewModel?.getMerchantParkingSchedule(merchantId, typesId)

                if (merchantLogo != null) {
                    context?.let {
                        val logUrl = ConstVar.PATH_IMAGE + merchantLogo
                        GlideApp.with(it)
                            .load(logUrl)
                            .apply(RequestOptions().placeholder(R.drawable.logo_end_user))
                            .into(iv_parking)
                    }
                }
            } else {
                viewModel?.getParkingSchedule(parkingSpaceId)
            }
        }

        initWebView()

        btn_menu.setOnClickListener {
            if (merchantId > 0) {
                viewModel?.viewEventObservable?.postValue(
                    MapViewEvent.GoToMerchantMenuScreen(
                        merchantId, bundle!!
                    )
                )
            }
        }
    }

    private fun initWebView() {
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://www.instagram.com/$merchantIg")

    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = (Utils.getScreenWidth(activity!!) / 1.1).toInt()
        params?.height = Utils.getScreenHeight(activity!!) - 200

        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.setCanceledOnTouchOutside(true)
    }
}