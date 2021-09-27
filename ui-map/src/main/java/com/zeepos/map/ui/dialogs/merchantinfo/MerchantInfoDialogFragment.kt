package com.zeepos.map.ui.dialogs.merchantinfo

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.zeepos.map.R
import com.zeepos.map.ui.MapViewEvent
import com.zeepos.map.ui.MapViewModel
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.ui_base.ui.BaseDialogFragment
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.dialog_merchant_info.*

/**
 * Created by Arif S. on 6/20/20
 */
class MerchantInfoDialogFragment : BaseDialogFragment() {

    private var foodTruckDataStr: String = ConstVar.EMPTY_STRING
    private var merchantLogo: String? = null
    private var viewModel: MapViewModel? = null
    private var viewPagerAdapter: MerchantInfoPagerAdapter? = null
    private lateinit var parkingSchedule: ParkingSchedule
    private var merchantId: Long = 0
    private var parkingSpaceId: Long = 0
    private var typesId: Long = 0
    private var merchantIg: String? = ""

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = (Utils.getScreenWidth(activity!!) / 1.1).toInt()
        params?.height = Utils.getScreenHeight(activity!!) - 200

        dialog?.window?.attributes = params as WindowManager.LayoutParams

        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun initResourceLayout(): Int {
        return R.layout.dialog_merchant_info
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        activity?.let {
            viewModel = ViewModelProvider(it).get(MapViewModel::class.java)

            viewModel?.merchantScheduleObs?.observe(it, Observer { data ->
                Log.d(ConstVar.TAG, "data size -> $data")
//                data.forEach { parkingSchedule ->
//                    if (parkingSchedule.merchantId == merchantId) {
//                        this.parkingSchedule = parkingSchedule
//                        initViewPager(gson.toJson(parkingSchedule))
//                        return@forEach
//                    }
//                }

                initViewPager(Gson().toJson(data))
            })
        }

        val bundle = arguments

        if (bundle != null) {
            merchantId = bundle.getLong("merchantId")
            parkingSpaceId = bundle.getLong("parkingSpaceId")
            merchantLogo = bundle.getString("logo")
            merchantIg = bundle.getString("merchantIg") ?: ""
            typesId = bundle.getLong("typesId", 0)
            tv_name.text = bundle.getString("name")
            tv_address.text = bundle.getString("address")
            foodTruckDataStr = bundle.getString("foodTruckData", ConstVar.EMPTY_STRING)

            if (merchantId > 0) {
                viewModel?.getMerchantParkingSchedule(merchantId, typesId)

                if (merchantLogo != null) {
                    context?.let {
                        val logUrl = ConstVar.PATH_IMAGE + merchantLogo
                        GlideApp.with(it)
                            .load(logUrl)
                            .into(iv_parking)
                    }
                }
            } else {
                viewModel?.getParkingSchedule(parkingSpaceId)
            }
        }

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

    private fun initViewPager(data: String) {
        if (viewPagerAdapter == null) {
            viewPagerAdapter = MerchantInfoPagerAdapter(childFragmentManager, data, merchantIg!!)
        }
        viewpager?.adapter = viewPagerAdapter
        tab?.setupWithViewPager(viewpager)
        tab?.getTabAt(0)?.setIcon(R.drawable.ic_time)
        tab?.getTabAt(1)?.setIcon(R.drawable.ic_instagram)
    }

}