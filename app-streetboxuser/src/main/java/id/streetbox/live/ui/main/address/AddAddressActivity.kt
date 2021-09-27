package id.streetbox.live.ui.main.address

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.zeepos.models.master.User
import com.zeepos.models.response.DataAddress
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.TrackGps
import com.zeepos.utilities.showToastExt
import id.streetbox.live.R
import kotlinx.android.synthetic.main.activity_add_address.*
import javax.inject.Inject

class AddAddressActivity : BaseActivity<AddressViewEvent, AddressViewModel>() {
    @Inject
    lateinit var gson: Gson
    var user: User? = null
    var dataAddress: DataAddress? = null
    var listAddress: List<DataAddress?>? = null
    var trackGps: TrackGps? = null
    var latitude = 0.0
    var longitude = 0.0
    var latitudeCoordinate = 0.0
    var longitudeCoordinate = 0.0
    var isCoordinate = false
    var primary = false
    override fun initResourceLayout(): Int {
        return R.layout.activity_add_address
    }

    override fun init() {
        val bundle = intent.extras
        val userStr = bundle?.getString("user")
        user = gson.fromJson(userStr, User::class.java)
        dataAddress = intent.getParcelableExtra("data")
        viewModel = ViewModelProvider(this, viewModeFactory).get(AddressViewModel::class.java)
    }

    private fun initGetUpdateData(dataAddress: DataAddress?) {
        edAddressName.setText(dataAddress?.addressName)
        edReceiverName.setText(dataAddress?.person)
        edAdddressPhone.setText(dataAddress?.phone)
        edAddress.setText(dataAddress?.address)
        primary = dataAddress?.primary!!
        btnSaveAddress.text = "Update"

        latitude = dataAddress.latitude!!
        longitude = dataAddress.longitude!!
        tvLatLangAddress.text = "Lat,Lng : $latitude, $longitude"

    }

    override fun onResume() {
        super.onResume()
        viewModel.callGetAddress()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        if (dataAddress != null) {
            tvAddTitleAddress.text = "Update Address"
            initGetUpdateData(dataAddress)
        } else {
            btnSaveAddress.text = "Save"
            tvAddTitleAddress.text = "Add Address"
            initCurrentLocation()
        }

        toolbar.setNavigationOnClickListener { finish() }

        btnSaveAddress.setOnClickListener {
            showLoading()
            val getAddressName = edAddressName.text.toString()
            val getReceiverName = edReceiverName.text.toString()
            val getAddress = edAddress.text.toString()
            val getPhone = edAdddressPhone.text.toString()

            if (getAddress.isNotEmpty() || getReceiverName.isNotEmpty()
                || getAddressName.isNotEmpty() || getPhone.isNotEmpty()
            ) {
                if (dataAddress != null)
                    updateAddAddress(getAddress, getReceiverName, getAddressName, getPhone)
                else reqAddAddress(getAddress, getReceiverName, getAddressName, getPhone)
            }

        }

        directToMaps()

    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        val map = mutableMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
        viewModel.callUpdateLocation(map)
    }

    private fun directToMaps() {
        val launchSomeActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == 20) {
                    val data: Intent? = result.data
                    isCoordinate = true
                    latitude =
                        data?.getDoubleExtra("lat", 0.0)!!
                    longitude =
                        data.getDoubleExtra("lng", 0.0)

                    println("respon lat $latitudeCoordinate and $longitudeCoordinate")
                    tvLatLangAddress.text = "Lat,Lng : $latitude, $longitude"
                }
            }

        btnDirectToMapsAddress.setOnClickListener {
            val intent = Intent(this, AddressMapsCoordinateActivity::class.java)
            launchSomeActivity.launch(intent)
        }
    }

    private fun updateAddAddress(
        address: String,
        receiverName: String,
        addressName: String,
        phone: String
    ) {
        val map = mutableMapOf<String?, Any?>(
            "address" to address,
            "person" to receiverName,
            "address_name" to addressName,
            "phone" to phone,
            "id" to dataAddress?.id,
            "user_id" to dataAddress?.userId,
            "primary" to primary,
            "latitude" to latitude,
            "longitude" to longitude
        )

        viewModel.callUpdateAddress(map)
    }

    private fun reqAddAddress(
        address: String,
        receiverName: String,
        addressName: String,
        phone: String
    ) {
        val map = mutableMapOf<String?, Any?>(
            "address" to address,
            "person" to receiverName,
            "address_name" to addressName,
            "phone" to phone,
            "user_id" to user?.id?.toInt(),
            "primary" to false,
            "latitude" to latitude,
            "longitude" to longitude
        )
        if (listAddress?.size == 0) {
            updateLocation(latitude, longitude)
        }

        viewModel.callAddAddress(map)

    }

    private fun initCurrentLocation() {
        trackGps = TrackGps(this)
        if (trackGps!!.canGetLocation()) {
            latitude = trackGps!!.latitude
            longitude = trackGps!!.longitude
        }

        tvLatLangAddress.text = "Lat,Lng : $latitude, $longitude"
    }

    override fun onEvent(useCase: AddressViewEvent) {
        dismissLoading()
        when (useCase) {
            is AddressViewEvent.OnSuccessListAddress -> {
                val dataAddress = useCase.responseListAddress.data
                listAddress = dataAddress
            }
            is AddressViewEvent.OnSuccessAddAddress -> {
                finish()
                showToastExt("Success", this)
            }

            is AddressViewEvent.OnFailedAddress -> {

            }
        }
    }
}