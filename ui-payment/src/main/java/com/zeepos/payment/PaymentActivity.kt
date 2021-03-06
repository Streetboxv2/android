package com.zeepos.payment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log.println
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dbroom.db.room.AppDatabase
import com.example.dbroom.db.room.enitity.MenuItemStore
import com.google.gson.Gson
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.BookHomeVisit
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.PaymentMethod
import com.zeepos.models.master.Tax
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.models.transaction.TaxSales
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.PermissionUtils
import com.zeepos.utilities.SharedPreferenceUtil
import kotlinx.android.synthetic.main.activity_payment.*
import javax.inject.Inject

/**
 * Created by Arif S. on 5/18/20
 */
class PaymentActivity : BaseActivity<PaymentViewEvent, PaymentViewModel>() {

    @Inject
    lateinit var paymentUiEvent: PaymentUiEvent

    @Inject
    lateinit var gson: Gson

    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var orderTrx: Order
    private var foodTruck: FoodTruck? = null
    private var bookHomeVisit: BookHomeVisit? = null
    var grandTotal: Double = 0.0
    var totalTax: Double = 0.0
    var isActive:Boolean = false
    var typeTax:Int = 0
    var menuItemStoreList: List<MenuItemStore> = mutableListOf()

    private val appType: String by lazy {
        SharedPreferenceUtil.getString(this, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
            ?: ConstVar.EMPTY_STRING
    }

    override fun initResourceLayout(): Int {
        return R.layout.activity_payment
    }

    override fun init() {
        val bundle = intent.extras
        viewModel = ViewModelProvider(this, viewModeFactory).get(PaymentViewModel::class.java)
        grandTotal = intent.getDoubleExtra("grandTotal", 0.0)
        totalTax = intent.getDoubleExtra("totalTax",0.0)
        isActive = intent.getBooleanExtra("isActive", false)
        typeTax = intent.getIntExtra("typeTax",0)
        println("respon Grand total $grandTotal")

        val foodTruckStr = bundle?.getString("foodTruckData", ConstVar.EMPTY_STRING)
        foodTruck = gson.fromJson(foodTruckStr, FoodTruck::class.java)
        val bookedDataStr = bundle?.getString("bookedData", ConstVar.EMPTY_STRING)

        if (bookedDataStr != null)
            bookHomeVisit = gson.fromJson(bookedDataStr, BookHomeVisit::class.java)

        val orderUniqueId = intent.getStringExtra(ORDER_UNIQUE_ID)
        paymentAdapter = PaymentAdapter()

        if (orderUniqueId != null) {
            showLoading()
            viewModel.getOrderByUniqueId(orderUniqueId)
        } else { //home visit payment
            if (bundle != null) {
                showLoading()
                viewModel.getPaymentMethod()
            }
        }

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        initList()

        menuItemStoreList = AppDatabase.getInstance(this)
            .dataDao().getAllDataListMenu()

        var productSales = ProductSales()


        btn_pay.setOnClickListener {
            val selectedPayment = paymentAdapter.selectedPayment
            if (selectedPayment != null) {
                showLoading()
                if (appType == ConstVar.APP_CUSTOMER) {
                      if (bookHomeVisit != null) {//home visit
                        val order = Order()//fake order not used in home visit
                        order.uniqueId = "123qwer"
                        order.address = foodTruck?.address
                        val idMerchant = foodTruck?.merchantId

                        bookHomeVisit!!.paymentMethodId = selectedPayment.id
                          val orderJson =  gson.toJson(order)
                        viewModel.getQRCodePayment(
                            idMerchant!!,
                            bookHomeVisit!!.grandTotal.toDouble(),
                            ConstVar.TRANSACTION_TYPE_VISIT,
                            order,
                            orderJson
                        )
                    } else {

                        //from nearby


                          viewModel.generatePaymentSales(
                            selectedPayment,
                            ConstVar.PAYMENT_STATUS_UNPAID,
                            orderTrx
                        )
//                        val idMerchant = foodTruck?.merchantId
//                            idMerchant!!,
//                            grandTotal.toInt(),
//                            foodTruck?.address.toString(),
//                            ConstVar.TRANSACTION_TYPE_ORDER
//                        )
                    }
                } else {

                    //for POS always paid when order close
                    viewModel.generatePaymentSales(
                        selectedPayment,
                        ConstVar.PAYMENT_STATUS_PAID,
                        orderTrx
                    )
                }
            } else {
                Toast.makeText(this, "Please select payment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initList() {
        rcv.apply {
            layoutManager = LinearLayoutManager(this@PaymentActivity)
            adapter = paymentAdapter
        }

        paymentAdapter.setOnItemClickListener { adapter, _, position ->

            val paymentMethod = adapter.getItem(position) as PaymentMethod
            paymentAdapter.selectedPayment = paymentMethod
            paymentAdapter.notifyDataSetChanged()

        }
    }

    override fun onEvent(useCase: PaymentViewEvent) = when (useCase) {
        is PaymentViewEvent.GetPaymentMethodSuccess -> {
            paymentAdapter.setList(useCase.data)
            dismissLoading()
        }
        is PaymentViewEvent.GetPaymentMethodFailed -> {
            Toast.makeText(applicationContext, useCase.errorMesge, Toast.LENGTH_SHORT).show()
            dismissLoading()
        }
        is PaymentViewEvent.GetQRCodePaymentSuccess -> {
            dismissLoading()
            val qrContent = useCase.data.qrCode
            qrContent?.let {
                showDialog(QRDialog.getInstance(it))
            }
            if (bookHomeVisit != null) {
                bookHomeVisit?.let {
                    it.uniqueId = ObjectFactory.generateGUID()
                    it.trxId = useCase.data.trxId!!
                    paymentUiEvent.onHomeVisitPaymentFinish(
                        this,
                        it.uniqueId,
                        gson.toJson(it)
                    )//sync visit data transaction
                }
            } else {

                paymentUiEvent.onPaymentFinish(this, orderTrx.uniqueId)//sync data transaction
            }
            AppDatabase.getInstance(applicationContext)
                .dataDao().deleteAllMenuStore()

        }
        is PaymentViewEvent.GetQRCodePaymentFailed -> {
            dismissLoading()
            Toast.makeText(applicationContext, useCase.errorMessage, Toast.LENGTH_SHORT).show()
        }
        is PaymentViewEvent.GetOrderSuccess -> {
            orderTrx = useCase.order
            viewModel.getPaymentMethod()
        }
        is PaymentViewEvent.GetOrderFailed -> {
            dismissLoading()
            println("respon Error order ${useCase.throwable.message}")
        }
        PaymentViewEvent.CloseOrderSuccess -> {
            val merchanId = orderTrx.merchantId
            val orderJson =  gson.toJson(orderTrx.productSales)
            if(isActive== true && typeTax == 0){
                grandTotal = grandTotal + ((totalTax/100) * grandTotal)
                orderTrx.grandTotal = grandTotal
            }
            viewModel.getQRCodePayment(
                merchanId,
                grandTotal,
                ConstVar.TRANSACTION_TYPE_ORDER,
                orderTrx,
                orderJson
            )
        }
    }

    override fun onStart() {
        super.onStart()
        PermissionUtils.isReadWriteStoragePermissionGranted(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtils.RC_READ_WRITE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    PermissionUtils.isReadWriteStoragePermissionGranted(this)
                    Toast.makeText(
                        this,
                        "Storage access not granted!!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        const val ORDER_UNIQUE_ID = "uniqueId"

        fun getIntent(context: Context, uniqueId: String, grandTotal: Double): Intent {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(ORDER_UNIQUE_ID, uniqueId)
            intent.putExtra("grandTotal", grandTotal)
            return intent
        }

        fun getIntent(context: Context, bundle: Bundle): Intent {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }
    }
}