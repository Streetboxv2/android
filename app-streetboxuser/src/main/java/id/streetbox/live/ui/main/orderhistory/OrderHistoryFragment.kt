package id.streetbox.live.ui.main.orderhistory

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.zeepos.ui_base.ui.BaseFragment
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterViewPagerOrderHistory
import kotlinx.android.synthetic.main.fragment_order_history.*
import javax.inject.Inject

/**
 * Created by Arif S. on 6/13/20
 */
class OrderHistoryFragment : BaseFragment<OrderHistoryViewEvent, OrderHistoryViewModel>() {

    @Inject
    lateinit var gson: Gson
    var adapterViewPagerOrderHistory: AdapterViewPagerOrderHistory? = null
    private val titles =
        arrayOf("History", "Ongoing")

    override fun initResourceLayout(): Int {
        return R.layout.fragment_order_history
    }

    override fun init() {

    }

    private fun setTabViewPager() {
        adapterViewPagerOrderHistory = AdapterViewPagerOrderHistory(titles, this)
        viewPager2OrderHistory.adapter = adapterViewPagerOrderHistory
        TabLayoutMediator(tabOrderHistory, viewPager2OrderHistory,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }
        ).attach()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setTabViewPager()
    }


    override fun onEvent(useCase: OrderHistoryViewEvent) {

    }

    companion object {
        fun newInstance(): OrderHistoryFragment {
            return OrderHistoryFragment()
        }
    }
}