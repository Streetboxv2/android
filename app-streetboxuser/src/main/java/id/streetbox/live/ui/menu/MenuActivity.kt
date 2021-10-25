package id.streetbox.live.ui.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.zeepos.extension.isVisible
import com.zeepos.models.ConstVar
import com.zeepos.models.ConstVar.MERCHANT_ID
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.*
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterMenu
import id.streetbox.live.adapter.AdapterViewPagerMenuVisit
import com.example.dbroom.db.room.enitity.MenuItemStore
import id.streetbox.live.ui.bookhomevisit.BookHomeVisitActivity
import id.streetbox.live.ui.main.doortodoor.notification.NotifInstagramActivity
import id.streetbox.live.ui.main.home.nearby.NearbyDetailVisitActivity
import id.streetbox.live.ui.pickuporder.PickupOrderActivity
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.header_product.*
import javax.inject.Inject

/**
 * Created by Arif S. on 6/20/20
 */
class MenuActivity : BaseActivity<MenuViewEvent, MenuViewModel>() {

    private var rlBookHomeVisit: RelativeLayout? = null
    private var isCanOrder: Boolean = true
    private var tvParkingTime: TextView? = null
    var adapterViewPagerMenuVisit: AdapterViewPagerMenuVisit? = null
    private val titles =
        arrayOf("Menu", "Instagram")
    var getSaveListMenu: List<MenuItemStore> = ArrayList()
    var getProductSales: List<ProductSales> = ArrayList()

    @Inject
    lateinit var gson: Gson

    private var scheduleStr: String = ConstVar.EMPTY_STRING

    private var foodTruck: FoodTruck? = null
    private lateinit var menuAdapter: MenuAdapter
    var adapterMenu: AdapterMenu? = null

    private var merchantId: Long = 0
    private lateinit var order: Order
    private var isGetOrderSuccess: Boolean = false
    var types: String? = ""
    var total: Double = 0.0
    var qtyItems = 0


    val onClickIncrease = object : AdapterMenu.OnClickIncrease {
        override fun ClickIncrease(position: Int, product: Product, value: Int, tvQty: TextView) {
            if (value > product.qty) {
                showToastExt("Maximal limited", this@MenuActivity)
            } else {
                Hawk.put("saveAddressToko", foodTruck?.address)
                tvQty.text = value.toString()
                viewModel.addItem(product, order)
                if (types.equals("homevisit", ignoreCase = true)) {
                    Hawk.put("saveCart", "visit")
                } else {
                    Hawk.put("saveCart", "nearby")
                }

            }
        }

        override fun ClickDecrease(position: Int, product: Product, value: Int, tvQty: TextView) {
            Hawk.put("saveAddressToko", foodTruck?.address)
            tvQty.text = value.toString()
            viewModel.removeProductSales(product, order)
            if (types.equals("homevisit", ignoreCase = true)) {
                Hawk.put("saveCart", "visit")
            } else {
                Hawk.put("saveCart", "nearby")
            }

        }

        override fun ClickTvNoted(position: Int, product: Product) {
            var productSales: ProductSales? = null
            order.productSales.forEach {
                if (it.productId == product.id) {
                    productSales = it
                    return@forEach
                }
            }

            if (productSales != null) {
                productSales?.let {
                    showProductNotesDialog(it)
                }
            } else {
                showToastExt("Please add item first before take note", this@MenuActivity)
            }
        }
    }


    override fun initResourceLayout(): Int {
        return R.layout.activity_menu
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(MenuViewModel::class.java)
        merchantId = intent.getLongExtra(MERCHANT_ID, 0)

        val bundle = intent.extras
        if (bundle != null) {
            val foodTruckStr = bundle.getString("foodTruckData", ConstVar.EMPTY_STRING)
            types = bundle.getString("types")
            if (foodTruckStr.isNotEmpty()) {
                foodTruck = gson.fromJson(foodTruckStr, FoodTruck::class.java)
                val typeId = foodTruck?.typesId ?: 0
                if (typeId > 0)
                    viewModel.getMerchantParkingSchedule(merchantId, typeId)
            }
        }

        menuAdapter = MenuAdapter()
    }


