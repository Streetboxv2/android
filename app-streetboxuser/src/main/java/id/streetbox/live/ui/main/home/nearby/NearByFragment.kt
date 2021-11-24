package id.streetbox.live.ui.main.home.nearby

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import com.zeepos.ui_base.ui.BaseFragment
import id.streetbox.live.R
import id.streetbox.live.ui.main.MainViewModel
import id.streetbox.live.ui.menu.MenuActivity
import kotlinx.android.synthetic.main.fragment_nearby.*
import javax.inject.Inject

/**
 * Created by Arif S. on 6/14/20
 */
class NearByFragment : BaseFragment<NearByViewEvent, NearByViewModel>() {

    @Inject
    lateinit var gson: Gson
    private lateinit var adapterNearby: NearByAdapter
    private var currentLatLng: LatLng? = null
    private var mainViewModel: MainViewModel? = null
    private var page: Int = 1
    private var isLocationUpdate = false
    var distance: String? = ""

    override fun initResourceLayout(): Int {
        return R.layout.fragment_nearby
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(NearByViewModel::class.java)

        activity?.let {
            try {
                mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        adapterNearby = NearByAdapter()
        viewModel.getDistanceKm()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        swipe_refresh.setColorSchemeColors(Color.rgb(47, 223, 189))
        swipe_refresh.isRefreshing = true
        swipe_refresh.setOnRefreshListener {
            if (isLocationUpdate) return@setOnRefreshListener
            page = 1
            viewModel.getDistanceKm()
        }

        if (currentLatLng != null) {
            val lat = currentLatLng?.latitude
            val lng = currentLatLng?.longitude
            if (lat != null && lng != null)
                viewModel.getNearByFoodTruck(lat, lng, page, distance.toString())
        }

        rcv.apply {
            if (context != null)
                layoutManager = LinearLayoutManager(context)
            adapter = this@NearByFragment.adapterNearby
        }

        adapterNearby.setEmptyView(R.layout.empty_data)
        adapterNearby.loadMoreModule.setOnLoadMoreListener {
            currentLatLng?.let {
                viewModel.getNearByFoodTruck(it.latitude, it.longitude, page, distance.toString())
            }
        }

        adapterNearby.setOnItemClickListener { adapter, view, position ->
            val foodTruck = adapter.getItem(position) as FoodTruck
            println("respon status  ${foodTruck.status}")

            if (foodTruck.status == ConstVar.FOOD_TRUCK_STATUS_CHECK_IN) {
                val bundle = Bundle()
                bundle.putString("foodTruckData", gson.toJson(foodTruck))
                bundle.putString("types", "nearby")
                startActivity(
                    Intent(requireContext(), NearbyDetailVisitActivity::class.java)
                        .putExtras(bundle)
                        .putExtra(ConstVar.MERCHANT_ID, foodTruck.merchantId)
                )
            }

        }

    }

    override fun onEvent(useCase: NearByViewEvent) {
        var mutableListFoodTruck: MutableList<FoodTruck> = arrayListOf()

        when (useCase) {
            is NearByViewEvent.GetNearBySuccess -> {
                swipe_refresh.isRefreshing = false
                if (useCase.data.isNotEmpty()) {
                    adapterNearby.data.clear()
                    mutableListFoodTruck = useCase.data
                    if (page == 1) {
                        if (isLocationUpdate)
                            isLocationUpdate = false
                        adapterNearby.loadMoreModule.loadMoreStatus
                    }

                    page = page.inc()
                    adapterNearby.addData(mutableListFoodTruck)
                    adapterNearby.loadMoreModule.loadMoreComplete()
                } else {
                    adapterNearby.loadMoreModule.loadMoreEnd()
                }
            }

            is NearByViewEvent.GetSuccessDistanceKm -> {
                swipe_refresh.isRefreshing = false
                if (useCase.dataDistance.data != null) {
                    distance = useCase.dataDistance.data!!.value.toString()
                    mainViewModel?.location?.observe(this, Observer { location: Location? ->
                        if (location != null) {
                            isLocationUpdate = true
                            page = 1
                            currentLatLng = LatLng(location.latitude, location.longitude)
                            viewModel.getNearByFoodTruck(
                                location.latitude, location.longitude, page,
                                distance.toString()
                            )
                        }
                    })
                }
            }

            NearByViewEvent.GetNearByFailed -> {
                swipe_refresh.isRefreshing = false
                adapterNearby.loadMoreModule.loadMoreFail()
            }
        }
    }

    companion object {
        fun newInstance(): NearByFragment {
            return NearByFragment()
        }
    }
}