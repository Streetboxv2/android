package com.zeepos.streetbox.ui.dialog

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.operatormerchant.OperatorViewModel
import com.zeepos.ui_base.ui.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_food_truck_detail.*

/**
 * Created by Arif S. on 6/15/20
 */
class FoodTruckDetailDialog : BaseDialogFragment() {
    private var viewModel: OperatorViewModel? = null

    override fun initResourceLayout(): Int {
        return R.layout.dialog_food_truck_detail
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        activity?.let {
            viewModel = ViewModelProvider(it).get(OperatorViewModel::class.java)
        }

        val bundle = arguments
        val id = bundle?.getLong("id", 0)!!

        viewModel?.foodTruckObservable?.observe(this, Observer {
            val name = if (it.platNo != null && it.platNo!!.isNotEmpty())
                it.platNo
            else it.name

            tv_plate_number.text = name
            tv_email.text = it.userName
            tv_address.text = it.address
        })

        viewModel?.getFoodTruck(id)
    }
}