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
import com.zeepos.models.master.Tax
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
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
import kotlin.collections.ArrayList

/**
 * Created by Arif S. on 10/13/20
 */
class  ReceiptDetailDialog : BaseDialogFragment() {

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
    private var typeTax:Int = 0
    private var taxName:String = ConstVar.EMPTY_STRING
    private var taxAmount:Double = 0.0
    private var isActive:Boolean = false
    var calculate:Double = 0.0



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

        viewModel?.orderObs?.observe(this, Observer {
            order = it
            setData(order)

        })

        val bundle = arguments
        if (bundle != null) {
            val orderUniqueId = bundle.getString("orderUniqueId") ?: ""
            taxName = bundle.getString("taxName","")
            typeTax = bundle.getInt("taxType",0)
            taxAmount = bundle.getDouble("taxAmount",0.0)
            isActive = bundle.getBoolean("isActive",isActive)
            if (orderUniqueId.isNotEmpty()) {
                viewModel?.getOrder(orderUniqueId)
            }
        }

        user = viewModel!!.getProfileMerchantLocal()

        userOperator = viewModel!!.getOperator()


        viewModel?.tax?.observe(this, Observer {
            tax = it


        })

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
            if(order.typeOrder.equals("Online")){
                order.createdAt = order.dateCreated
            }
            formatReceipt()
            printBluetooth()
            Handler().postDelayed({
               dismiss()
            }, 3000)



        }

        btn_void.setOnClickListener {
            viewModel!!.voidOrder(order.trxId)
            startActivity(context?.let { it1 -> ReceiptActivity.getIntent(it1) })
        }

    }

    private fun setData(order: Order) {
        if(order.typeOrder.equals("Online")){
            order.createdAt = order.dateCreated
        }
        if (order.trxId.isNotEmpty()) {
            tv_no_trx?.text = "No Transaksi #${order.trxId}"
        } else {
            tv_no_trx?.text = "No Transaksi #${order.billNo}"
        }
        Log.d(ConstVar.TAG, "order test")
        Log.d(ConstVar.TAG, order.grandTotal.toString())
        Log.d(ConstVar.TAG, order?.orderBill[0]?.billNo)
//        Log.d(ConstVar.TAG, order?.trx[0]?.trxId)
        tv_status_label?.text = "Status: ${order?.orderBill[0]?.billNo}"
        if(order.taxSales[0].isActive == true){
            if(order.taxSales[0].type == 0){
                order.grandTotal = order.orderBill[0].totalTax + order.grandTotal
            }
        }
        tv_grand_total?.text = "${NumberUtil.formatToStringWithoutDecimal(order.grandTotal)}"

        adapter.setList(order.productSales)
        setFooterData(order)
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

    private fun setFooterData(order: Order) {
        val tvSubtotal = footerView.findViewById<TextView>(R.id.tv_subtotals)
        val tvTotalTax = footerView.findViewById<TextView>(R.id.tv_total_tax)
        val tvTaxLabel = footerView.findViewById<TextView>(R.id.tv_tax_label)
        val tvTotalPayment = footerView.findViewById<TextView>(R.id.tv_total_payment)
        val tvPaymentName = footerView.findViewById<TextView>(R.id.tv_payment_name)
        val tvTrxDateLabel = footerView.findViewById<TextView>(R.id.tv_trx_date_label)
        val ivQR = footerView.findViewById<ImageView>(R.id.iv_qr)

        val orderBill = if (order.orderBill.isNotEmpty()) order.orderBill[0] else null
        val taxSales = if (order.taxSales.isNotEmpty()) order.taxSales[0] else null
        val paymentSales = if (order.paymentSales.isNotEmpty()) order.paymentSales[0] else null

        val totalTax: Double = orderBill?.totalTax ?: 0.0
        val subTotal: Double = order.grandTotal ?: 0.0
        var totalPayment: Double = order.grandTotal ?: 0.0
        val taxType = taxSales ?: 1
        val taxTypeDisplay =
            if (taxType == ConstVar.TAX_TYPE_EXCLUSIVE) ConstVar.TAX_TYPE_EXCLUSIVE_DISPLAY else ConstVar.TAX_TYPE_INCLUSIVE_DISPLAY
        val paymentMethod = (paymentSales?.name ?: order?.typePayment ?: "-") + " - " + order?.typeOrder
        if(order.typeOrder.equals("Online")){
            order.createdAt = order.dateCreated

        }

       calculate = order.orderBill[0].totalTax
        val a:Double = order.orderBill[0].subTotal
        if(isActive == true) {
            if (typeTax == 0) {
                tvTotalTax.text = "${NumberUtil.formatToStringWithoutDecimal(calculate)}"
                tvTaxLabel.text = taxName + "(Excl)"
                tvSubtotal.text = "${NumberUtil.formatToStringWithoutDecimal(subTotal - calculate )}"
                tvTotalPayment.text = "${NumberUtil.formatToStringWithoutDecimal(totalPayment)}"
            } else if (typeTax == 1) {
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
                order.businessDate,
                "dd/MM/YYYY"
            )
        } ${DateTimeUtil.getLocalDateWithFormat(order.createdAt, "HH:mm")}"

/*r
        if (totalTax <= 0) {
            tvTotalTax.visibility = View.INVISIBLE
            tvTaxLabel.visibility = View.INVISIBLE
        }b
*/

        if (order.typePayment == "QRIS") {
            var qrCode = "";
            for (row in order.productSales) {
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
            if (order.typePayment.equals("CASH")) {
                btn_void.visibility = View.VISIBLE
            }
        }
    }


    private fun printSomePrintable() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }

    private fun checkPrinter() {
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                context?.let {
                    Toast.makeText(
                        it,
                        "Connecting with printer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun printingOrderSentSuccessfully() {
                context?.let {
                    Toast.makeText(
                        it,
                        "Order sent to printer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun connectionFailed(error: String) {
                context?.let {
                    Toast.makeText(
                        it,
                        "Failed to connect printer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onError(error: String) {
                context?.let {
                    Toast.makeText(it, error, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onMessage(message: String) {
                context?.let {
                    Toast.makeText(it, "Message: $message", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

    }

    fun formatReceipt(){
        for (i in order.productSales.indices) {

            header = "[L]QTY ITEM[R]PRICE\n"
            var productName = order.productSales[i].name
            if (productName.length > 15) {
                productName = order.productSales[i].name.substring(0,15) + "..."
            }
            val disc = order.productSales[i].discount
            val discPrice =
                (order.productSales[i].priceOriginal * order.productSales[i].discount / 100)
            if(disc > 0) {
                menuDisc += "[L]"+ order.productSales[i].qty+"   "+ productName + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    order.productSales[i].price
                ) + "\n" +"[L]     discount " + NumberUtil.formatToStringWithoutDecimal(
                    discPrice)+"\n"

            }else{
                menu += "[L]" + order.productSales[i].qty +" "+ productName  + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    order.productSales[i].price
                ) + "\n"
            }
        }
        val taxSales = if (order.taxSales.isNotEmpty()) order.taxSales[0] else null
        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING
        val grandTotal = order.grandTotal
        calculate =  order.orderBill[0].totalTax
        if(taxSales == null || order.taxSales[0].isActive == false){
            subTot =  "[L]<b>Subtotal </b>" +"[R]"+ NumberUtil.formatToStringWithoutDecimal(order.grandTotal) + "\n"
            grandTot = "[L]<b>Grand Total </b>" + "[R]" +  NumberUtil.formatToStringWithoutDecimal(
                order.grandTotal)+"\n"

        }else if( order.taxSales[0].isActive == true){


            if (type == 1) {
                taxTotal =
                    "[L]<b>" + taxName+ "(Incl)"+ "</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        calculate
                    ) + "\n"
                grandTot =
                    "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                       grandTotal
                    ) + "\n"
                subTot =  "[L]<b>Subtotal </b>" +"[R]"+ NumberUtil.formatToStringWithoutDecimal(order.grandTotal) + "\n"

            }else if (type == 0){
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
        val taxSales = if (order.taxSales.isNotEmpty()) order.taxSales[0] else null

        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING

        val username =
            userOperator!!.userName!!.substring(0, userOperator!!.userName!!.indexOf("@"));

        val address: String = userOperator?.address.toString()
        val disc = order.productSales[0].discount
        val discPrice = (order.productSales[0].priceOriginal * order.productSales[0].discount / 100)

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

        if (order.typePayment == "QRIS") {
            var qrCode = "";
            for (row in order.productSales) {
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
                    "[C]" + format.format(order.createdAt) + "\n" +
                    "[C]================================\n"+
                    "[L]No Antrian: "+order.orderNo+"\n"+
                    "[L]Transaction ID : " + order.trxId+"\n"+
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
                    "[L]<b>Payment Method</b>" + "[R]" + order.typePayment + "\n" +
                    "[L]<b>Type</b>" + "[R]" + order.typeOrder + "\n" +
                    "[C]================================\n"+
                    "[C]THANK YOU\n" +
                    "[C]\n" +
                    "[C]\n" +
                    "[C]\n" +
                    "[C]\n"



        )

        return printer
    }


    private fun getSomePrintables() = ArrayList<Printable>().apply {
        val taxSales = if (order.taxSales.isNotEmpty()) order.taxSales[0] else null

        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING

        val username =
            userOperator!!.userName!!.substring(0, userOperator!!.userName!!.indexOf("@"));

        val address: String = userOperator?.address.toString()

         type = taxSales?.type ?: 1
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode


        add(
            ImagePrintable.Builder(theBitmap!!)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build()
        )


        add(
            TextPrintable.Builder()
                .setText(user!!.name!!)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(12)
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText(address)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Employee  :" + username)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("" + DateTimeUtil.getCurrentTimeStamp("dd-MM-yyyy hh:mm"))
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("===============================")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("No Antrian   : " + order.orderNo)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("OrderBill No : " + order.orderBill[0].billNo)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .build()
        )

        for (i in order.productSales.indices) {
            val disc = order.productSales[i].discount
            val discPrice =
                (order.productSales[i].priceOriginal * order.productSales[i].discount / 100)
            if (disc > 0) {
                add(
                    TextPrintable.Builder()
                        .setText(
                            "" + order.productSales[i].qty + " " + order.productSales[i].name + "    " + "\n" + NumberUtil.formatToStringWithoutDecimal(
                                order.productSales[i].price
                            ) + "    " + "\n" + "disc. " + NumberUtil.formatToStringWithoutDecimal(
                                discPrice
                            )
                        )
                        .setAlignment(DefaultPrinter.LINE_SPACING_60)
                        .setNewLinesAfter(2)
                        .build()
                )
            } else {
                add(
                    TextPrintable.Builder()
                        .setText(
                            "" + order.productSales[i].qty + " " + order.productSales[i].name + "    " + "\n" + NumberUtil.formatToStringWithoutDecimal(
                                order.productSales[i].price
                            )
                        )
                        .setAlignment(DefaultPrinter.LINE_SPACING_60)
                        .setNewLinesAfter(2)
                        .build()
                )
            }

        }
        add(
            TextPrintable.Builder()
                .setText("SubTotal          : " + NumberUtil.formatToStringWithoutDecimal(order.orderBill[0].subTotal))
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .build()
        )

        if (taxSales == null) {
            add(
                TextPrintable.Builder()
                    .setText("Grand Total        : " + NumberUtil.formatToStringWithoutDecimal(order.orderBill[0].subTotal))
                    .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                    .setNewLinesAfter(1)
                    .build()
            )
        } else {

            if (type < 1) {

                add(
                    TextPrintable.Builder()
                        .setText(
                            taxName + "(Excl.)       : " + NumberUtil.formatToStringWithoutDecimal(
                                order.orderBill[0].totalTax
                            )
                        )
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )



                add(
                    TextPrintable.Builder()
                        .setText(
                            "Grand Total       : " + NumberUtil.formatToStringWithoutDecimal(
                                order.orderBill[0].subTotal + order.orderBill[0].totalTax
                            )
                        )
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )

            } else {

                add(
                    TextPrintable.Builder()
                        .setText(
                            taxName + "(Incl.)      : " + NumberUtil.formatToStringWithoutDecimal(
                                order.orderBill[0].totalTax
                            )
                        )
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )

                add(
                    TextPrintable.Builder()
                        .setText(
                            "Grand Total       : " + NumberUtil.formatToStringWithoutDecimal(
                                order.orderBill[0].subTotal
                            )
                        )
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )

            }
        }

        add(
            TextPrintable.Builder()
                .setText("===============================")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setAlignment(DefaultPrinter.LINE_SPACING_60)
                .setNewLinesAfter(1)
                .build()
        )


        add(
            TextPrintable.Builder()
                .setText("===============================")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setAlignment(DefaultPrinter.LINE_SPACING_60)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Thank You")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )
    }


}