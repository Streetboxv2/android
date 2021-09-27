package com.zeepos.streetbox.ui.parkingdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import com.tiagosantos.enchantedviewpager.EnchantedViewPager
import com.zeepos.map.ui.MapActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.master.ParkingSpace
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.models.transaction.ParkingSlotSales
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.operatormerchant.OperatorActivity
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.SlidingImageAdapter
import com.zeepos.utilities.DateTimeUtil
import kotlinx.android.synthetic.main.activity_parking_detail.*

class ParkingDetailActivity : BaseActivity<ParkingDetailViewEvent, ParkingDetailViewModel>() {

    private lateinit var parkingDetailAdapter: ParkingDetailAdapter
    lateinit var parkingData: Any
    private var isBooked: Boolean = false

    override fun initResourceLayout(): Int {
        return R.layout.activity_parking_detail
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ParkingDetailViewModel::class.java)

        val id = intent.getLongExtra("id", 0)

        isBooked = intent.getBooleanExtra("isBooked", false)

        parkingDetailAdapter = ParkingDetailAdapter()

        if (isBooked) {
            showLoading()
            viewModel.getParkingSalesDetail(id)
        } else {
            parkingData = viewModel.getParkingSpace(id)!!

            val parkingSpace = parkingData as ParkingSpace
            if (parkingSpace.parkingSlots.isEmpty()) {
                showLoading()
                viewModel.getParkingDetail(parkingSpace.id)
            } else {
                parkingDetailAdapter.setList(parkingSpace.parkingSlots)
            }
        }

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)

        if (!isBooked)
            initList()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

    }

    private fun initList() {
        rcv_parking_detail?.apply {
            layoutManager = LinearLayoutManager(this@ParkingDetailActivity)
            adapter = parkingDetailAdapter
            parkingDetailAdapter.addHeaderView(getHeaderView())

            parkingDetailAdapter.setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.tv_assign -> {
                        val parkingSlotSales = adapter.getItem(position) as ParkingSlotSales
                        startActivity(
                            OperatorActivity.getIntent(
                                this@ParkingDetailActivity,
                                parkingSlotSales.id
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getHeaderView(): View {
        val view: View =
            layoutInflater.inflate(R.layout.parking_detail_header, rcv_parking_detail, false)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvPoint = view.findViewById<TextView>(R.id.tv_point)
        val tvRating = view.findViewById<TextView>(R.id.tv_rating)
        val tvAddress = view.findViewById<TextView>(R.id.tv_address)
        val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        val vp = view.findViewById<EnchantedViewPager>(R.id.vp_parking_picture)
        val dotIndicator = view.findViewById<WormDotsIndicator>(R.id.vpd_indicator)
        val ivMap = view.findViewById<ImageView>(R.id.iv_map)
        val tvParkingTime = view.findViewById<TextView>(R.id.tv_parking_time)

        if (parkingData is ParkingSpace) {
            val parkingSpace = parkingData as ParkingSpace
            val images = parkingSpace.imagesMeta ?: arrayListOf()
            val slideAdapter = SlidingImageAdapter(this, images)

            val startDateMillis = DateTimeUtil.getDateFromString(parkingSpace.startTime)?.time ?: 0L
            val endDateMillis = DateTimeUtil.getDateFromString(parkingSpace.endTime)?.time ?: 0L
            val startTime = DateTimeUtil.getDateWithFormat(startDateMillis, "HH:mm")
            val endTime = DateTimeUtil.getDateWithFormat(endDateMillis, "HH:mm")

            vp.adapter = slideAdapter
            vp.removeScale()
            dotIndicator.setViewPager(vp)

            tvName.text = parkingSpace.name
            tvRating.text = parkingSpace.rating.toString()
            tvPoint.text = parkingSpace.point.toString()
            tvAddress.text = parkingSpace.address
            tvDescription.text = parkingSpace.description
            tvParkingTime.text = "$startTime - $endTime"

            View.OnClickListener {

            }

            ivMap.setOnClickListener {
                val bundle = Bundle()
                bundle.putDouble("lat", parkingSpace.latitude)
                bundle.putDouble("lng", parkingSpace.longitude)
                startActivity(
                    MapActivity.getIntent(this, type = ConstVar.MAP_TYPE_LOCATION, bundle = bundle)
                )
            }

        } else {
            val parkingSales = parkingData as ParkingSales
            val images = parkingSales.images ?: arrayListOf()
            val slideAdapter = SlidingImageAdapter(this, images)

            val startDateMillis = DateTimeUtil.getDateFromString(parkingSales.startTime)?.time ?: 0L
            val endDateMillis = DateTimeUtil.getDateFromString(parkingSales.endTime)?.time ?: 0L
            val startTime = DateTimeUtil.getDateWithFormat(startDateMillis, "HH:mm")
            val endTime = DateTimeUtil.getDateWithFormat(endDateMillis, "HH:mm")

            vp.adapter = slideAdapter
            vp.removeScale()
            dotIndicator.setViewPager(vp)

            tvName.text = parkingSales.name
            tvRating.text = parkingSales.rating.toString()
            tvAddress.text = parkingSales.address
            tvPoint.visibility = View.INVISIBLE
            tvDescription.text = parkingSales.description
            tvParkingTime.text = "$startTime - $endTime"

            View.OnClickListener {

            }

            ivMap.setOnClickListener {
                val bundle = Bundle()
                bundle.putDouble("lat", parkingSales.latitude)
                bundle.putDouble("lng", parkingSales.longitude)
                startActivity(
                    MapActivity.getIntent(this, type = ConstVar.MAP_TYPE_LOCATION, bundle = bundle)
                )
            }
        }

        return view
    }

    override fun onEvent(useCase: ParkingDetailViewEvent) {
        when (useCase) {
            ParkingDetailViewEvent.UpdateParkingSalesSuccess -> {
                dismissLoading()
            }

            is ParkingDetailViewEvent.GetParkingDetailSuccess -> {
                parkingDetailAdapter.addData(useCase.data)
                dismissLoading()
            }
            is ParkingDetailViewEvent.GetParkingDetailFailed -> {
                Toast.makeText(this, useCase.errorMessage, Toast.LENGTH_SHORT).show()
                dismissLoading()
            }
            is ParkingDetailViewEvent.GetParkingSalesDetailSuccess -> {
                parkingData = useCase.data
                initList()
                parkingDetailAdapter.setList(useCase.data.parkingSlotSales)
                dismissLoading()
            }
            ParkingDetailViewEvent.DisMissLoading -> dismissLoading()
        }
    }

    companion object {
        fun getIntent(context: Context, id: Long, booked: Boolean): Intent {
            val intent = Intent(context, ParkingDetailActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("isBooked", booked)
            return intent
        }
    }

}
