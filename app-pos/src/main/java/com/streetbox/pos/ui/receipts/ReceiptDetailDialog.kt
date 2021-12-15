package com.streetbox.pos.ui.receipts

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.streetbox.pos.R
import com.streetbox.pos.async.AsyncBluetoothEscPosPrint
import com.streetbox.pos.async.AsyncEscPosPrinter
import com.streetbox.pos.ui.main.MainActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import com.zeepos.models.master.Tax
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.PaymentSales
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseDialogFragment
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.dialog_receipt_detail.*
import kotlinx.android.synthetic.main.footer_receipt_detail.*
import kotlinx.android.synthetic.main.fragment_checkout_detail.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Arif S. on 10/13/20
 */
class  ReceiptDetailDialog : BaseDialogFragment() {
    @Inject
    lateinit var gson: Gson

    private lateinit var headerView: View
    private lateinit var footerView: View
    private var viewModel: ReceiptViewModel? = null
    private lateinit var adapter: ReceiptDetailAdapter
    private lateinit var order: Order
    private lateinit var tax: Tax
    private var printing: Printing? = null
    private var user: User? = null
    private var theBitmap: Bitmap? = null

    private var userOperator: User? = null

    private var qrCode: String = ConstVar.EMPTY_STRING
    private var menu:String = ""
    private var menuDisc:String = ""
    private var subTot: String=""
    private var grandTot: String=""
    private var taxTotal:String=""
    private var pay:String =""
    private var changeM:String =""
    private var qrCodeM:String = ""
    private var header:String=""
    private var type: Int = 0
    private var taxType:Int = 0
    private var taxName:String = ConstVar.EMPTY_STRING
    private var isActive:Boolean = false
    private var trxId:String = ""
    private var billNo:String = ConstVar.EMPTY_STRING
    private var typePayment:String = ConstVar.EMPTY_STRING
    var calculate:Double = 0.0
    var productMenu:List<ProductSales> = ArrayList()
    var orderBillMenu:List<OrderBill> = ArrayList()
    var createdAt:Long = 0
    var dateCreated:Long = 0
    var typeOrder:String = ConstVar.EMPTY_STRING
    var grandTotal:Double = 0.0
    var orderNo:String = ""
    var businessDate:Long = 0

    private lateinit var productSales: ProductSales




    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/
    val PERMISSION_BLUETOOTH = 1
    private var selectedDevice: BluetoothConnection? = null



