package id.streetbox.live.ui.bookhomevisit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.common.api.Status
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.zeepos.map.ui.MapActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.AvailableHomeVisitBookDate
import com.zeepos.models.entities.BookHomeVisit
import com.zeepos.models.entities.MenuItem
import com.zeepos.models.entities.VisitSales
import com.zeepos.models.master.Address
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.response.DataAddress
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.ActiveDateValidator
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.showToastExt
import com.zeepos.utilities.showView
import id.streetbox.live.R
import id.streetbox.live.ui.main.MainActivity
import id.streetbox.live.ui.main.address.AddressDeliveryActivity
import id.streetbox.live.ui.orderreview.homevisit.BookHomeVisitOrderActivity
import kotlinx.android.synthetic.main.activity_book_home_visit.*
import kotlinx.android.synthetic.main.activity_book_home_visit.tv_pickup
import kotlinx.android.synthetic.main.activity_menu.iv_banner
import kotlinx.android.synthetic.main.activity_menu.toolbar
import java.util.*
import javax.inject.Inject

/**
 * Created by Arif S. on 6/25/20
 */
class BookHomeVisitActivity : BaseActivity<BookHomeVisitViewEvent, BookHomeVisitViewModel>() {

    @Inject
    lateinit var gson: Gson

    private var mTotalDeposit: Long = 0
    private var foodTruck: FoodTruck? = null
    private var merchantId: Long = 0
    private var dataTimeMap: Map<Long, List<AvailableHomeVisitBookDate>> = mapOf()
    private var selectedTimeSet: MutableSet<AvailableHomeVisitBookDate> = mutableSetOf()
    private var addressLatLng: LatLng? = null
    private var previousAddressList: MutableList<Address> = mutableListOf()
    var dataAddress: DataAddress? = null

    val menuItemList: MutableList<MenuItem> = mutableListOf()
    var totalMenuItem: Double = 0.0
    var totalQty: Int = 0
    var deposit: Double = 0.0
    var getMenuList: String = ""
    var bookedData = BookHomeVisit()
    val visitSales = VisitSales()
    var visituniqueId : String? = ""

    override fun initResourceLayout(): Int {
        return R.layout.activity_book_home_visit
    }

