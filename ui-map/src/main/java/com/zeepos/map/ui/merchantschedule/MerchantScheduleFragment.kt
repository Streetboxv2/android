package com.zeepos.map.ui.merchantschedule

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.zeepos.map.R
import com.zeepos.models.entities.Schedule
import com.zeepos.ui_base.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_merchant_schedule.*
import javax.inject.Inject

/**
 * Created by Arif S. on 8/12/20
 */
class MerchantScheduleFragment :
    BaseFragment<MerchantScheduleViewEvent, MerchantScheduleViewModel>() {

    @Inject
    lateinit var gson: Gson
    private lateinit var merchantScheduleAdapter: MerchantScheduleAdapter
    private lateinit var scheduleList: List<Schedule>

    override fun initResourceLayout(): Int {
        return R.layout.fragment_merchant_schedule
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(MerchantScheduleViewModel::class.java)

        val bundle = arguments!!
        val dataStr = bundle.getString("data")!!
        scheduleList = gson.fromJson(dataStr, Array<Schedule>::class.java).asList()

        merchantScheduleAdapter = MerchantScheduleAdapter(scheduleList as MutableList<Schedule>)

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initList()
    }

    override fun onEvent(useCase: MerchantScheduleViewEvent) {
    }

    private fun initList() {
        rcv.apply {
            val lm = LinearLayoutManager(context)
            if (context != null)
                layoutManager = lm
            adapter = merchantScheduleAdapter
            val dividerItemDecoration = DividerItemDecoration(
                context,
                lm.orientation
            )
            addItemDecoration(dividerItemDecoration)
        }

//        rcv.setOnTouchListener { view, motionEvent ->
//            view.performClick()
//            view.parent.requestDisallowInterceptTouchEvent(true)
//            view.onTouchEvent(motionEvent)
//            true
//        }
    }

    companion object {
        fun newInstance(data: String): MerchantScheduleFragment {
            val bundle = Bundle()
            bundle.putString("data", data)
            val fragment = MerchantScheduleFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}