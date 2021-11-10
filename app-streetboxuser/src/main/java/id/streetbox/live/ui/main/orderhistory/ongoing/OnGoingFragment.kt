package id.streetbox.live.ui.main.orderhistory.ongoing

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.zeepos.models.entities.OrderHistory
import com.zeepos.ui_base.ui.BaseFragment
import id.streetbox.live.R
import id.streetbox.live.ui.main.orderhistory.OrderHistoryAdapter
import id.streetbox.live.ui.main.orderhistory.history.HistoryViewEvent
import id.streetbox.live.ui.main.orderhistory.history.HistoryViewModel
import id.streetbox.live.ui.main.orderhistory.orderhistorydetail.OrderHistoryDetailActivity
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_on_going.*
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnGoingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnGoingFragment : BaseFragment<HistoryViewEvent, HistoryViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @Inject
    lateinit var gson: Gson
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private var page: Int = 1


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnGoingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnGoingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initResourceLayout(): Int {
        return R.layout.fragment_on_going
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(HistoryViewModel::class.java)
        orderHistoryAdapter = OrderHistoryAdapter()

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        swipe_refresh_ongoing.setColorSchemeColors(Color.rgb(47, 223, 189))
        swipe_refresh_ongoing.isRefreshing = true
        swipe_refresh_ongoing.setOnRefreshListener {
//            page = 1
            viewModel.getOrderHistory(page, "ongoing")
        }
    }

    override fun onEvent(useCase: HistoryViewEvent) {
        when (useCase) {
            is HistoryViewEvent.GetOrderHistorySuccess -> {
                swipe_refresh_ongoing.isRefreshing = false

                if (useCase.data.isNotEmpty()) {
                    if (page == 1) {
                        orderHistoryAdapter.data.clear()
                        orderHistoryAdapter.loadMoreModule.loadMoreStatus
                    }

                    page = page.inc()
//                    orderHistoryAdapter.addData(useCase.data)
                    orderHistoryAdapter.loadMoreModule.loadMoreComplete()
                    initList()
                } else {
                    swipe_refresh.isRefreshing = false
                    orderHistoryAdapter.loadMoreModule.loadMoreEnd()
                    Toast.makeText(requireContext(), "Tidak ada data", Toast.LENGTH_SHORT).show()
                }
            }
            is HistoryViewEvent.GetOrderHistoryFailed -> {
                swipe_refresh_ongoing.isRefreshing = false
                orderHistoryAdapter.loadMoreModule.loadMoreFail()

                Toast.makeText(context, useCase.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        swipe_refresh_ongoing?.isRefreshing = true
        page = 1
        viewModel.getOrderHistory(page, "ongoing")
    }

    private fun initList() {
        rvOngoing?.apply {
            adapter = orderHistoryAdapter
            val lm = LinearLayoutManager(context)
            layoutManager = lm
            val dividerItemDecoration = DividerItemDecoration(
                context,
                lm.orientation
            )
            addItemDecoration(dividerItemDecoration)
//            orderHistoryAdapter.addHeaderView(getHeaderView())
        }

        orderHistoryAdapter.setOnItemClickListener { adapter, view, position ->
            context?.let {
                val data = adapter.getItem(position) as OrderHistory
                val bundle = Bundle()
                bundle.putString("data", gson.toJson(data))
                startActivity(OrderHistoryDetailActivity.getIntent(it, bundle))
            }
        }

        orderHistoryAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getOrderHistory(page, "ongoing")
        }

    }

}