    override fun onResume() {
        super.onResume()
        showLoading()
        isGetOrderSuccess = false
        viewModel.getBookAvailableDate(merchantId)
        if (types.equals("homevisit", ignoreCase = true)) {
            tvTitleMenuHeader.text = "Menu Visit"
            viewModel.getProduct("visit", merchantId)
        } else {
            tvTitleMenuHeader.text = "Menu Nearby"
            viewModel.getProduct("nearby", merchantId)
        }
    }


    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationIcon(R.drawable.ic_back_menu)
        toolbar.setNavigationOnClickListener { finish() }

//        initListView()

        if (types.equals("homevisit", ignoreCase = true)) {
            tvTitleMenuHeader.text = "Menu Visit"
            viewModel.getProduct("visit", merchantId)
        } else {
            tvTitleMenuHeader.text = "Menu Nearby"
            viewModel.getProduct("nearby", merchantId)
        }

        initHeaderProduct()
        val imageUrl: String = ConstVar.PATH_IMAGE + foodTruck?.banner

        GlideApp.with(this)
            .load(imageUrl)
            .into(iv_banner)

        rl_order_summary.visibility = View.GONE

        tv_pickup.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("foodTruckData", gson.toJson(foodTruck))
            bundle.putString("menulist", gson.toJson(order.productSales))
            bundle.putDouble("total", total)
            bundle.putInt("qty", qtyItems)
            bundle.putString("taxName", order.orderBill[0].taxName)
            bundle.putInt("taxType",order.orderBill[0].taxType)
            bundle.putDouble("totalTax",order.taxSales[0].amount)
            bundle.putBoolean("isActive",order.taxSales[0].isActive)
            bundle.putString("order", gson.toJson(order))

            val intent = Intent(this, PickupOrderActivity::class.java)
                .putExtras(bundle)
            startActivity(intent)
        }

        if (types.equals("homevisit", ignoreCase = true)) {
            showView(imgInstagramVisit)
            imgInstagramVisit.setOnClickListener {
                val intent = intentPageData(this, NotifInstagramActivity::class.java)
                    .putExtra("merchangIg", foodTruck?.merchantIG)
                startActivity(intent)
            }
        }

