package com.zeepos.map.ui.merchantinstagram

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.zeepos.map.R
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.ui_base.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_merchant_instagram.*
import javax.inject.Inject

/**
 * Created by Arif S. on 8/12/20
 */
class MerchantInstagramFragment :
    BaseFragment<MerchantInstagramViewEvent, MerchantInstagramViewModel>() {

    private lateinit var merchantIg: String

    @Inject
    lateinit var gson: Gson
    private lateinit var parkingSchedule: ParkingSchedule

    override fun initResourceLayout(): Int {
        return R.layout.fragment_merchant_instagram
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(MerchantInstagramViewModel::class.java)

        val bundle = arguments!!
        merchantIg = bundle.getString("merchantIg")!!
//        parkingSchedule = gson.fromJson(dataStr, ParkingSchedule::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
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

    override fun onEvent(useCase: MerchantInstagramViewEvent) {
    }

    companion object {
        fun newInstance(data: String): MerchantInstagramFragment {
            val bundle = Bundle()
            bundle.putString("merchantIg", data)
            val fragment = MerchantInstagramFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}