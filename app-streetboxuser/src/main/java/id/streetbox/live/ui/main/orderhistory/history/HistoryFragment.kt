package id.streetbox.live.ui.main.orderhistory.history

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
import id.streetbox.live.ui.main.orderhistory.orderhistorydetail.OrderHistoryDetailActivity
import kotlinx.android.synthetic.main.fragment_history.*
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryFragment : BaseFragment<HistoryViewEvent, HistoryViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @Inject
    lateinit var gson: Gson
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private var page: Int = 1

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initResourceLayout(): Int {
        return R.layout.fragment_history
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(HistoryViewModel::class.java)
        orderHistoryAdapter = OrderHistoryAdapter()
        page = 1

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        swipe_refresh.setColorSchemeColors(Color.rgb(47, 223, 189))
        swipe_refresh.isRefreshing = true
        swipe_refresh.setOnRefreshListener {
            page = 1
            viewModel.getOrderHistory(page, "history")
        }
    }

    override fun onEvent(useCase: HistoryViewEvent) {
        when (useCase) {
            is HistoryViewEvent.GetOrderHistorySuccess -> {
                swipe_refresh.isRefreshing = false
                orderHistoryAdapter.data.clear()
                if (useCase.data.isNotEmpty()) {
                    if (page == 1) {
//                        orderHistoryAdapter.data.clear()
                        orderHistoryAdapter.loadMoreModule.loadMoreStatus
                    }

                    page = page.inc()

                    orderHistoryAdapter.addData(useCase.data)
                    orderHistoryAdapter.loadMoreModule.loadMoreComplete()
                    initList()
                } else {
                    orderHistoryAdapter.loadMoreModule.loadMoreEnd()
//                    Toast.makeText(requireContext(), "Tidak ada data", Toast.LENGTH_SHORT).show()
                }
            }
            is HistoryViewEvent.GetOrderHistoryFailed -> {
                orderHistoryAdapter.loadMoreModule.loadMoreFail()
                swipe_refresh.isRefreshing = false
                Toast.makeText(context, useCase.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        swipe_refresh?.isRefreshing = true
        page = 1
        viewModel.getOrderHistory(page, "history")
    }

    private fun initList() {
        rcv?.apply {
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
            viewModel.getOrderHistory(page, "history")
        }

    }

}