//        initViewTabViewPager()
    }

    private fun initHeaderProduct() {
        if (foodTruck?.merchantName?.isNotEmpty()!!) {
            Hawk.put("saveTitleToko", foodTruck?.merchantName)
            tv_name.text = foodTruck?.merchantName
        } else {
            Hawk.put("saveTitleToko", foodTruck?.name)
            tv_name.text = foodTruck?.name
        }

        val address = foodTruck?.address ?: ConstVar.EMPTY_STRING

        tv_address.text = address
        tvParkingTime?.text = scheduleStr

        if (address.isEmpty()) {
            tv_address.visibility = View.INVISIBLE
            iv_location.visibility = View.INVISIBLE
        }

        foodTruck?.let {
            when (it.types) {
                ConstVar.FOODTRUCK_TYPE_REGULAR -> {
                    tvParkingTime?.visibility = View.VISIBLE
                    iv_time.visibility = View.VISIBLE
                    tvParkingTime?.text = foodTruck?.schedule
                }
                else -> {
                    tvParkingTime?.visibility = View.INVISIBLE
                    iv_time.visibility = View.INVISIBLE
                }
            }
        }

        rlBookHomeVisit?.visibility = View.GONE
        rl_home_visit?.setOnClickListener {
            if (types.equals("homevisit")) {
                if (getProductSales.isNotEmpty()) {
                    val bundle = Bundle()
                    bundle.putString("foodTruckData", gson.toJson(foodTruck))
                    bundle.putString("menulist", gson.toJson(order.productSales))
                    bundle.putDouble("total", total)
                    bundle.putInt("qty", qtyItems)
                    startActivity(BookHomeVisitActivity.getIntent(this, merchantId, bundle))
                } else showToastExt("Item not choice", this)

            } else {
                val bundle = Bundle()
                bundle.putString("foodTruckData", gson.toJson(foodTruck))
                startActivity(
                    Intent(this, NearbyDetailVisitActivity::class.java)
                        .putExtras(bundle)
                        .putExtra(MERCHANT_ID, merchantId)
                )
            }
        }

    }

    private fun initAdapterMenu(
        mutablistData: MutableList<Product>,
        getSaveListMenu: List<MenuItemStore>
    ) {
        adapterMenu = AdapterMenu(mutablistData, types.toString(), getSaveListMenu)
        rcv.apply {
            adapter = adapterMenu
            layoutManager = LinearLayoutManager(this@MenuActivity)
            hasFixedSize()
        }
        adapterMenu!!.onClickIncrease = onClickIncrease
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_ORDER_VIEW) {
                finish()
            }
        }
    }

    override fun onEvent(useCase: MenuViewEvent) {
        when (useCase) {
            is MenuViewEvent.GetProductSuccess -> {
//                menuAdapter.setList(useCase.data)
                dismissLoading()
                val dataProduct = useCase.data
                initAdapterMenu(
                    dataProduct as MutableList<Product>, getSaveListMenu
                )
                viewModel.getMerchantTax(merchantId)
            }
            is MenuViewEvent.GetProductFailed -> {
                dismissLoading()
                showToastExt(useCase.errorMessage, this)
            }
            is MenuViewEvent.GetOrCreateOrderSuccess -> {
                dismissLoading()
                isGetOrderSuccess = true
                order = useCase.order
                adapterMenu?.setProDuctSalesMap(useCase.order.productSales)
                showHideOrderSummary()
            }
            MenuViewEvent.OrderFailedCreated -> {
            }
            is MenuViewEvent.OnCalculateDone -> {
                val orderBill = useCase.orderBill
                order = orderBill.order.target//update order to get updated product sales
                adapterMenu?.setProDuctSalesMap(orderBill.order.target.productSales)
                showHideOrderSummary()
            }
            MenuViewEvent.OnRemoveProductSuccess -> {
                viewModel.calculateOrder(order)
            }
            is MenuViewEvent.AddItemSuccess -> {
                viewModel.calculateOrder(order)
            }
            MenuViewEvent.UpdateProductSalesSuccess -> {
            }
            is MenuViewEvent.GetTaxSuccess -> {
                viewModel.getRecentOrder(merchantId, foodTruck)
            }
            is MenuViewEvent.GetMerchantScheduleSuccess -> {
                dismissLoading()
                if (useCase.scheduleList.isNotEmpty()) {
                    var schedule = useCase.scheduleList[0]

                    useCase.scheduleList.forEach {
                        val scheduleDate = DateTimeUtil.getDateFromString(it.startDate)?.time
                            ?: DateTimeUtil.getCurrentDateTime()
                        val currentDate = DateTimeUtil.getCurrentDateTime()

                        Log.d(
                            ConstVar.TAG,
                            "schedule : ${DateTimeUtil.getCurrentDateWithoutSecond(scheduleDate)}, currentDate : ${
                                DateTimeUtil.getCurrentDateWithoutSecond(
                                    currentDate
                                )
                            }"
                        )
                        if (DateTimeUtil.getCurrentDateWithoutSecond(scheduleDate) >= DateTimeUtil.getCurrentDateWithoutSecond(
                                currentDate
                            )
                        ) {
                            schedule = it
                        }
                    }

                    val day =
                        DateTimeUtil.getDateWithFormat1(schedule.startDate!!, "EEEE")
                    val startScheduleTime =
                        DateTimeUtil.getDateWithFormat1(schedule.startDate!!, "HH:mm")
                    val endScheduleTime =
                        DateTimeUtil.getDateWithFormat1(schedule.endDate!!, "HH:mm")

                    scheduleStr = "$day $startScheduleTime - $endScheduleTime"
                    tvParkingTime?.text = scheduleStr
                }
            }
            is MenuViewEvent.GetMerchantScheduleFailed -> Toast.makeText(
                this,
                useCase.errorMessage,
                Toast.LENGTH_SHORT
            ).show()
            MenuViewEvent.GetTaxFailed -> {
                dismissLoading()
                viewModel.getRecentOrder(merchantId, foodTruck)
            }
            is MenuViewEvent.GetFoodTruckHomeVisitDataSuccess -> {
                dismissLoading()
                if (useCase.data.isNotEmpty()) {
                    rlBookHomeVisit?.visibility = View.VISIBLE
                } else {
                    rlBookHomeVisit?.visibility = View.GONE
                }
            }
            is MenuViewEvent.GetFoodTruckHomeVisitDataFailed -> {
                dismissLoading()
            }
        }
    }

    private fun showProductNotesDialog(productSales: ProductSales) {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_note, null)
        dialog.setView(view)
        val etProductNote = view.findViewById<TextInputEditText>(R.id.et_product_notes)

        etProductNote.setText(productSales.notes)

        dialog.setPositiveButton(
            "Save"
        ) { d, _ ->
            productSales.notes = etProductNote.text.toString()
            d.dismiss()
            viewModel.updateProductSales(productSales)
        }

        dialog.setNegativeButton(
            "Cancel"
        ) { d, _ ->
            d.dismiss()
        }

        dialog.show()


    }

    private fun showHideOrderSummary() {
        getProductSales = order.productSales
        var orderBill: OrderBill? = null

        if (order.orderBill.size > 0) {
            orderBill = order.orderBill.get(0)
        }

        if (getProductSales.isNotEmpty()) {
            if (types.equals("homevisit"))
                rl_order_summary.visibility = View.GONE
            else rl_order_summary.visibility = View.VISIBLE
        } else {
            if (rl_order_summary.isVisible()) {
                rl_order_summary.visibility = View.GONE
            }
        }
        var textItemCount = "0 Item"
        var textSubtotal = "0"
        var subtotal = 0.0
        var qty = 0

        if (getProductSales.isNotEmpty()) {
            subtotal = orderBill?.subTotal ?: 0.0

            for (p in getProductSales) {
                qty += p.qty
                qtyItems += p.qty
                //                    subtotal += p.subtotal
            }

            textSubtotal = NumberUtil.formatToStringWithoutDecimal(subtotal)
            textItemCount = if (qty > 1) {
                "$qty Items"
            } else {
                "$qty Item"
            }
        }

        total = subtotal
        tv_item_count.text = textItemCount
        tv_subtotal.text = NumberUtil.formatToStringWithoutDecimal(total.toString())
    }

    private fun getHeaderView(): View {
        val view: View =
            layoutInflater.inflate(R.layout.header_product, rcv, false)

        rlBookHomeVisit = view.findViewById(R.id.rl_home_visit)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvAddress = view.findViewById<TextView>(R.id.tv_address)
        val ivLocation = view.findViewById<ImageView>(R.id.iv_location)
        val tvParkingTime = view.findViewById<TextView>(R.id.tv_parking_time)
        val ivTime = view.findViewById<ImageView>(R.id.iv_time)

        val address = foodTruck?.address ?: ConstVar.EMPTY_STRING

        println("respon Name ${foodTruck?.merchantName}")
        this.tvParkingTime = tvParkingTime

        if (foodTruck?.merchantName?.isNotEmpty()!!) {
            Hawk.put("saveTitleToko", foodTruck?.merchantName)
            tvName.text = foodTruck?.merchantName
        } else {
            Hawk.put("saveTitleToko", foodTruck?.name)
            tvName.text = foodTruck?.name
        }

        tvAddress.text = address
        tvParkingTime?.text = scheduleStr

        if (address.isEmpty()) {
            tvAddress.visibility = View.INVISIBLE
            ivLocation.visibility = View.INVISIBLE
        }

        foodTruck?.let {
            when (it.types) {
                ConstVar.FOODTRUCK_TYPE_REGULAR -> {
                    tvParkingTime.visibility = View.VISIBLE
                    ivTime.visibility = View.VISIBLE
                    tvParkingTime.text = foodTruck?.schedule
                }
                else -> {
                    tvParkingTime.visibility = View.INVISIBLE
                    ivTime.visibility = View.INVISIBLE
                }
            }
        }

        rlBookHomeVisit?.visibility = View.GONE

        return view
    }

    companion object {
        private const val REQ_CODE_ORDER_VIEW = 1001

        fun getIntent(context: Context, merchantId: Long = 0, bundle: Bundle): Intent {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra(MERCHANT_ID, merchantId)
            intent.putExtras(bundle)
            return intent
        }
    }
}