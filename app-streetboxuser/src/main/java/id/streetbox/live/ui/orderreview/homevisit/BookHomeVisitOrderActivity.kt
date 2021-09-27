package id.streetbox.live.ui.orderreview.homevisit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.BookHomeVisit
import com.zeepos.models.entities.MenuItem
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.transaction.ProductSales
import com.zeepos.payment.PaymentActivity
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.showToastExt
import com.zeepos.utilities.showView
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterMenuChoiceOrder
import kotlinx.android.synthetic.main.activity_book_home_visit_order.*
import kotlinx.android.synthetic.main.activity_pickup_order_review.*
import kotlinx.android.synthetic.main.activity_pickup_order_review.btn_next
import kotlinx.android.synthetic.main.activity_pickup_order_review.rcv
import kotlinx.android.synthetic.main.activity_pickup_order_review.toolbar
import javax.inject.Inject

/**
 * Created by Arif S. on 8/10/20
 */
class BookHomeVisitOrderActivity :
    BaseActivity<BookHomeVisitOrderViewEvent, BookHomeVisitOrderViewModel>() {

    @Inject
    lateinit var gson: Gson

    private lateinit var bookHomeVisitOrderAdapter: BookHomeVisitOrderAdapter
    private var merchantId: Long = 0
    private lateinit var foodTruck: FoodTruck
    private lateinit var bookHomeVisit: BookHomeVisit
    private var bookedDataStr: String? = null
    private var foodTruckStr: String? = null
    val menuItemList: MutableList<MenuItem> = mutableListOf()
    var getDeposit: Double = 0.0
    var getMenuList: String = ""
    var getTotalMenuItem: Double = 0.0
    var bundle: Bundle? = null
    var menuItem: List<ProductSales> = mutableListOf()
    override fun initResourceLayout(): Int {
        return R.layout.activity_book_home_visit_order
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(BookHomeVisitOrderViewModel::class.java)

        bundle = intent.extras
        if (bundle != null) {
            foodTruckStr = bundle!!.getString("foodTruckData", ConstVar.EMPTY_STRING)
            bookedDataStr = bundle!!.getString("bookedData", ConstVar.EMPTY_STRING)

            foodTruckStr?.let {
                foodTruck = gson.fromJson(foodTruckStr, FoodTruck::class.java)
            }

            bookedDataStr?.let {
                bookHomeVisit = gson.fromJson(it, BookHomeVisit::class.java)
            }

            getDeposit = bundle!!.getDouble("deposit")
            getTotalMenuItem = bundle!!.getDouble("total")


        }

        bookHomeVisitOrderAdapter = BookHomeVisitOrderAdapter(bookHomeVisit.visitSales)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }
        initList()
        initDataMenuChoice()

        btn_next.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("foodTruckData", foodTruckStr)
            bundle.putString("bookedData", bookedDataStr)
            startActivityForResult(
                PaymentActivity.getIntent(this, bundle),
                REQ_CODE_PAYMENT
            )
        }
    }

    private fun initDataMenuChoice() {
        val getMenuList = bundle?.getString("menulist")

        menuItem = gson.fromJson(getMenuList, Array<ProductSales>::class.java).toList()

        if (menuItem.isNotEmpty()) {
            showView(tvMenuChoice)
        }

        val adapterMenuChoiceOrder = AdapterMenuChoiceOrder(
            menuItem.toMutableList(), false,
            null
        )

        rvListMenuOrder.apply {
            adapter = adapterMenuChoiceOrder
            layoutManager = LinearLayoutManager(this@BookHomeVisitOrderActivity)
            hasFixedSize()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_PAYMENT) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initList() {
        rcv?.apply {
            layoutManager = LinearLayoutManager(this@BookHomeVisitOrderActivity)
            adapter = bookHomeVisitOrderAdapter
            bookHomeVisitOrderAdapter.addHeaderView(getHeaderView())
            bookHomeVisitOrderAdapter.addFooterView(getFooterView())
        }

        bookHomeVisitOrderAdapter.setOnItemChildClickListener { adapter, view, position ->
            val productSales = adapter.getItem(position) as ProductSales
        }
    }

    private fun getHeaderView(): View {
        val view: View =
            layoutInflater.inflate(R.layout.header_book_visit_order, rcv, false)

        val ivMerchantLogo = view.findViewById<ImageView>(R.id.iv_product)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvPrice = view.findViewById<TextView>(R.id.tv_price)
        val tvNote = view.findViewById<TextView>(R.id.tv_note)
        tvPrice.visibility = View.GONE

        tvName.text = foodTruck.merchantName
        tvNote.text = bookHomeVisit.notes

        val imageUrl: String = ConstVar.PATH_IMAGE + foodTruck.logo

        Glide.with(this)
            .load(imageUrl)
            .into(ivMerchantLogo)

        return view
    }

    private fun getFooterView(): View {
        val view: View =
            layoutInflater.inflate(R.layout.footer_book_visit_order, rcv, false)

        val tvSubtotal = view.findViewById<TextView>(R.id.tv_subtotal)
        val tvTotalPayment = view.findViewById<TextView>(R.id.tv_total_payment)
        val tvDeposit = view.findViewById<TextView>(R.id.tvDepositOrder)

        tvSubtotal.text =
            NumberUtil.formatToStringWithoutDecimal(bookHomeVisit.grandTotal.toDouble())
        tvTotalPayment.text =
            NumberUtil.formatToStringWithoutDecimal(bookHomeVisit.grandTotal.toDouble())

        tvDeposit.text =
            NumberUtil.formatToStringWithoutDecimal(getDeposit)

        return view
    }

    override fun onEvent(useCase: BookHomeVisitOrderViewEvent) {
    }

    companion object {
        private const val REQ_CODE_PAYMENT = 1002

        fun getIntent(context: Context, bundle: Bundle): Intent {
            val intent = Intent(context, BookHomeVisitOrderActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }
    }
}