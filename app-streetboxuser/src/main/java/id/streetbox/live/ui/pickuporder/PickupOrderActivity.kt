package id.streetbox.live.ui.pickuporder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dbroom.db.room.AppDatabase
import com.example.dbroom.db.room.enitity.MenuItemStore
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.Tax
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.TaxSales
import com.zeepos.payment.PaymentActivity
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.*
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterPickupOrderNearby
import id.streetbox.live.ui.main.home.nearby.NearbyDetailVisitActivity
import id.streetbox.live.ui.onclick.OnClickIncreaseOrderNearby
import id.streetbox.live.ui.orderreview.pickup.PickUpOrderReviewViewModel
import id.streetbox.live.ui.orderreview.pickup.PickupOrderReviewViewEvent

import kotlinx.android.synthetic.main.activity_pickup_order.*
import kotlinx.android.synthetic.main.activity_pickup_order.toolbar
import kotlinx.android.synthetic.main.item_footer_pickup_order_review.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PickupOrderActivity : BaseActivity<PickupOrderReviewViewEvent, PickUpOrderReviewViewModel>() {

    var totalQty: Int = 0
    var deposit: Double = 0.0
    var getMenuList: String = ""
    var menuItemStoreList: List<MenuItemStore> = mutableListOf()
    var totalMenuItem: Double = 0.0
    var merchantId: String? = ""
    var foodTruck: FoodTruck? = null
    var tax: Tax?= null
    var getOrder: Order? = null
    var taxName:String = ConstVar.EMPTY_STRING
    var typeTax:Int = 0
    var totalTax:Double = 0.0
    var isActive:Boolean = false
    var adapterMenuChoiceOrder: AdapterPickupOrderNearby? = null
    var calculateTax:Double = 0.0
    var beforeTax:Double = 0.0
    var qtyProduct:Int = 0

    @Inject
    lateinit var gson: Gson


    override fun onResume() {
        super.onResume()
        init()
    }
    override fun initResourceLayout(): Int {
        return R.layout.activity_pickup_order
    }

    override fun init() {
        menuItemStoreList = AppDatabase.getInstance(this)
            .dataDao().getAllDataListMenu()
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(PickUpOrderReviewViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {


        initial()


        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initial() {
        val bundle = intent.extras
        totalMenuItem = bundle?.getDouble("total")!!
        totalQty = bundle.getInt("qty", 0)
        taxName = bundle.getString("taxName","taxName")
        typeTax = bundle.getInt("taxType",0)
        totalTax = bundle.getDouble("totalTax",0.0)
        isActive = bundle.getBoolean("isActive",false)
        qtyProduct = bundle.getInt("qtyProduct",0)
        val foodtruckBundle = bundle.getString("foodTruckData")
        val orderBundle = bundle.getString("order")
        val taxBundle = bundle.getString("tax")
        foodTruck = gson.fromJson(foodtruckBundle, FoodTruck::class.java)
        getOrder = gson.fromJson(orderBundle, Order::class.java)
        merchantId = getOrder?.merchantId.toString()
        getOrder?.grandTotal = totalMenuItem
        tax = gson.fromJson(taxBundle,Tax::class.java)

        initGetData()
        iniOnClick()
    }

    private fun iniOnClick() {
        btn_next.setOnClickListener {
            if (menuItemStoreList.isNotEmpty()) {
                val notes = et_notes.text.toString()
//            getOrder?.address = foodTruck?.address
                getOrder?.note = notes
                viewModel.updateOrder(getOrder!!)
                val intent = intentPageData(this, PaymentActivity::class.java)
                    .putExtra("foodTruckData", gson.toJson(foodTruck))
                    .putExtra("notes", notes)
                    .putExtra(PaymentActivity.ORDER_UNIQUE_ID, getOrder!!.uniqueId)
                    .putExtra("grandTotal", totalMenuItem)
                startActivityForResult(intent, 1002)
            } else {
                showToastExt("Item Order not found", this)
            }

        }

        tvAddMore.setOnClickListener {
            finish()
        }

        showView(fbl)
        tm_1.setOnClickListener {
            et_notes.setText(tm_1.text)
        }

        tm_2.setOnClickListener {
            et_notes.setText(tm_2.text)
        }

        tm_3.setOnClickListener {
            et_notes.setText(tm_3.text)
        }

        tm_4.setOnClickListener {
            et_notes.setText(tm_4.text)
        }

        tm_5.setOnClickListener {
            et_notes.setText(tm_5.text)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1002) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initGetData() {
        adapterMenuChoiceOrder = AdapterPickupOrderNearby(
            menuItemStoreList as MutableList<MenuItemStore>,
            true, object : OnClickIncreaseOrderNearby {
                override fun ClickIncrease(
                    position: Int,
                    product: MenuItemStore,
                    value: Int,
                    tvQty: TextView
                ) {

                    if (value > product.qtyProduct!!) {
                        showToastExt("Out of stock", this@PickupOrderActivity)
                    } else {
                        Hawk.put("saveAddressToko", foodTruck?.address)
                        val totalProduct = value * product.price
                        tvQty.text = value.toString()

                        lifecycleScope.launch(Dispatchers.Main) {
                            addRoomItemStore(product, value,product.qtyProduct!!, totalProduct, product.price)
                        }
                    }
                }

                override fun ClickDecrease(
                    position: Int,
                    product: MenuItemStore,
                    value: Int,
                    tvQty: TextView
                ) {
                    Hawk.put("saveAddressToko", foodTruck?.address)
                    val totalProduct = product.price * value
                    if (value == 0) {
                        tvQty.text = "0"
                        adapterMenuChoiceOrder?.removeItem(product, position)
                        lifecycleScope.launch(Dispatchers.Main) {
                            val idDatabase =
                                AppDatabase.getInstance(this@PickupOrderActivity).dataDao()
                                    .getItemMenutStore(product.id!!)
                            AppDatabase.getInstance(this@PickupOrderActivity)
                                .dataDao()
                                .deleteDataMenuStore(idDatabase)
                            menuItemStoreList = AppDatabase.getInstance(this@PickupOrderActivity)
                                .dataDao().getAllDataListMenu()
                            showSummary(menuItemStoreList)
                        }

                    } else {
                        tvQty.text = value.toString()
                        lifecycleScope.launch(Dispatchers.Main) {
                            addRoomItemStore(product, value,product.qtyProduct!!, totalProduct, product.price)
                        }
                    }

                }

            })

        rvListMenuPickupOrder.apply {
            adapter = adapterMenuChoiceOrder
            layoutManager = LinearLayoutManager(this@PickupOrderActivity)
            hasFixedSize()
        }

        if(isActive == false){
            tv_tax_label.visibility = View.GONE
            tv_total_tax.visibility = View.GONE
        }else{
            tv_tax_label.visibility = View.VISIBLE
            tv_total_tax.visibility = View.VISIBLE
        }

        calculateTax = (totalTax/100) * totalMenuItem
        tv_subtotal.text =
            NumberUtil.formatToStringWithoutDecimal(totalMenuItem)

        if(typeTax == 0 && isActive == true){
            totalMenuItem = totalMenuItem + calculateTax
            taxName = taxName+"(Excl)"

        }else if(typeTax == 1 && isActive == true){
            taxName = taxName+"(Incl)"
        }

        tv_total_tax.text =  NumberUtil.formatToStringWithoutDecimal(calculateTax)
        tv_tax_label.text = taxName

        tv_total_payment.text =
            NumberUtil.formatToStringWithoutDecimal(totalMenuItem)
    }

    private fun showSummary(menuItemStoreList: List<MenuItemStore>) {
        var total = 0.0
        var type = 0
        var taxName = taxName
        menuItemStoreList.forEach {
            total += it.total

        }
        val totalTaxs:Double = (totalTax/100) * total
        if(isActive == false){
            tv_tax_label.visibility = View.GONE
            tv_total_tax.visibility = View.GONE
        }else{
            tv_tax_label.visibility = View.VISIBLE
            tv_total_tax.visibility = View.VISIBLE
        }
        if(typeTax == 0 && isActive== true){
            total + totalTaxs
        }

        tv_tax_label.text = taxName
        tv_total_tax.text =  ConvertToRupiah.toRupiah("", totalTaxs.toString(), false)
            tv_subtotal.text =
            ConvertToRupiah.toRupiah("", total.toString(), false)
        tv_total_payment.text =
            ConvertToRupiah.toRupiah("", total.toString(), false)
    }

    override fun onEvent(useCase: PickupOrderReviewViewEvent) {
        when (useCase) {
            PickupOrderReviewViewEvent.UpdateOrderSuccess -> {

            }
        }
    }

    private fun addRoomItemStore(product: MenuItemStore, value: Int, qtyProduct:Int,total: Long, price: Long) {
        val idDatabase =
            AppDatabase.getInstance(this).dataDao().getItemMenutStore(product.id!!)
        val idMerchant =
            AppDatabase.getInstance(this).dataDao()
                .getIdMerchantStore(merchantId?.toInt()!!)

        if (idDatabase == product.id) {
            val menuItemStore = MenuItemStore(
                idDatabase,
                merchantId!!.toInt(),
                product.title,
                value,
                qtyProduct,
                price,
                total,
                product.title
            )

            AppDatabase.getInstance(this)
                .dataDao()
                .editDataMenutStore(menuItemStore)
        } else {
            val menuItemStore = MenuItemStore(
                product.id,
                merchantId!!.toInt(),
                product.title,
                value,
                qtyProduct,
                price,
                total,
                product.image
            )
            AppDatabase.getInstance(this).dataDao()
                .addDataMenuStore(menuItemStore)
        }

        lifecycleScope.launch {
            menuItemStoreList = AppDatabase.getInstance(this@PickupOrderActivity)
                .dataDao().getAllDataListMenu()
            showSummary(menuItemStoreList)
        }
    }

}