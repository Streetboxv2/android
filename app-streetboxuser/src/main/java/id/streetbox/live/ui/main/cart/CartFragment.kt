package id.streetbox.live.ui.main.cart

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseFragment
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterPickupOrderNearby
import com.example.dbroom.db.room.AppDatabase
import com.example.dbroom.db.room.enitity.MenuItemStore
import com.zeepos.models.ConstVar
import com.zeepos.utilities.hideView
import com.zeepos.utilities.showToastExt
import com.zeepos.utilities.showView
import id.streetbox.live.ui.onclick.OnClickIncreaseOrderNearby
import id.streetbox.live.ui.pickuporder.PickupOrderActivity
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.toLongOrDefault
import javax.inject.Inject

/**
 * Created by Arif S. on 6/13/20
 */
class CartFragment : BaseFragment<CartViewEvent, CartViewModel>() {

    private lateinit var cartAdapter: CartAdapter
    private lateinit var order: Order
    var getSaveCart: String? = ""
    var adapterMenuChoiceOrder: AdapterPickupOrderNearby? = null
    var typesTax:Int = 0
    var taxId:Long = 0
    var taxName:String = ConstVar.EMPTY_STRING
    var totalTax:Double = 0.0
    var isActive:Boolean =  false
    var totalProduct:Double = 0.0
    var menuItemStoreList: List<MenuItemStore> = mutableListOf()


    @Inject
    lateinit var gson: Gson
    var total: Double = 0.0
    var qtyItems = 0

    override fun initResourceLayout(): Int {
        return R.layout.fragment_cart
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        menuItemStoreList = AppDatabase.getInstance(requireContext())
            .dataDao().getAllDataListMenu()
        if(menuItemStoreList.isEmpty()) {
            hideView(btn_checkout)
            hideView(llNameTokoCart)
        }
       initGetData()

    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(CartViewModel::class.java)

        menuItemStoreList = AppDatabase.getInstance(requireContext())
            .dataDao().getAllDataListMenu()



        cartAdapter = CartAdapter()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        if (menuItemStoreList.isNotEmpty())
            showSummary(menuItemStoreList)

//        swipe_refresh.setColorSchemeColors(Color.rgb(47, 223, 189))
//        swipe_refresh.isRefreshing = true
//
//        swipe_refresh.setOnRefreshListener {
//            viewModel.getCartData()
//        }
        btn_checkout.setOnClickListener {
//            context?.let {
//                startActivity(
//                    PickupOrderReviewActivity.getIntent(it, order.merchantId, 1)
//                )
//            }
            val bundle = Bundle()
            if(typesTax == 0 && isActive == true){
                order.grandTotal = total + ((totalTax/100)*total)
            }else{
                order.grandTotal = total
            }
            order.grandTotal = total
//            viewModel.updateOrder(order)
            bundle.putDouble("total", total)
            bundle.putInt("qty", qtyItems)
            bundle.putString("order", gson.toJson(order))
            bundle.putString("taxName", taxName)
            bundle.putInt("taxType",typesTax)
            bundle.putDouble("totalTax",totalTax)
            bundle.putBoolean("isActive",isActive)
            bundle.putDouble("amount",totalTax)
            bundle.putInt("taxId",taxId.toInt())


            val intent = Intent(requireContext(), PickupOrderActivity::class.java)
                .putExtras(bundle)
            startActivity(intent)
        }

//        initList()
        initGetData()
    }

    override fun onResume() {
        super.onResume()
//        swipe_refresh?.isRefreshing = true
        viewModel.getCartData()
    }

    override fun onEvent(useCase: CartViewEvent) {
        cartAdapter.data.clear()
        when (useCase) {
            is CartViewEvent.GetCartDataSuccess -> {
//                swipe_refresh.isRefreshing = false
                order = useCase.data
                if(order == null){
                    order = Order()
                }

                if (menuItemStoreList.isNotEmpty())
                    showSummary(menuItemStoreList)

                viewModel.getMerchantTax(order.merchantId)

            }
            CartViewEvent.NoDataInCart ->{} //swipe_refresh.isRefreshing = false
            is CartViewEvent.GetTaxSuccess -> {
                taxId = useCase.tax.id
                taxName = useCase.tax.name!!
                typesTax = useCase.tax.type
                totalTax = useCase.tax.amount
                isActive = useCase.tax.isActive
            }
            is CartViewEvent.GetOrCreateOrderSuccess -> {
                order = useCase.order
                viewModel.getMerchantTax(order.merchantId)
            }

        }
    }