    override fun initResourceLayout(): Int {
        return R.layout.dialog_receipt_detail
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        activity?.let {
            viewModel = ViewModelProvider(it).get(ReceiptViewModel::class.java)

        }

        adapter = ReceiptDetailAdapter()
        initList()


        val bundle = arguments

        if (bundle != null) {
            val orderUniqueId = bundle.getString("orderUniqueId") ?: ""
            taxName = bundle.getString("taxName","")
            taxType = bundle.getInt("taxType",0)
            trxId = bundle.getString("trxId",trxId)
            isActive = bundle.getBoolean("isActive",false)
            billNo = bundle.getString("billNo",ConstVar.EMPTY_STRING)
            typePayment = bundle.getString("typePayment",ConstVar.EMPTY_STRING)
            typeOrder = bundle.getString("typeOrder",ConstVar.EMPTY_STRING)
            createdAt = bundle.getLong("createdAt",0)
            dateCreated = bundle.getLong("dateCreated",0)
            grandTotal = bundle.getDouble("grandTotal",0.0)
            orderNo = bundle.getString("noAntrian",ConstVar.EMPTY_STRING)
            businessDate = bundle.getLong("businessDate",0)
            gson = Gson()
            val orderList = bundle.getString("order",ConstVar.EMPTY_STRING)
             val productList = bundle.getString("productSales")
             val orderBill = bundle.getString("orderBill")
            isActive = bundle.getBoolean("isActive",isActive)

             productMenu = gson.fromJson(productList, Array<ProductSales>::class.java).toList()
             orderBillMenu = gson.fromJson(orderBill,Array<OrderBill>::class.java).toList()

//            if (orderUniqueId.isNotEmpty()) {
//                viewModel?.getOrder(orderUniqueId)
//            }
        }

        user = viewModel!!.getProfileMerchantLocal()

        userOperator = viewModel!!.getOperator()

        setData()



//        checkPrinter()

        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (user?.logo != null) user?.logo else ConstVar.EMPTY_STRING

        GlideApp.with(this)
            .asBitmap()
            .load(imageUrl)
            .override(200, 200)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    if (user?.logo != null) {
                        theBitmap = resource
                    }
                }

            })

        btn_print.setOnClickListener {
//            printSomePrintable()
            if(typeOrder.equals("Online")){
                createdAt = dateCreated
            }
            formatReceipt()
            printBluetooth()
            Handler().postDelayed({
               dismiss()
            }, 3000)



        }

        btn_void.setOnClickListener {
            viewModel!!.voidOrder(trxId)
            Handler().postDelayed({
                startActivity(context?.let { it1 -> ReceiptActivity.getIntent(it1) })
            }, 1000)


        }

    }

    private fun setData() {
        if(typeOrder.equals("Online")){
            createdAt = dateCreated
        }
        if (trxId.isNotEmpty()) {
            tv_no_trx?.text = "No Transaksi #${trxId}"
        } else {
            tv_no_trx?.text = "No Transaksi #${billNo}"
        }

//        Log.d(ConstVar.TAG, order?.orderBill[0]?.billNo)
//        Log.d(ConstVar.TAG, order?.trx[0]?.trxId)
        tv_status_label?.text = "Status: ${orderBillMenu[0]?.billNo}"

        tv_grand_total?.text = "${NumberUtil.formatToStringWithoutDecimal(grandTotal)}"
        productMenu[0]!!.subtotal = grandTotal
//        if(order!=null) {
            adapter.setList(productMenu)
            setFooterData()
//        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes

        if (activity != null)
            params?.width = Utils.getScreenWidth(activity!!) / 2
        else
            params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = Utils.getScreenHeight(activity!!) - 200

        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun initList() {
        rcv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ReceiptDetailDialog.adapter
            this@ReceiptDetailDialog.adapter.addHeaderView(getHeaderView())
            this@ReceiptDetailDialog.adapter.addFooterView(getFooterView())
        }

    }

    private fun getHeaderView(): View {
        headerView =
            layoutInflater.inflate(R.layout.header_receipt_detail, rcv, false)

        return headerView
    }

    private fun getFooterView(): View {
        footerView =
            layoutInflater.inflate(R.layout.footer_receipt_detail, rcv, false)

        return footerView
    }

    private fun setFooterData() {
        val tvSubtotal = footerView.findViewById<TextView>(R.id.tv_subtotals)
        val tvTotalTax = footerView.findViewById<TextView>(R.id.tv_total_tax)
        val tvTaxLabel = footerView.findViewById<TextView>(R.id.tv_tax_label)
        val tvTotalPayment = footerView.findViewById<TextView>(R.id.tv_total_payment)
        val tvPaymentName = footerView.findViewById<TextView>(R.id.tv_payment_name)
        val tvTrxDateLabel = footerView.findViewById<TextView>(R.id.tv_trx_date_label)
        val ivQR = footerView.findViewById<ImageView>(R.id.iv_qr)



        val totalTax: Double = orderBillMenu[0]?.totalTax ?: 0.0
        val subTotal: Double = grandTotal ?: 0.0
        var totalPayment: Double = grandTotal ?: 0.0

        val taxTypeDisplay =
            if (taxType == ConstVar.TAX_TYPE_EXCLUSIVE) ConstVar.TAX_TYPE_EXCLUSIVE_DISPLAY else ConstVar.TAX_TYPE_INCLUSIVE_DISPLAY
        val paymentMethod = ( typePayment ?: "-") + " - " + typeOrder
        if(typeOrder.equals("Online")){
            createdAt = dateCreated

        }

       calculate = orderBillMenu[0].totalTax
        val a:Double = orderBillMenu[0].subTotal
        if(isActive == true) {
            if (taxType == 0) {
                tvTotalTax.text = "${NumberUtil.formatToStringWithoutDecimal(calculate)}"
                tvTaxLabel.text = taxName + "(Excl)"
                tvSubtotal.text = "${NumberUtil.formatToStringWithoutDecimal(subTotal - calculate )}"
                tvTotalPayment.text = "${NumberUtil.formatToStringWithoutDecimal(totalPayment)}"
            } else if (taxType  == 1) {
                tvTaxLabel.text = taxName + "(Incl)"
                tvSubtotal.text = "${NumberUtil.formatToStringWithoutDecimal(subTotal)}"
                tvTotalTax.text = "${NumberUtil.formatToStringWithoutDecimal(calculate)}"
                tvTotalPayment.text = "${NumberUtil.formatToStringWithoutDecimal(totalPayment )}"
            }
        }
        else {
            tvSubtotal.text = "${NumberUtil.formatToStringWithoutDecimal(subTotal )}"
            tvTotalPayment.text = "${NumberUtil.formatToStringWithoutDecimal(totalPayment )}"
        }




        tvPaymentName.text = "$paymentMethod"
//        tvTaxLabel.text = "${taxSales?.name} ($taxTypeDisplay)"


        tvTrxDateLabel.text = "${
            DateTimeUtil.getDateWithFormat(
                businessDate,
                "dd/MM/YYYY"
            )
        } ${DateTimeUtil.getLocalDateWithFormat(createdAt, "HH:mm")}"

