package com.zeepos.map.ui.dialogs

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zeepos.map.R
import com.zeepos.map.ui.MapViewEvent
import com.zeepos.map.ui.MapViewModel
import com.zeepos.models.master.FoodTruck
import kotlinx.android.synthetic.main.bottomsheet_address_display.*
import kotlinx.android.synthetic.main.dialog_merchant_menu.tv_name
import kotlinx.android.synthetic.main.dialog_merchant_menu.txt_address
import java.util.*

/**
 * Created by Arif S. on 9/16/20
 */
class AddressDisplayBottomSheet : BottomSheetDialogFragment() {
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
            R.layout.bottomsheet_address_display, container,
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

            val latitude = bundle.getDouble("latitude")
            val longitude = bundle.getDouble("longitude")

            val geocoder = Geocoder(activity, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )

            if (addresses.isNotEmpty()) {
                val address = addresses[0]

                tv_name.text = address.countryName
                txt_address.setText(address.getAddressLine(0))

                btn_pick.setOnClickListener {
                    val ad = com.zeepos.models.master.Address()
                    ad.address = txt_address.text.toString()
                    ad.latitude = latitude
                    ad.longitude = longitude
                    viewModel?.viewEventObservable?.postValue(
                        MapViewEvent.OnSelectedAddress(ad)
                    )
                }
            }
        }

    }

    companion object {
        fun getInstance(latitude: Double, longitude: Double): AddressDisplayBottomSheet {
            val bundle = Bundle()
            bundle.putDouble("latitude", latitude)
            bundle.putDouble("longitude", longitude)

            val fragment = AddressDisplayBottomSheet()
            fragment.arguments = bundle

            return fragment
        }
    }
}