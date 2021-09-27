package com.zeepos.map.ui.dialogs.merchantinfo

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.zeepos.map.R
import com.zeepos.map.ui.MapViewEvent
import com.zeepos.map.ui.MapViewModel
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import kotlinx.android.synthetic.main.dialog_merchant_menu.*

/**
 * Created by Arif S. on 8/19/20
 */
class MerchantViewMenuDialog : BottomSheetDialogFragment() {
    private var viewModel: MapViewModel? = null
    private var foodTruck: FoodTruck? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_merchant_menu, container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProvider(it).get(MapViewModel::class.java)
        }

        val bundle = arguments

        if (bundle != null) {

            val foodTruckStr = bundle.getString("foodTruckData", ConstVar.EMPTY_STRING)
            if (foodTruckStr.isNotEmpty()) {
                foodTruck = Gson().fromJson(foodTruckStr, FoodTruck::class.java)

                tv_name.text = foodTruck?.name
                txt_address.text = foodTruck?.address
                txt_startdate.text = foodTruck?.schedule
            }
        }

        btn_menu.setOnClickListener {
            val merchantId = foodTruck?.merchantId ?: 0
            if (merchantId > 0)
                viewModel?.viewEventObservable?.postValue(
                    MapViewEvent.GoToMerchantMenuScreen(
                        merchantId, bundle!!
                    )
                )
        }
    }
}