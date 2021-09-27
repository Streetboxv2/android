package id.streetbox.live.ui.main.home.homevisit

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.zeepos.models.master.FoodTruck
import com.zeepos.ui_base.ui.BaseFragment
import id.streetbox.live.R
import id.streetbox.live.ui.menu.MenuActivity
import kotlinx.android.synthetic.main.fragment_nearby.*
import javax.inject.Inject

/**
 * Created by Arif S. on 6/14/20
 */
class HomeVisitFragment : BaseFragment<HomeVisitViewEvent, HomeVisitViewModel>() {

    @Inject
    lateinit var gson: Gson

    private lateinit var adapter: HomeVisitAdapter
    private var page: Int = 1

    override fun initResourceLayout(): Int {
        return R.layout.fragment_home_visit
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(HomeVisitViewModel::class.java)

        adapter = HomeVisitAdapter()
        viewModel.getHomeVisitFoodTruck(page)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        swipe_refresh.setColorSchemeColors(Color.rgb(47, 223, 189))
        swipe_refresh.isRefreshing = true
        swipe_refresh.setOnRefreshListener {
            page = 1
            viewModel.getHomeVisitFoodTruck(page)
        }

        rcv.apply {
            if (context != null)
                layoutManager = LinearLayoutManager(context)
            adapter = this@HomeVisitFragment.adapter
        }

        adapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getHomeVisitFoodTruck(page)
        }

        adapter.setEmptyView(R.layout.empty_data)

        adapter.setOnItemClickListener { adapter, view, position ->
            val foodTruck = adapter.getItem(position) as FoodTruck
            context?.let {
                val merchantId = foodTruck.id// // this is merchantId
                val bundle = Bundle()
                bundle.putString("foodTruckData", gson.toJson(foodTruck))
                bundle.putString("types", "homevisit")
                startActivity(MenuActivity.getIntent(it, merchantId, bundle))
            }
        }
    }

    override fun onEvent(useCase: HomeVisitViewEvent) {
        when (useCase) {
            is HomeVisitViewEvent.GetFoodTruckHomeVisitSuccess -> {
                swipe_refresh.isRefreshing = false
                if (useCase.data.isNotEmpty()) {
                    if (page == 1) {
                        adapter.data.clear()
                        adapter.loadMoreModule.loadMoreStatus
                    }
                    page = page.inc()
                    adapter.addData(useCase.data)
                    adapter.loadMoreModule.loadMoreComplete()
                } else {
                    adapter.loadMoreModule.loadMoreEnd()
                }
            }
            is HomeVisitViewEvent.GetFoodTruckHomeVisitFailed -> {
                swipe_refresh.isRefreshing = false
                adapter.loadMoreModule.loadMoreFail()
                Toast.makeText(
                    context,
                    useCase.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        fun newInstance(): HomeVisitFragment {
            return HomeVisitFragment()
        }
    }
}