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
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.Product
import com.zeepos.models.master.Tax
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
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
import java.util.*
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
    var merchantIds:Long = 0
    var subtotal : Double = 0.0
    var subtotalItem : Double = 0.0
    var total: Double = 0.0
    var totalProducts:Product? = null
    var orderBill:OrderBill? = null
    var productSales:ProductSales? = null
    var getProduct: List<Product> = ArrayList()
    var totalTaxs:Double = 0.0
    var merchantUserId:Long = 0
    var taxId:Long = 0
    var amount:Double = 0.0


    @Inject
    lateinit var gson: Gson


    override fun onResume() {
        super.onResume()
        viewModel.getRecentOrder(getOrder!!.merchantId)
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

        orderBill = OrderBill()

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
        amount = bundle.getDouble("amount", 0.0)
        var tax: Int = 0
        tax = bundle.getInt("taxId",0)
        taxId = tax.toLong()

        val foodtruckBundle = bundle.getString("foodTruckData")
        val orderBundle = bundle.getString("order")
        val taxBundle = bundle.getString("tax")
        val products = bundle.getString("menuList")
//        productSales = gson.fromJson(products,ProductSales::class.java)
        foodTruck = gson.fromJson(foodtruckBundle, FoodTruck::class.java)
        getOrder = gson.fromJson(orderBundle, Order::class.java)
        merchantUserId = getOrder!!.merchantUsersId
        merchantId = getOrder?.merchantId.toString()


        initGetData()
        iniOnClick()
    }

    private fun iniOnClick() {
        btn_next.setOnClickListener {
            if (menuItemStoreList.isNotEmpty()) {
                val notes = et_notes.text.toString()
                getOrder?.address = foodTruck?.address
                getOrder?.note = notes

                if(total > 0){
                    getOrder!!.grandTotal = subtotal
                    total = subtotal
                }else{
                    getOrder!!.grandTotal = subtotalItem
                    total = subtotalItem
                }

                getOrder!!.merchantUsersId = merchantUserId
                getOrder!!.typeOrder = "Online"
                getOrder!!.types = 1
                menuItemStoreList.forEach {
                    var productSales:ProductSales = ProductSales()

                        productSales!!.qty = it.qty!!
                        productSales!!.name = it.title!!
                        productSales.orderUniqueId = getOrder!!.uniqueId
                        productSales.orderBillUniqueId = getOrder!!.uniqueId
                        productSales.businessDate = DateTimeUtil.getCurrentDateWithoutTime()
                        productSales!!.qtyProduct = it.qtyProduct!!
                        productSales.uniqueId = ObjectFactory.generateGUID()
                        productSales.createdAt = DateTimeUtil.getCurrentDateTime()
                        productSales.updatedAt = DateTimeUtil.getCurrentDateTime()
                        productSales.photo = it.image!!
                        productSales.price = it.price.toDouble()
                        productSales.productId = it.id!!.toLong()
                        getOrder!!.productSales.add(productSales)

                }


                val orderBill = OrderBill()
                orderBill.uniqueId = ObjectFactory.generateGUID()
                orderBill.orderUniqueId = getOrder!!.uniqueId
//        orderBill.businessDate = order.businessDate
                orderBill.billNo = getOrder!!.billNo
                orderBill.businessDate = DateTimeUtil.getCurrentDateWithoutTime()
                orderBill.createdAt = DateTimeUtil.getCurrentDateTime()
                orderBill.updatedAt = DateTimeUtil.getCurrentDateTime()
                orderBill.totalTax = totalTaxs
                getOrder!!.orderBill.add(orderBill)


                val taxSales = TaxSales()
                taxSales.uniqueId = ObjectFactory.generateGUID()
                taxSales.merchantId = getOrder!!.merchantId
                taxSales.merchantTaxId = taxId
                taxSales.name = taxName
                taxSales.amount = amount
                taxSales.orderUniqueId = getOrder!!.uniqueId
                taxSales.type = typeTax
                taxSales.isActive = isActive
                taxSales.createdAt = DateTimeUtil.getCurrentDateTime()
                taxSales.updatedAt = DateTimeUtil.getCurrentDateTime()
                getOrder!!.taxSales.add(taxSales)

                viewModel.updateOrder(getOrder!!)



//                val intent = intentPageData(this, PaymentActivity::class.java)
//                    .putExtra("foodTruckData", gson.toJson(foodTruck))
//                    .putExtra("notes", notes)
//                    .putExtra(PaymentActivity.ORDER_UNIQUE_ID, getOrder!!.uniqueId)
//                    .putExtra("grandTotal", total)
//                    .putExtra("totalTax", totalTax)
//                    .putExtra("isActive", isActive)
//                    .putExtra("typeTax",typeTax)
//                startActivityForResult(intent, 1002)
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
                        total = totalProduct.toDouble()
//                        totalProducts!!.qty = value
                        lifecycleScope.launch(Dispatchers.Main) {
                            addRoomItemStore(product, value,product.qtyProduct!!, totalProduct, product.price)
                        }

                        if(isActive!! == false || typeTax == 1){
                            getOrder!!.grandTotal = totalProduct.toDouble()
                        }else if(typeTax == 0){
                            getOrder!!.grandTotal = totalProduct.toDouble() + ((totalTax/100) * totalProduct.toDouble())
                            total =  getOrder!!.grandTotal
                            subtotalItem = totalProduct.toDouble()
                        }
                        menuItemStoreList = AppDatabase.getInstance(this@PickupOrderActivity)
                            .dataDao().getAllDataListMenu()
                        showSummary(menuItemStoreList)

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
                        total = totalProduct.toDouble()
//                        totalProducts!!.qty = value

                        lifecycleScope.launch(Dispatchers.Main) {
                            addRoomItemStore(product, value,product.qtyProduct!!, totalProduct, product.price)
                        }

                        if(isActive!! == false || typeTax == 1){
                            getOrder!!.grandTotal = totalProduct.toDouble()
                        }else if(typeTax == 0){
                            getOrder!!.grandTotal = totalProduct.toDouble() + ((totalTax/100) * totalProduct.toDouble())
                            subtotalItem = totalProduct.toDouble()
                            total = getOrder!!.grandTotal

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


            subtotalItem = totalMenuItem
        if(typeTax == 0 && isActive == true){

            totalMenuItem = totalMenuItem + calculateTax
            taxName = taxName+"(Excl)"

        }else if(typeTax == 1 && isActive == true){
            taxName = taxName+"(Incl)"
        }


        tv_subtotal.text =
            NumberUtil.formatToStringWithoutDecimal(subtotalItem)
        tv_total_tax.text =  NumberUtil.formatToStringWithoutDecimal(calculateTax)
        tv_tax_label.text = taxName

        tv_total_payment.text =
            NumberUtil.formatToStringWithoutDecimal(totalMenuItem)
    }

    private fun showSummary(menuItemStoreList: List<MenuItemStore>) {
        var totalProduct = 0.0
        var type = 0
        var taxName = taxName
        menuItemStoreList.forEach {
            totalProduct += it.total

        }

        total = totalProduct
        subtotal = totalProduct
         totalTaxs = (totalTax/100) * total
        if(isActive == false){
            tv_tax_label.visibility = View.GONE
            tv_total_tax.visibility = View.GONE
        }else{
            tv_tax_label.visibility = View.VISIBLE
            tv_total_tax.visibility = View.VISIBLE
        }
        if(typeTax == 0 && isActive== true){
            total = total + totalTaxs
        }

        tv_tax_label.text = taxName
        tv_total_tax.text =  ConvertToRupiah.toRupiah("", totalTaxs.toString(), false)
            tv_subtotal.text =
            ConvertToRupiah.toRupiah("", subtotal.toString(), false)
        tv_total_payment.text =
            ConvertToRupiah.toRupiah("", total.toString(), false)

        orderBill!!.totalTax = totalTaxs

    }

    override fun onEvent(useCase: PickupOrderReviewViewEvent) {
        when (useCase) {
            PickupOrderReviewViewEvent.UpdateOrderSuccess -> {
                getOrder?.note = et_notes.toString()
                val intent = intentPageData(this, PaymentActivity::class.java)
                    .putExtra("foodTruckData", gson.toJson(foodTruck))
                    .putExtra("notes", et_notes.toString())
                    .putExtra(PaymentActivity.ORDER_UNIQUE_ID, getOrder!!.uniqueId)
                    .putExtra("grandTotal", total)
                    .putExtra("totalTax", totalTax)
                    .putExtra("isActive", isActive)
                    .putExtra("typeTax",typeTax)
                startActivityForResult(intent, 1002)
            }

            is PickupOrderReviewViewEvent.OnCalculateDone -> {
               getOrder = useCase.orderBill.order.target
                viewModel.updateOrder(getOrder!!)

            }
            is PickupOrderReviewViewEvent.AddItemSuccess ->{
                viewModel.calculateOrder(getOrder!!)
            }
            PickupOrderReviewViewEvent.OnRemoveProductSuccess -> {

                viewModel.calculateOrder(getOrder!!)
            }

            is PickupOrderReviewViewEvent.GetOrderSuccess -> {
                getOrder = useCase.order
                viewModel.getProduct("visit",getOrder!!.merchantId)
            }
            PickupOrderReviewViewEvent.UpdateProductSalesSuccess -> {
                viewModel.calculateOrder(getOrder!!)
//                viewModel.updateOrder(getOrder!!)
//                getOrder?.note = et_notes.toString()
//                val intent = intentPageData(this, PaymentActivity::class.java)
//                    .putExtra("foodTruckData", gson.toJson(foodTruck))
//                    .putExtra("notes", et_notes.toString())
//                    .putExtra(PaymentActivity.ORDER_UNIQUE_ID, getOrder!!.uniqueId)
//                    .putExtra("grandTotal", total)
//                    .putExtra("totalTax", totalTax)
//                    .putExtra("isActive", isActive)
//                    .putExtra("typeTax",typeTax)
//                startActivityForResult(intent, 1002)
            }
            PickupOrderReviewViewEvent.UpdateOrderBillSuccess -> {
                viewModel.updateOrder(getOrder!!)

            }

            is PickupOrderReviewViewEvent.GetProductSuccess ->{
                getProduct = useCase.data

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