/*r
        if (totalTax <= 0) {
            tvTotalTax.visibility = View.INVISIBLE
            tvTaxLabel.visibility = View.INVISIBLE
        }b
*/

        if (typePayment == "QRIS") {
            var qrCode = "";
            for (row in productMenu) {
                if (row.qrCode != "") {
                    qrCode = row.qrCode
                }
            }

            if (qrCode.isNotEmpty()) {
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix =
                    multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE, 300, 300)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
                ivQR?.setImageBitmap(bitmap)
            }
        }else {
            ivQR.visibility = View.GONE
            if (typePayment.equals("CASH")) {
                btn_void.visibility = View.VISIBLE
            }
        }
    }




    fun formatReceipt(){
        for (i in productMenu.indices) {

            header = "[L]QTY ITEM[R]PRICE\n"
            var productName = productMenu[i].name
            if (productName.length > 15) {
                productName = productMenu[i].name.substring(0,15) + "..."
            }
            val disc = productMenu[i].discount
            val discPrice =
                (productMenu[i].priceOriginal * productMenu[i].discount / 100)
            if(disc > 0) {
                menuDisc += "[L]"+ productMenu[i].qty+"   "+ productName + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    productMenu[i].price
                ) + "\n" +"[L]     discount " + NumberUtil.formatToStringWithoutDecimal(
                    discPrice)+"\n"

            }else{
                menu += "[L]" +productMenu[i].qty +" "+ productName  + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    productMenu[i].price
                ) + "\n"
            }
        }


        val grandTotal = grandTotal
        calculate = orderBillMenu[0].totalTax
        if( isActive == false){
            subTot =  "[L]<b>Subtotal </b>" +"[R]"+ NumberUtil.formatToStringWithoutDecimal(grandTotal) + "\n"
            grandTot = "[L]<b>Grand Total </b>" + "[R]" +  NumberUtil.formatToStringWithoutDecimal(
                grandTotal)+"\n"

        }else if( isActive == true){


            if (taxType == 1) {
                taxTotal =
                    "[L]<b>" + taxName+ "(Incl)"+ "</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        calculate
                    ) + "\n"
                grandTot =
                    "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                       grandTotal
                    ) + "\n"
                subTot =  "[L]<b>Subtotal </b>" +"[R]"+ NumberUtil.formatToStringWithoutDecimal(grandTotal) + "\n"

            }else if (taxType == 0){
                taxTotal =
                    "[L]<b>" + taxName +"(Excl)"+ "</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        calculate
                    ) + "\n"
                subTot =  "[L]<b>Subtotal </b>" +"[R]"+ NumberUtil.formatToStringWithoutDecimal((grandTotal - calculate) ) + "\n"
                grandTot =
                    "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        grandTotal
                    ) + "\n"
            }


        }


    }


    fun printBluetooth() {
        if (ContextCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!.applicationContext as Activity,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else {
            try {
                val bluetoothDevicesList: Array<BluetoothConnection> =
                    BluetoothPrintersConnections().getList()!!
                selectedDevice = bluetoothDevicesList[0]
                AsyncBluetoothEscPosPrint(context).execute(getAsyncEscPosPrinter(selectedDevice))
//                getAsyncEscPosPrinter(selectedDevice)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {


        val productSales = productMenu
        val username =
            userOperator!!.userName!!.substring(0, userOperator!!.userName!!.indexOf("@"));

        val address: String = userOperator?.address.toString()
        val disc = productMenu[0].discount
        val discPrice = (productMenu[0].priceOriginal * productMenu[0].discount / 100)

        val format =
            SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm")
        format.timeZone = DateTimeUtil.timeZone

        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        var logoImg = "";

        if (user?.logo != null) {
            logoImg = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                theBitmap
            ) + "</img>\n";
        }

        if (typePayment == "QRIS") {
            var qrCode = "";
            for (row in productSales) {
                if (row.qrCode != "") {
                    qrCode = row.qrCode
                }
            }

            if (qrCode.isNotEmpty()) {
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix =
                    multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE, 400, 400)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

                qrCodeM = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    bitmap
                ) + "</img>\n";
            }
        }

        printer.setTextToPrint(
            logoImg +
                    "[C]~COPY~\n" +
                    "[L]\n" +
                    "[C]"+address+"\n" +
                    "[C]Employee : "+username+"\n" +
                    "[C]" + format.format(createdAt) + "\n" +
                    "[C]================================\n"+
                    "[L]No Antrian: "+orderNo+"\n"+
                    "[L]Transaction ID : " + trxId+"\n"+
                    "[C]================================\n"+
                    header+
                    menu +
                    menuDisc +
                    "[C]================================\n"+
                    subTot +
                    taxTotal +
                    grandTot +
                    "[C]================================\n"+
                    qrCodeM +
                    "[L]<b>Payment Method</b>" + "[R]" + typePayment + "\n" +
                    "[L]<b>Type</b>" + "[R]" + typeOrder + "\n" +
                    "[C]================================\n"+
                    "[C]THANK YOU\n" +
                    "[C]\n" +
                    "[C]\n" +
                    "[C]\n" +
                    "[C]\n"



        )

        return printer
    }





}