package com.zeepos.streetbox.ui.broadcast.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chad.library.adapter.base.listener.OnClickItemAny
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.response.DataItemGetStatusCallFoodTruck
import com.zeepos.streetbox.R
import com.zeepos.streetbox.adapter.AdapterStatusCallFoodTruck
import com.zeepos.streetbox.ui.broadcast.BroadCastViewEvent
import com.zeepos.streetbox.ui.broadcast.BroadCastViewModel
import com.zeepos.streetbox.ui.broadcast.callaccepted.AcceptedCallMapsActivity
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.hideView
import com.zeepos.utilities.intentPageData
import com.zeepos.utilities.setRvAdapterVertikal
import com.zeepos.utilities.showView
import kotlinx.android.synthetic.main.fragment_accepted_call.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryAllBlast.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryAllBlastFragment : BaseFragment<BroadCastViewEvent, BroadCastViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    val onClickItemAny = object : OnClickItemAny {
        override fun clickItem(any: Any, pos: Int) {
//            val dataItemGetStatusCallFoodTruck = any as DataItemGetStatusCallFoodTruck
//            val intent = intentPageData(requireContext(), AcceptedCallMapsActivity::class.java)
//                .putExtra(ConstVar.DATA_ITEM_STATUS_CALL_FOODTRUCK, dataItemGetStatusCallFoodTruck)
//            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryAllBlast.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryAllBlastFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initResourceLayout(): Int {
        return R.layout.fragment_history_all_blast
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(BroadCastViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.callGetStatusCallEndUser("history")
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        showView(progressListBlast)
    }

    override fun onEvent(useCase: BroadCastViewEvent) {
        when (useCase) {
            is BroadCastViewEvent.OnSuccessList -> {
                val dataItem = useCase.dataItemGetStatusCallFoodTruck.data
                if (dataItem!!.isNotEmpty())
                    initAdapter(dataItem)
                else {
                    hideView(progressListBlast)
                    showView(tvNotFoundAccepted)
                    hideView(rvStatusCallFoodTruck)
                }
            }
            is BroadCastViewEvent.OnFailed -> {
                hideView(progressListBlast)
            }
        }
    }

    private fun initAdapter(dataItem: List<DataItemGetStatusCallFoodTruck?>) {
        hideView(progressListBlast)
        hideView(tvNotFoundAccepted)
        showView(rvStatusCallFoodTruck)
        val groupieAdapter = GroupAdapter<GroupieViewHolder>().apply {
            dataItem.forEach {
                add(AdapterStatusCallFoodTruck(it!!, onClickItemAny))
            }
        }

        setRvAdapterVertikal(rvStatusCallFoodTruck, groupieAdapter)
    }
}