    override fun onResume() {
        super.onResume()
        init()
//            totalQty

        bookedData.visitSales.clear()
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(BookHomeVisitViewModel::class.java)
        viewModel.callGetAddressPrimary()
        val bundle = intent.extras
        if (bundle != null) {
            visituniqueId = bundle.getString("visituniqueid")
            val foodTruckStr = bundle.getString("foodTruckData")
            if (foodTruckStr != null && foodTruckStr.isNotEmpty()) {
                foodTruck = gson.fromJson(foodTruckStr, FoodTruck::class.java)
                foodTruck?.let {
                    merchantId = it.merchantId

                    if (merchantId <= 0) {
                        merchantId = intent.getLongExtra(MERCHANT_ID, 0)
                        it.merchantId = merchantId
                    }
                }
                totalMenuItem = bundle.getDouble("total")
//                totalQty = bundle.getInt("qty", 0)
                getMenuList = bundle.getString("menulist")!!
                val menuItem = gson.fromJson(getMenuList, Array<ProductSales>::class.java).toList()

                menuItem.forEach {
                    menuItemList.addAll(
                        listOf(
                            MenuItem(it.productId, it.qty)
                        )
                    )
                    totalQty += it.qty
                    println("respon Id add menu ${it.id} and produ id ${it.productId}")
                }


                if (menuItem.isNotEmpty()) {
                    mTotalDeposit = totalMenuItem.toLong()
                } else {
                    mTotalDeposit = deposit.toLong()
                }
                println("respon Total item $totalMenuItem")
            }
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationIcon(R.drawable.ic_back_menu)
        toolbar.setNavigationOnClickListener { finish() }

        val merchantName = foodTruck?.merchantName
        val displayName =
            if (merchantName != null && merchantName.isNotEmpty()) merchantName else foodTruck?.name


        tv_name.text = displayName
        tv_address.text = foodTruck?.address
        tv_description.text = foodTruck?.terms
        tv_address_1.visibility = View.GONE
        tv_address_2.visibility = View.GONE
        tv_previous_address_label.visibility = View.GONE

        tv_subtotal.text = "${NumberUtil.formatToStringWithoutDecimal(totalMenuItem)}"
        tv_item_count.text = "$totalQty Items"

        val imageUrl: String = ConstVar.PATH_IMAGE + foodTruck?.banner

        Glide.with(this)
            .load(imageUrl)
            .into(iv_banner)

        showLoading()
        viewModel.getBookAvailableDate(merchantId)

        tv_address_1.setOnClickListener {
            if (previousAddressList.isNotEmpty())
                fillAddress(previousAddressList[0])
        }
        tv_address_2.setOnClickListener {
            if (previousAddressList.isNotEmpty() && previousAddressList.size > 1)
                fillAddress(previousAddressList[1])
        }

        selectAddress()

        tv_pickup.setOnClickListener {
            if (selectedTimeSet.isEmpty()) {
                Toast.makeText(this, "Please select at least one slot time", Toast.LENGTH_SHORT)
                    .show()
            } else if (dataAddress == null) {
                showToastExt("Delivery address not choice", this)
            } else if (totalMenuItem < deposit) {
                showToastExt("Total price item limited deposit", this)
            } else {
                if (cbAgreesHomeVisit.isChecked) {

                    bookedData.address = dataAddress?.address.toString()
                    bookedData.customerName = dataAddress?.person.toString()
                    bookedData.phone = dataAddress?.phone.toString()
                    bookedData.grandTotal = totalMenuItem.toLong()
                    bookedData.latitude = dataAddress?.latitude!!
                    bookedData.longitude = dataAddress?.longitude!!
                    et_notes.visibility = View.VISIBLE
                    val getNotes = et_notes.text.toString()

                    if (getNotes.isEmpty()) {
                        bookedData.notes = "Notes Empty"
                    } else {
                        bookedData.notes = et_notes.text.toString()
                    }

                    //save address to local
                    val address = Address()
                    address.name = bookedData.customerName
                    address.address = bookedData.address
                    address.phone = bookedData.phone
                    address.note = bookedData.notes
                    address.latitude = bookedData.latitude
                    address.longitude = bookedData.longitude
                    address.createdAt = DateTimeUtil.getCurrentDateTime()
                    address.updatedAt = DateTimeUtil.getCurrentDateTime()
                    viewModel.saveAddress(address)

                    selectedTimeSet.forEach {

                        visitSales.salesId = it.id
                        visitSales.deposit = it.deposit.toLong()
                        visitSales.scheduleDate = it.scheduleDate
                        visitSales.startTime =
                            DateTimeUtil.getDateFromString(it.startDate)?.time ?: 0L
                        visitSales.endTime =
                            DateTimeUtil.getDateFromString(it.endDate)?.time ?: 0L
                        visitSales.menus = menuItemList
                        bookedData.visitSales.add(visitSales)
                    }

                    val bundle = Bundle()
                    bundle.putString("foodTruckData", gson.toJson(foodTruck))
                    bundle.putString("bookedData", gson.toJson(bookedData))
                    bundle.putString("menulist", getMenuList)
                    bundle.putString("visituniqueid", visituniqueId)
                    bundle.putDouble("deposit", mTotalDeposit.toDouble())
                    bundle.putDouble("total", totalMenuItem)

                    startActivityForResult(
                        BookHomeVisitOrderActivity.getIntent(this, bundle),
                        REQ_CODE_ORDER_VIEW
                    )
                } else showToastExt("I Agree not selected", this)

            }
        }

        et_address.setOnClickListener {
//            launchLocationAutoCompleteActivity(RC_ADDRESS)
            startActivityForResult(
                MapActivity.getIntent(
                    this,
                    type = ConstVar.MAP_TYPE_PICK_LOCATION
                ), RC_ADDRESS_PICK_MAP
            )
        }

        rl_select_date.setOnClickListener {

            selectDate(dataTimeMap)

//            try {
//                val packageInfo: PackageInfo =
//                    packageManager.getPackageInfo(packageName, 0)
//                Log.d(ConstVar.TAG, packageInfo.packageName)
//            } catch (e: PackageManager.NameNotFoundException) {
//            }

//            val intent =
//                Intent("ovo.id")
//
//            intent.putExtra("SCAN_MODE", "QR_CODE_MODE")
//            startActivityForResult(intent, 0)

//            val launchIntent =
//                packageManager.getLaunchIntentForPackage("ovo.id")
//
//            if (launchIntent != null) {
//                startActivity(launchIntent);//null pointer check in case package name was not found
//            }


//            val intent = Intent()
//                // Show only images, no videos or anything else
//                intent.type = "image/*"
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(intent, 1001)

        }

        viewModel.getAllAddress()
    }

    private fun selectAddress() {
        val launchSomeActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == 10) {
                    val data: Intent? = result.data
                    dataAddress =
                        data?.getParcelableExtra("data")

                    showView(llAddressSelect)
                    tvItemNameAddressSelect.text = dataAddress?.addressName
                    tvItemAddressSelect.text = dataAddress?.address
                    tvItemPesonAddressSelect.text = dataAddress?.person
                    tvItemPhoneAddressSelect.text = dataAddress?.phone

                }
            }
        tvChangeDeliveryAddress.setOnClickListener {
            val intent = Intent(this, AddressDeliveryActivity::class.java)
                .putExtra("from", "homevisit")
            launchSomeActivity.launch(intent)
        }
    }

    private fun validateForm(): Boolean {
        val lat = addressLatLng?.latitude ?: 0.0
        val lng = addressLatLng?.longitude ?: 0.0

        return et_address.text.toString().isNotEmpty() &&
                et_name.text.toString().isNotEmpty() &&
                et_notes.text.toString().isNotEmpty() &&
                et_phone.text.toString().isNotEmpty() &&
                lat != 0.0 && lng != 0.0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1001 &&
            data != null &&
            data.data != null
        ) {

            val shareIntent: Intent = Intent().apply {
                action = Intent.CATEGORY_BROWSABLE
                putExtra(Intent.EXTRA_STREAM, data.data)
                type = "image/*"
                setClassName("ovo.id", "ovo.id.wallet.scan.presentation.scanner.ScannerActivity")
            }
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.app_name)))
        } else if (requestCode == RC_ADDRESS) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.d(
                        ConstVar.TAG,
                        "Place: " + place.name + ", " + place.id + ", " + place.latLng
                    )

                    addressLatLng = place.latLng
                    et_address.setText(place.address)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Log.d(ConstVar.TAG, status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> {
                    Log.d(ConstVar.TAG, "Place Selection Canceled")
                }
            }
        } else if (requestCode == REQ_CODE_ORDER_VIEW) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        } else if (requestCode == RC_ADDRESS_PICK_MAP) {
            when (resultCode) {
                RESULT_OK -> {
                    val address = data?.getStringExtra("address")
                    val latitude = data?.getDoubleExtra("latitude", 0.0)
                    val longitude = data?.getDoubleExtra("longitude", 0.0)

                    addressLatLng = LatLng(latitude!!, longitude!!)
                    et_address.setText(address)
                }
            }
        }
    }

    override fun onEvent(useCase: BookHomeVisitViewEvent) {
        when (useCase) {
            is BookHomeVisitViewEvent.OnSuccessListAddress -> {
                dataAddress = useCase.responseListAddress.data
                println("respon Errror ${dataAddress}")
                showView(llAddressSelect)
                tvItemNameAddressSelect.text = dataAddress?.addressName
                tvItemAddressSelect.text = dataAddress?.address
                tvItemPesonAddressSelect.text = dataAddress?.person
                tvItemPhoneAddressSelect.text = dataAddress?.phone
                et_notes.visibility = View.VISIBLE
            }
            is BookHomeVisitViewEvent.OnFailedAddress -> {
                val throwable = useCase.throwable
                println("respon Errror ${throwable.message}")
            }
            is BookHomeVisitViewEvent.GetFoodTruckHomeVisitDataSuccess -> {
                //getdeposit
                dismissLoading()
                dataTimeMap = viewModel.collectDate(useCase.data)
                deposit = useCase.data[0].deposit

                val currentTime =
                    DateTimeUtil.getCurrentDateWithoutTime(DateTimeUtil.getCurrentDateTime())
                val selectedDate = DateTimeUtil.getDateWithFormat(currentTime, "dd MMM yyyy")
                tv_selected_date.text = selectedDate

                if (dataTimeMap.containsKey(currentTime)) {
                    val availableTime =
                        dataTimeMap[DateTimeUtil.getCurrentDateWithoutTime(currentTime)]
                    availableTime?.let { timeList ->
                        showAvailableTime(timeList)
                    }
                }

            }
            is BookHomeVisitViewEvent.GetFoodTruckHomeVisitDataFailed -> {
                dismissLoading()
                Toast.makeText(this, "HomeVisit Is Not Available", Toast.LENGTH_SHORT).show()
                startActivity(MainActivity.getIntent(this))
            }

            is BookHomeVisitViewEvent.OnCalculateDone -> {
                updateOrderView(useCase.totalDeposit)
            }
            is BookHomeVisitViewEvent.GetAllAddressSuccess -> {
                previousAddressList.clear()
                previousAddressList.addAll(useCase.addressList)

                var pos = 0
                previousAddressList.forEach {
                    if (pos == 0) {
                        tv_previous_address_label.visibility = View.VISIBLE
                        tv_address_1.visibility = View.VISIBLE
                        tv_address_1.text = it.address
                    } else if (pos == 1) {
                        tv_address_2.visibility = View.VISIBLE
                        tv_address_2.text = it.address
                        return@forEach
                    }

                    pos = pos.inc()
                }
            }
        }
    }

    private fun fillAddress(address: Address) {
        et_name.setText(address.name)
        et_address.setText(address.address)
        et_phone.setText(address.phone)
        et_notes.setText(address.note)

        addressLatLng = LatLng(address.latitude, address.longitude)
    }

    private fun launchLocationAutoCompleteActivity(requestCode: Int) {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this)
        startActivityForResult(intent, requestCode)
    }

    private fun updateOrderView(totalDeposit: Double) {
        val qty = selectedTimeSet.size
        val textItemCount = if (qty > 1) "$qty Items" else "$qty Item"
        mTotalDeposit = totalDeposit.toLong()

//        tv_item_count.text = textItemCount
//        tv_subtotal.text = "${NumberUtil.formatToStringWithoutDecimal(totalDeposit)}"
    }

    private fun selectDate(dataTimeMap: Map<Long, List<AvailableHomeVisitBookDate>>) {
        if (dataTimeMap.isEmpty()) {
            Toast.makeText(this, "No Home visit available", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.clear()

        val activeDate: List<Long> = dataTimeMap.keys.toList()
//        val endDateMillis = activeDate[activeDate.size - 1]
        val startDateMillis = activeDate[0]
        val endDateMillis = activeDate[activeDate.size - 1]

//        calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        calendar.roll(Calendar.MONTH, 1)

        val builder: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
        val currentTimeMillis = DateTimeUtil.getCurrentDateTime()
        val constraintBuilder = CalendarConstraints.Builder()

        builder.setTitleText("Select date")
        builder.setSelection(startDateMillis)

        constraintBuilder.setStart(startDateMillis)
        constraintBuilder.setEnd(endDateMillis)
        constraintBuilder.setOpenAt(startDateMillis)


        constraintBuilder.setValidator(ActiveDateValidator(activeDate))

        builder.setCalendarConstraints(constraintBuilder.build())

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            val availableTime = dataTimeMap[DateTimeUtil.getCurrentDateWithoutTime(it)]
            val selectedDateDisplay = DateTimeUtil.getDateWithFormat(it, "dd MMM yyyy")

            tv_selected_date.text = selectedDateDisplay

            availableTime?.let { timeList ->
                showAvailableTime(timeList)
            }
        }
        picker.show(supportFragmentManager, picker.toString())
    }

    private fun showAvailableTime(availableTimeList: List<AvailableHomeVisitBookDate>) {
        val fl = findViewById<FlexboxLayout>(R.id.fl)
        fl.removeAllViews()

        for (availableTime in availableTimeList) {
            val startDateMillis =
                DateTimeUtil.getDateFromString(availableTime.startDate)?.time ?: 0L
            val endDateMillis = DateTimeUtil.getDateFromString(availableTime.endDate)?.time ?: 0L
            val startTimeDisplay = DateTimeUtil.getDateWithFormat(startDateMillis, "HH:mm")
            val endTimeDisplay = DateTimeUtil.getDateWithFormat(endDateMillis, "HH:mm")

            val tv = TextView(this)
            val params: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(10, 10, 10, 10)
            tv.layoutParams = params
            tv.background = ContextCompat.getDrawable(this, R.drawable.time_slot_unselect_bg)
            tv.text = "$startTimeDisplay - $endTimeDisplay"
            tv.setTextColor(ContextCompat.getColor(this, R.color.color_text_regular))
            tv.setPadding(40, 20, 40, 20)
            tv.tag = availableTime
            tv.setOnClickListener { view ->
                val data = view.tag
                if (data is AvailableHomeVisitBookDate) {
                    if (selectedTimeSet.contains(data)) {
                        selectedTimeSet.remove(data)

                        if (view is TextView) {
                            view.background =
                                ContextCompat.getDrawable(this, R.drawable.time_slot_unselect_bg)
                            view.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.color_text_regular
                                )
                            )
                        }
                    } else {
                        selectedTimeSet.add(data)
                        deposit = availableTime.deposit
                        if (view is TextView) {
                            view.setTextColor(ContextCompat.getColor(this, R.color.white))
                            view.background =
                                ContextCompat.getDrawable(this, R.drawable.button_add_bg)
                        }
                    }
                }

                viewModel.calculate(selectedTimeSet)
            }

            fl.addView(tv)
        }
    }

    companion object {
        private const val MERCHANT_ID = "merchantId"
        private const val REQ_CODE_ORDER_VIEW = 1011
        private const val RC_ADDRESS = 1
        private const val RC_ADDRESS_PICK_MAP = 2

        fun getIntent(context: Context, merchantId: Long = 0, bundle: Bundle? = null): Intent {
            val intent = Intent(context, BookHomeVisitActivity::class.java)
            intent.putExtra(MERCHANT_ID, merchantId)
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            return intent
        }
    }
}