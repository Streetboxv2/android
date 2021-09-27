package id.streetbox.live.ui.main.address

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.zeepos.models.master.User
import com.zeepos.models.response.DataAddress
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.TrackGps
import com.zeepos.utilities.intentPageData
import com.zeepos.utilities.showToastExt
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterAddress
import id.streetbox.live.adapter.OnClickSelectItemChecked
import id.streetbox.live.ui.onclick.OnClickItemAny
import id.streetbox.live.ui.onclick.OnClickItemSelect
import kotlinx.android.synthetic.main.activity_address_delivery.*
import javax.inject.Inject

class AddressDeliveryActivity : BaseActivity<AddressViewEvent, AddressViewModel>() {

    @Inject
    lateinit var gson: Gson
    var user: User? = null
    var from: String? = ""
    var isCheckedAdress = false
    var dataAddressChecked: DataAddress? = null

    var trackGps: TrackGps? = null
    var latitude = 0.0
    var longitude = 0.0
    var isAddresStatusCall = false

    val onClickItemAny = object : OnClickItemAny {
        override fun clickItem(any: Any) {
            val dataAddress = any as DataAddress
            val intent = Intent()
                .putExtra("data", dataAddress)
            setResult(10, intent)
            finish()
        }
    }

    val onClickItemSelect = object : OnClickItemSelect {
        override fun clickItem(any: Any) {
            val dataAddress = any as DataAddress
            val intent =
                intentPageData(this@AddressDeliveryActivity, AddAddressActivity::class.java)
                    .putExtra("data", dataAddress)
            startActivity(intent)
        }

        override fun clickItemMarkAsAddress(any: Any) {
            showLoading()
            val dataAddress = any as DataAddress
            viewModel.callApiPrimaryAddress(dataAddress.id.toString())
            updateLocation(dataAddress.latitude!!, dataAddress.longitude!!)
        }

        override fun clickItemDelete(any: Any) {
            showLoading()
            val dataAddress = any as DataAddress
            viewModel.callApiDeleteAddress(dataAddress.id.toString())
        }
    }

    override fun initResourceLayout(): Int {
        return R.layout.activity_address_delivery
    }

    override fun init() {
        val bundle = intent.extras
        val userStr = bundle?.getString("user")
        user = gson.fromJson(userStr, User::class.java)
        from = intent.getStringExtra("from")
        showLoading()
        viewModel = ViewModelProvider(this, viewModeFactory).get(AddressViewModel::class.java)
        initCurrentLocation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.callGetAddress()
    }


    private fun initCurrentLocation() {
        trackGps = TrackGps(this)
        if (trackGps!!.canGetLocation()) {
            latitude = trackGps!!.latitude
            longitude = trackGps!!.longitude
        }
    }


    private fun updateLocation(latitude: Double, longitude: Double) {
        val map = mutableMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
        viewModel.callUpdateLocation(map)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        btnAddAddress.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user", gson.toJson(user))
            val intent = intentPageData(this, AddAddressActivity::class.java)
                .putExtras(bundle)
            startActivity(intent)
        }

        btnSelectAddress.setOnClickListener {
            if (from.equals("homevisit")) {
                if (dataAddressChecked != null) {
                    val intent = Intent()
                    intent.putExtra("data", dataAddressChecked)
                    setResult(10, intent)
                    finish()
                } else showToastExt("Anda belum memilih Alamat", this)
            }
        }
    }

    override fun onEvent(useCase: AddressViewEvent) {
        dismissLoading()
        when (useCase) {
            is AddressViewEvent.OnSuccessListAddress -> {
                val dataAddress = useCase.responseListAddress.data
                initAdapter(dataAddress)
            }

            is AddressViewEvent.OnSuccessAddress -> {
                showToastExt("Suksess", this)
                onResume()
            }

            is AddressViewEvent.OnFailedAddress -> {

            }
            is AddressViewEvent.OnSuccessCallFoodTruck -> {
                dismissLoading()
            }
        }
    }

    private fun initAdapter(dataAddress: List<DataAddress?>?) {
        val adapterAddress =
            AdapterAddress(
                from,
                dataAddress = dataAddress as MutableList<DataAddress>,
                onClickItemAny = onClickItemAny,
                onClickItemSelect = onClickItemSelect,
                onClickSelectItemChecked = object : OnClickSelectItemChecked {
                    override fun onChecked(any: Any) {
                        dataAddressChecked = any as DataAddress
                        val intent = Intent()
                        intent.putExtra("data", dataAddressChecked)
                        setResult(10, intent)
                        finish()
                    }
                }
            )

        rvListAddress.apply {
            adapter = adapterAddress
            layoutManager = LinearLayoutManager(this@AddressDeliveryActivity)
            hasFixedSize()
        }
    }
}