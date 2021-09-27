package com.zeepos.map.ui.dialogs.merchantinfo

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.zeepos.map.R
import com.zeepos.map.ui.MapViewEvent
import com.zeepos.map.ui.MapViewModel
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.ParkingSchedule
import com.zeepos.models.master.FoodTruck
import com.zeepos.ui_base.ui.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_merchant_info.tv_address
import kotlinx.android.synthetic.main.dialog_merchant_info.tv_name
import kotlinx.android.synthetic.main.dialog_parking_schedule_info.*
import javax.inject.Inject

/**
 * Created by Arif S. on 8/18/20
 */
class ParkingSpaceInfoDialogFragment : BaseDialogFragment() {
    private var viewModel: MapViewModel? = null
    private var merchantId: Long = 0
    private var parkingSpaceId: Long = 0
    private lateinit var parkingSpaceInfoAdapter: ParkingSpaceInfoAdapter

//    @Inject
//    lateinit var gson: Gson

    override fun onResume() {
        super.onResume()
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        parkingSpaceInfoAdapter = ParkingSpaceInfoAdapter()

        activity?.let {
            viewModel = ViewModelProvider(it).get(MapViewModel::class.java)

            viewModel?.parkingScheduleObs?.observe(it, Observer { data ->
                Log.d(ConstVar.TAG, "data size -> ${data.size}")
                parkingSpaceInfoAdapter.setList(data)
            })
        }

        initList()

        val bundle = arguments

        if (bundle != null) {
            merchantId = bundle.getLong("merchantId")
            parkingSpaceId = bundle.getLong("parkingSpaceId")
            tv_name.text = bundle.getString("name")
            tv_address.text = bundle.getString("address")

            if (merchantId > 0) {
                viewModel?.getParkingSchedule(merchantId)
            } else {
                viewModel?.getParkingSchedule(parkingSpaceId)
            }
        }

        rcv?.setOnTouchListener { view, motionEvent ->
            view.performClick()
            view.parent.requestDisallowInterceptTouchEvent(true)
            view.onTouchEvent(motionEvent)
            true
        }
    }

    override fun initResourceLayout(): Int {
        return R.layout.dialog_parking_schedule_info
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
    }

    fun initList() {
        rcv?.apply {
            if (context != null)
                layoutManager = LinearLayoutManager(context)
            adapter = parkingSpaceInfoAdapter
        }

        parkingSpaceInfoAdapter.setOnItemClickListener { adapter, view, position ->
            val parkingSchedule = adapter.getItem(position) as ParkingSchedule
            val foodTruck = FoodTruck()
            foodTruck.status = if (parkingSchedule.isCheckin) ConstVar.FOOD_TRUCK_STATUS_CHECK_IN else ConstVar.FOOD_TRUCK_STATUS_CHECK_OUT
            foodTruck.merchantId = parkingSchedule.merchantId
            foodTruck.merchantName = parkingSchedule.merchantName
            foodTruck.name = parkingSchedule.merchantName
            foodTruck.logo = parkingSchedule.logo
            foodTruck.banner = parkingSchedule.banner
            foodTruck.address = parkingSchedule.address

            val bundle = Bundle()
            bundle.putString("foodTruckData", Gson().toJson(foodTruck))

            viewModel?.viewEventObservable?.postValue(
                MapViewEvent.GoToMerchantMenuScreen(
                    foodTruck.merchantId, bundle
                )
            )
        }
    }
}