    private fun initList() {
        rcv?.apply {
            adapter = cartAdapter
            val lm = LinearLayoutManager(context)
            layoutManager = lm
            cartAdapter.addHeaderView(addHeaderNameToko())
        }

        cartAdapter.setEmptyView(R.layout.empty_data)

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
                        showToastExt("Out Of Stock", requireContext())
                    } else {
                         val totalProducts = value * product.price

                        tvQty.text = value.toString()

                        lifecycleScope.launch(Dispatchers.Main) {
                            addRoomItemStore(product, value,product.qtyProduct!!, totalProducts, product.price)
                        }
//                        totalProduct = totalProducts.toDouble()

//                        if(isActive!! == false || typesTax == 1){
//                            order.grandTotal = totalProducts.toDouble()
//                        }else if(typesTax == 0){
//                            order.grandTotal = totalProduct.toDouble() + ((totalTax!!/100) * totalProducts.toDouble())
//                        }
                    }
                }

                override fun ClickDecrease(
                    position: Int,
                    product: MenuItemStore,
                    value: Int,
                    tvQty: TextView
                ) {
                     val totalProducts = product.price * value
                    tvQty.text = value.toString()
                    if (value == 0) {
                        adapterMenuChoiceOrder?.removeItem(product, position)
                        lifecycleScope.launch(Dispatchers.Main) {
                            val idDatabase =
                                AppDatabase.getInstance(requireContext()).dataDao()
                                    .getItemMenutStore(product.id!!)
                            AppDatabase.getInstance(requireContext())
                                .dataDao()
                                .deleteDataMenuStore(idDatabase)
                            menuItemStoreList = AppDatabase.getInstance(requireContext())
                                .dataDao().getAllDataListMenu()
                            showSummary(menuItemStoreList)
                        }
                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            addRoomItemStore(product, value, product.qtyProduct!!, totalProducts, product.price)
                        }

////                        totalProduct = totalProducts.toDouble()
//                        if(isActive!! == false || typesTax == 1){
//                            order.grandTotal = totalProduct.toDouble()
//                        }else if(typesTax == 0){
//                            order.grandTotal = totalProduct.toDouble() + ((totalTax!!/100) * totalProduct.toDouble())
//                        }

                    }

                }

            })

        rcv.apply {
            adapter = adapterMenuChoiceOrder
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun addRoomItemStore(product: MenuItemStore, value: Int,qtyProduct:Int ,total: Long, price: Long) {
        val idDatabase =
            AppDatabase.getInstance(requireContext()).dataDao().getItemMenutStore(product.id!!)
        if (idDatabase == product.id) {
            val menuItemStore = MenuItemStore(
                idDatabase,
                order.merchantId.toInt(),
                product.title,
                value,
                qtyProduct,
                price,
                total,
                product.image
            )

            AppDatabase.getInstance(requireContext())
                .dataDao()
                .editDataMenutStore(menuItemStore)
        } else {
            val menuItemStore = MenuItemStore(
                product.id,
                order.merchantId.toInt(),
                product.title,
                value,
                qtyProduct,
                price,
                total,
                product.image
            )
            AppDatabase.getInstance(requireContext()).dataDao()
                .addDataMenuStore(menuItemStore)
        }

        lifecycleScope.launch {
            menuItemStoreList = AppDatabase.getInstance(requireContext())
                .dataDao().getAllDataListMenu()
            showSummary(menuItemStoreList)
        }

    }

    fun addHeaderNameToko(): View {
        val view: View =
            layoutInflater.inflate(R.layout.layout_header_cart, rcv, false)

        val tvAddressTokoCart = view.findViewById<TextView>(R.id.tvAddressTokoCart)
        val tvTitleTokoCart = view.findViewById<TextView>(R.id.tvTitleTokoCart)

        val getNameToko = Hawk.get<String>("saveTitleToko")
        val getAddressToko = Hawk.get<String>("saveAddressToko")
        tvAddressTokoCart.text = getAddressToko
        tvTitleTokoCart.text = getNameToko
        return view
    }

    private fun showSummary(menuItemStoreList: List<MenuItemStore>) {
        var totalPrice:Double = 0.0
        menuItemStoreList.forEach {

            totalPrice += it.total + totalProduct
            qtyItems += it.qty!!
        }

        total = totalPrice

        println("respon List Menu Cart ${Gson().toJson(menuItemStoreList)}")
        if (menuItemStoreList.isNotEmpty()) {
            val getNameToko = Hawk.get<String>("saveTitleToko")
            val getAddressToko = Hawk.get<String>("saveAddressToko")
            tvAddressTokoCart.text = getAddressToko
            tvTitleTokoCart.text = getNameToko
            showView(btn_checkout)
            showView(llNameTokoCart)
        } else {
            hideView(btn_checkout)
            hideView(llNameTokoCart)
        }

    }

    companion object {
        fun newInstance(): CartFragment {
            return CartFragment()
        }
    }
}