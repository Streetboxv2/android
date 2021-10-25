package id.streetbox.live.ui.main.home.nearby

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.zeepos.extension.isVisible
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.ConvertToRupiah
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.showToastExt
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterMenu
import com.example.dbroom.db.room.AppDatabase
import com.example.dbroom.db.room.enitity.MenuItemStore
import com.zeepos.models.master.Tax
import com.zeepos.models.transaction.TaxSales
import com.zeepos.ui_base.ui.BaseViewEvent
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.DateTimeUtil
import id.streetbox.live.ui.menu.MenuAdapter
import id.streetbox.live.ui.menu.MenuViewEvent
import id.streetbox.live.ui.menu.MenuViewEvent.GetTaxSuccess
import id.streetbox.live.ui.menu.MenuViewModel
import id.streetbox.live.ui.pickuporder.PickupOrderActivity
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.header_product.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class NearbyDetailVisitActivity : BaseActivity<MenuViewEvent, MenuViewModel>() {
    @Inject
    lateinit var gson: Gson

    private var scheduleStr: String = ConstVar.EMPTY_STRING

    private var foodTruck: FoodTruck? = null
    private lateinit var menuAdapter: MenuAdapter
    var adapterMenu: AdapterMenu? = null

    private var merchantId: Long = 0
    private lateinit var order: Order
    private var isGetOrderSuccess: Boolean = false
    var qtyItems = 0
    var total: Double = 0.0
    var getSaveListMenu: List<MenuItemStore> = ArrayList()
    var types: String? = ""
    var typesTax:Int = 0
    var taxName:String = ConstVar.EMPTY_STRING
    var totalTax:Double = 0.0
    var isActive:Boolean =  false
    private lateinit var tax: Tax

    val onClickIncrease = object : AdapterMenu.OnClickIncrease {
        override fun ClickIncrease(position: Int, product: Product, value: Int, tvQty: TextView) {
            if (value > product.qty) {
                showToastExt("Maximal limited", this@NearbyDetailVisitActivity)
            } else {
                Hawk.put("saveAddressToko", foodTruck?.address)
                tvQty.text = value.toString()
                viewModel.addItem(product, order)
                Handler().postDelayed({
                    viewModel.calculateOrder(order)
                }, 1000)

                var disc:Double = product.discount
                var total:Double = 0.0
                var calculate:Double = 0.0
                if(disc > 0){
                    calculate = product.price - (product.price * (disc / 100))
                }else{
                    calculate = product.price
                }

                 total = value * calculate

                lifecycleScope.launch(Dispatchers.Main) {
                    addRoomItemStore(product, value, total.toLong(), calculate.toLong())
                }

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
//            val total = product.price * value
            var disc:Double = product.discount
            var total:Double = 0.0
            var calculate:Double = 0.0
            if(disc > 0){
                calculate = product.price - (product.price * (disc / 100))
            }else{
                calculate = product.price
            }

            total = value * calculate

            viewModel.removeProductSales(product, order)
            Handler().postDelayed({
                viewModel.calculateOrder(order)
            }, 1000)


            if (value == 0) {
                lifecycleScope.launch(Dispatchers.Main) {
                    val idDatabase =
                        AppDatabase.getInstance(this@NearbyDetailVisitActivity).dataDao()
                            .getItemMenutStore(product.id.toInt())
                    AppDatabase.getInstance(this@NearbyDetailVisitActivity)
                        .dataDao()
                        .deleteDataMenuStore(idDatabase)
                    getSaveListMenu = AppDatabase.getInstance(this@NearbyDetailVisitActivity)
                        .dataDao().getAllDataListMenu()
                    showHideOrderSummary()
                }

            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    addRoomItemStore(product, value, total.toLong(), calculate.toLong())
                }
            }

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
        }
    }

    private fun addRoomItemStore(product: Product, value: Int, total: Long, price: Long) {
        val idDatabase =
            AppDatabase.getInstance(this).dataDao().getItemMenutStore(product.id.toInt())
        val idMerchant =
            AppDatabase.getInstance(this).dataDao().getIdMerchantStore(merchantId.toInt())

        println("respon Id Merchant $idMerchant and ${merchantId.toInt()}")
        if (idMerchant == merchantId.toInt()) {
            if (idDatabase == product.id.toInt()) {
                val menuItemStore = MenuItemStore(
                    idDatabase,
                    merchantId.toInt(),
                    product.name,
                    value,
                    price,
                    total,
                    product.photo
                )

                AppDatabase.getInstance(this)
                    .dataDao()
                    .editDataMenutStore(menuItemStore)
            } else {
                val menuItemStore = MenuItemStore(
                    product.id.toInt(),
                    merchantId.toInt(),
                    product.name,
                    value,
                    price,
                    total,
                    product.photo
                )
                AppDatabase.getInstance(this).dataDao()
                    .addDataMenuStore(menuItemStore)
            }
        } else {
            lifecycleScope.launch {
                AppDatabase.getInstance(this@NearbyDetailVisitActivity).dataDao()
                    .deleteDataIdMerchant(merchantId.toInt())
                val menuItemStore = MenuItemStore(
                    product.id.toInt(),
                    merchantId.toInt(),
                    product.name,
                    value,
                    price,
                    total,
                    product.photo
                )
                AppDatabase.getInstance(this@NearbyDetailVisitActivity).dataDao()
                    .addDataMenuStore(menuItemStore)
                getSaveListMenu = AppDatabase.getInstance(this@NearbyDetailVisitActivity)
                    .dataDao().getAllDataListMenu()
                showHideOrderSummary()
            }
        }

        lifecycleScope.launch {
            getSaveListMenu = AppDatabase.getInstance(this@NearbyDetailVisitActivity)
                .dataDao().getAllDataListMenu()
            showHideOrderSummary()
        }
    }

    override fun initResourceLayout(): Int {
        return R.layout.activity_nearby_detail_visit
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(MenuViewModel::class.java)
        viewModel.getRecentOrder(merchantId, foodTruck)
        merchantId = intent.getLongExtra(ConstVar.MERCHANT_ID, 0)
        val bundle = intent.extras
        if (bundle != null) {
            val foodTruckStr = bundle.getString("foodTruckData", ConstVar.EMPTY_STRING)
            types = bundle.getString("types")
            if (foodTruckStr.isNotEmpty()) {
                foodTruck = gson.fromJson(foodTruckStr, FoodTruck::class.java)

            }
        }

        menuAdapter = MenuAdapter()
    }

    override fun onResume() {
        super.onResume()
        showLoading()
        getSaveListMenu = AppDatabase.getInstance(this)
            .dataDao().getAllDataListMenuNearby(merchantId.toInt())
        viewModel.getProduct("visit", merchantId)
        viewModel.getRecentOrder(merchantId, foodTruck)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initHeaderProduct()

        tv_pickup.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("foodTruckData", gson.toJson(foodTruck))
            bundle.putString("tax",gson.toJson(tax))
            bundle.putDouble("total", total)
            bundle.putInt("qty", qtyItems)
            bundle.putString("order", gson.toJson(order))
            bundle.putString("taxName", taxName)
            bundle.putInt("taxType",typesTax)
            bundle.putDouble("totalTax",totalTax)
            bundle.putBoolean("isActive",isActive)

            val intent = Intent(this, PickupOrderActivity::class.java)
                .putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onEvent(useCase: MenuViewEvent) {
        when (useCase) {
            is MenuViewEvent.GetProductSuccess -> {
                dismissLoading()
                val dataProduct = useCase.data
                initAdapterMenu(dataProduct as MutableList<Product>, getSaveListMenu)
                showHideOrderSummary()
                viewModel.getMerchantTax(merchantId)

            }
            is MenuViewEvent.GetProductFailed -> {
                dismissLoading()
                showToastExt(useCase.errorMessage, this)
            }
            is MenuViewEvent.GetTaxSuccess -> {
                tax = useCase.tax
                taxName = useCase.tax.name!!
                typesTax = useCase.tax.type
                totalTax = useCase.tax.amount
                isActive = useCase.tax.isActive
                viewModel.getRecentOrder(merchantId, foodTruck)
            }
            MenuViewEvent.GetTaxFailed -> {
                viewModel.getRecentOrder(merchantId, foodTruck)
            }
            is MenuViewEvent.GetOrCreateOrderSuccess -> {
                dismissLoading()
                isGetOrderSuccess = true
                order = useCase.order
                order.grandTotal = total
                adapterMenu?.setProDuctSalesMap(useCase.order.productSales)
                viewModel.getUserInfoCloud()


            }
            is MenuViewEvent.GetUserInfoSuccess -> {
                order.phone = useCase.user.phone!!
            }
            is MenuViewEvent.GetTaxSettingSuccess ->{

            }

        }
    }

    private fun initAdapterMenu(
        mutablistData: MutableList<Product>,
        getSaveListMenu: List<MenuItemStore>
    ) {
        adapterMenu = AdapterMenu(mutablistData, "homevisit", getSaveListMenu)
        rcv.apply {
            adapter = adapterMenu
            layoutManager = LinearLayoutManager(this@NearbyDetailVisitActivity)
            hasFixedSize()
        }
        adapterMenu!!.onClickIncrease = onClickIncrease
    }

    private fun initHeaderProduct() {
        val imageUrl: String = ConstVar.PATH_IMAGE + foodTruck?.banner

        GlideApp.with(this)
            .load(imageUrl)
            .into(iv_banner)

        tvTitleMenuHeader.text = "Menu Nearby"
        if (foodTruck?.merchantName?.isNotEmpty()!!) {
            Hawk.put("saveTitleToko", foodTruck?.merchantName)
            tv_name.text = foodTruck?.merchantName
        } else {
            Hawk.put("saveTitleToko", foodTruck?.name)
            tv_name.text = foodTruck?.name
        }

        val address = foodTruck?.address ?: ConstVar.EMPTY_STRING

        tv_address.text = address

        if (address.isEmpty()) {
            tv_address.visibility = View.INVISIBLE
            iv_location.visibility = View.INVISIBLE
        }
    }

    private fun showHideOrderSummary() {
        if (getSaveListMenu.isNotEmpty()) {
            rl_order_summary.visibility = View.VISIBLE
        } else {
            if (rl_order_summary.isVisible()) {
                rl_order_summary.visibility = View.GONE
            }
        }
        var textItemCount = "0 Item"
        var textSubtotal = "0"
        var subtotal = 0.0
        var qty = 0

        if (getSaveListMenu.isNotEmpty()) {
            for (p in getSaveListMenu) {
                qty += p.qty!!
                qtyItems += p.qty!!
                subtotal += p.total
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
        tv_subtotal.text = NumberUtil.formatToStringWithoutDecimal(total)
    }


}