package com.streetbox.pos.ui.checkout.checkoutdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.gson.Gson
import com.google.zxing.MultiFormatWriter
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.data.printer.DefaultPrinter.Companion.ALIGNMENT_CENTER
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.streetbox.pos.R
import com.streetbox.pos.async.AsyncBluetoothEscPosPrint
import com.streetbox.pos.async.AsyncEscPosPrinter
import com.streetbox.pos.ui.main.MainActivity
import com.streetbox.pos.worker.SyncTransactionWorker
import com.zeepos.models.ConstVar
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.dialog_checkout_qr.*
import kotlinx.android.synthetic.main.fragment_checkout_detail.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


/**
 * Created by Arif S. on 7/12/20
 */
class CheckoutDetailFragment : BaseFragment<CheckoutDetailViewEvent, CheckoutDetailViewModel>() {
    private var startDate: Long = DateTimeUtil.getCurrentLocalDateWithoutTime()
    private var endDate: Long = DateTimeUtil.getCurrentLocalDateWithoutTime()
    private var amount1: Int? = 0
    private var amount2: Int? = 0
    private var amount3: Int? = 0
    private var amount4: Int? = 0
    private var grandTotal: Double? = 0.0
    private var cashChange: Double? = 0.0
    private var cashAmount: Double? = 0.0
    private var cashTotalAmount: Double? = 0.0
    private lateinit var order: Order
    private var printing: Printing? = null
    private var isCashPayment = true
    private var loadingCharge = false
    private var user: User? = null

    private var userOperator: User? = null

    private var theBitmap: Bitmap? = null
    private var qrCode: String = ConstVar.EMPTY_STRING
    private var type: Int = 0
    private var menu: String = ""
    private var menuOnly: String = ""
    private var menuDisc: String = ""
    private var subTot: String = ""
    private var grandTot: String = ""
    private var taxTotal: String = ""
    private var pay: String = ""
    private var changeM: String = ""
    private var qrCodeM: String = ""
    private var header: String = ""

    private var printContent: String = ""
    private var printKitchenContent: String = ""
    private var calculate:Double = 0.0


    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/
    val PERMISSION_BLUETOOTH = 1
    private var selectedDevice: BluetoothConnection? = null

    @Inject
    lateinit var gson: Gson


    override fun initResourceLayout(): Int {
        return R.layout.fragment_checkout_detail
    }

    override fun init() {
        checkPrinter()
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(CheckoutDetailViewModel::class.java)
        val args = arguments
        val orderUniqueId = args!!.getString("orderUniqueId")
        viewModel.getOrder("" + orderUniqueId)
        user = viewModel.getProfileMerchantLocal()

        userOperator = viewModel.getOperator()

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


    }


    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener {
            startActivity(context?.let { it1 -> MainActivity.getIntent(it1) })
        }
        btn_charge.setOnClickListener {
            isCashPayment = true
            if (et_cash_amount.text.toString().isNullOrEmpty()) {
                cashAmount = 0.0
            } else if (!et_cash_amount.text.toString().isNullOrEmpty()) {
                cashAmount = et_cash_amount.text.toString().toDouble()
            }

            if (cashAmount!! < grandTotal!!) {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            } else {
                calculateChange()

            }
        }

        btn_other_payment.setOnClickListener {
            showLoading()
            isCashPayment = false
//            viewModel.closeOrder(order.uniqueId)
            order.address = "Address"
            val orderJson = gson.toJson(order)
            viewModel.getQRCodePayment(
                order.merchantId,
                grandTotal!!,
                ConstVar.TRANSACTION_TYPE_ORDER,
                order,
                orderJson
            )


        }


        btn_dinein.setOnClickListener {
            order.typeOrder = "Dine In"
            tv_selected_type.text = "Selected type: " + order.typeOrder
        }

        btn_takeaway.setOnClickListener {
            order.typeOrder = "Take Away"
            tv_selected_type.text = "Selected type: " + order.typeOrder
        }

        btn_doortodoor.setOnClickListener {
            order.typeOrder = "Door to Door"
            tv_selected_type.text = "Selected type: " + order.typeOrder
        }

        btn_onlinedelivery.setOnClickListener {
            order.typeOrder = "Online Delivery"
            tv_selected_type.text = "Selected type: " + order.typeOrder
        }

        btn_gofood.setOnClickListener {
            order.typeOrder = "GoFood"
            tv_selected_type.text = "Selected type: " + order.typeOrder
        }

        btn_grabfood.setOnClickListener {
            order.typeOrder = "GrabFood"
            tv_selected_type.text = "Selected type: " + order.typeOrder
        }

        btn_shopeefood.setOnClickListener {
            order.typeOrder = "ShopeeFood"
            tv_selected_type.text = "Selected type: " + order.typeOrder
        }

        btn_amount_1.setOnClickListener {
            cashAmount = amount1!!.toDouble()
            et_cash_amount.setText("" + amount1)
        }

        btn_amount_2.setOnClickListener {
            cashAmount = amount2!!.toDouble()
            et_cash_amount.setText("" + amount2)
        }

        btn_amount_3.setOnClickListener {
            cashAmount = amount3!!.toDouble()
            et_cash_amount.setText("" + amount3)
        }

        btn_amount_4.setOnClickListener {
            cashAmount = amount4!!.toDouble()
            et_cash_amount.setText("" + amount4!!)
        }
    }


    override fun onEvent(useCase: CheckoutDetailViewEvent) {
        when (useCase) {

            is CheckoutDetailViewEvent.GetOrderSuccess -> {
                order = useCase.order

                val taxSales =
                    if (useCase.order.taxSales.isNotEmpty()) useCase.order.taxSales[0] else null

                type = taxSales?.type ?: 1
                var calculate:Double = 0.0
                calculate = order.orderBill[0].totalTax
                if (taxSales == null || order.taxSales[0].isActive == false) {
                    grandTotal =
                        useCase.order.orderBill.get(0).subTotal
                } else if(order.taxSales[0].isActive == true){
                    if (type == 0) {
                        grandTotal =
                            useCase.order.orderBill.get(0).subTotal + calculate
                    } else if(type == 1) {
                        grandTotal =
                            useCase.order.orderBill.get(0).subTotal
                    }
                }
                val subtotal =
                    NumberUtil.formatToStringWithoutDecimal("" + useCase.order.orderBill.get(0).subTotal)
                tvGrandTotal.text = "" + NumberUtil.formatToStringWithoutDecimal(grandTotal!!)
                if (grandTotal!! > 100000) {
                    amount1 = 100000
                    amount2 = 150000
                    amount3 = 200000
                    amount4 = 250000
                } else {
                    amount1 = 10000
                    amount2 = 20000
                    amount3 = 50000
                    amount4 = 100000
                }

                btn_amount_1.text =
                    "" + NumberUtil.formatToStringWithoutDecimal(amount1!!.toDouble())
                btn_amount_2.text =
                    "" + NumberUtil.formatToStringWithoutDecimal(amount2!!.toDouble())
                btn_amount_3.text =
                    "" + NumberUtil.formatToStringWithoutDecimal(amount3!!.toDouble())
                btn_amount_4.text =
                    "" + NumberUtil.formatToStringWithoutDecimal(amount4!!.toDouble())
            }
            CheckoutDetailViewEvent.CloseOrderSuccess -> {
                dismissLoading()
                val data: HashMap<String, Any> = hashMapOf()
                order.createdAt = DateTimeUtil.getCurrentDateTime()
                order.updatedAt = DateTimeUtil.getCurrentDateTime()
                if(order.taxSales[0].isActive == true && order.taxSales[0].type == 0){
                    order.grandTotal = grandTotal!!
                }else{
                    order.grandTotal = grandTotal!!
                }

                if(order.taxSales[0].isActive == false){
                    order.orderBill[0].totalTax = 0.0
                }
                data["order"] = order
                data["orderBills"] = order.orderBill
                data["productSales"] = order.productSales
                data["paymentSales"] = order.paymentSales
                data["taxSales"] = order.taxSales


                val jsonText: String = gson.toJson(data)
//                viewModel.createSync(ConstVar.SYNC_TYPE_TRANSACTION, order.businessDate, jsonText)

                var syncData = ObjectFactory.createSync(
                    ConstVar.SYNC_TYPE_TRANSACTION,
                    jsonText,
                    order.businessDate
                )

                syncData = viewModel.saveSyncData(syncData)

                context?.let {
                    SyncTransactionWorker.syncTransactionData(it, syncData.uniqueId)
                }


                try {
                    viewModel.getAllTransaction(startDate, endDate, "")
                } catch(e: Exception) {
                    e.printStackTrace()
                    viewModel.getRecentOrder()
                }

            }
            is CheckoutDetailViewEvent.GetAllTransactionSuccess -> {
                viewModel.getRecentOrder()
//                Thread.sleep(2000)
            }
            is CheckoutDetailViewEvent.GetQRCodePaymentSuccess -> {
                qrCode = useCase.data.qrCode!!
                order.trxId = useCase.data.trxId!!


                formatReceipt()
                printBluetooth()

//                viewModel.closeOrder(order.uniqueId)

                qrCode.let {
                    showDialog(CheckoutQRDialog.getInstance(it, printContent, printKitchenContent))
                }

            }
            is CheckoutDetailViewEvent.GetQRCodePaymentFailed -> {
                Toast.makeText(context, "Maaf pembayaran kurang dari total pesanan ", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun formatReceipt() {
        for (i in order.productSales.indices) {

            header = "[L]QTY ITEM[R]PRICE\n"
            var productName = order.productSales[i].name
            if (productName.length > 15) {
                productName = order.productSales[i].name.substring(0,15) + "..."
            }
            val disc = order.productSales[i].discount
            val discPrice =
                (order.productSales[i].priceOriginal * order.productSales[i].discount / 100)
            if (disc > 0) {
                menuDisc += "[L]" + order.productSales[i].qty + "   " + productName + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    order.productSales[i].price
                ) + "\n" + "[L]     discount " + NumberUtil.formatToStringWithoutDecimal(
                    discPrice
                ) + "\n"

            } else {
                menu += "[L]" + order.productSales[i].qty + " " + productName + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    order.productSales[i].price
                ) + "\n"
            }
            menuOnly += "[L]" + order.productSales[i].qty + "   " + productName + "\n"
        }
        val taxSales = if (order.taxSales.isNotEmpty()) order.taxSales[0] else null
        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING
        var calculate:Double = 0.0
        calculate = order.orderBill[0].totalTax
        if (taxSales == null || order.taxSales[0].isActive == false) {
            subTot =
                "[L]<b>Subtotal </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(order.orderBill[0].subTotal) + "\n"
            grandTot = "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                order.orderBill[0].subTotal
            ) + "\n"

        } else {
            subTot =
                "[L]<b>Subtotal </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(order.orderBill[0].subTotal) + "\n"
            if (type == 0 ) {
                taxTotal =
                    "[L]<b>" + taxName + "</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        calculate
                    ) + "\n"
                grandTot =
                    "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        order.orderBill[0].subTotal + calculate
                    ) + "\n"

            }else  if(type == 1){
                taxTotal =
                    "[L]<b>" + taxName + "</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        calculate
                    ) + "\n"
                grandTot =
                    "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        order.orderBill[0].subTotal
                    ) + "\n"
            }
        }

        if (isCashPayment) {
            order.typePayment = "CASH"

            pay =
                "[L]<b>Pay</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal("" + et_cash_amount.text.toString()) + "\n"
            changeM =
                "[L]<b>Change</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal("" + cashChange) + "\n"

        } else {
            order.typePayment = "QRIS"
        }

    }


    fun calculateChange() {
        if (et_cash_amount.text.toString().isEmpty()) {
            cashAmount = 0.0
        } else {
            cashAmount = et_cash_amount.text.toString().toDouble()
        }

        if(order.taxSales[0].isActive == true){
            if(order.taxSales[0].type == 0){
                cashChange = cashAmount!! - grandTotal!!
            }else{
                cashChange = cashAmount!! - grandTotal!!
            }
        }else{
            cashChange = cashAmount!! - grandTotal!!
        }

        formatReceipt()
        printBluetooth()

        showDialog(
            CheckoutSuccessDialog.getInstance(order.uniqueId, cashChange!!, printContent, printKitchenContent),
            CheckoutSuccessDialog::class.java.simpleName
        )

        viewModel.closeOrder(order.uniqueId)
    }

    companion object {

        fun getInstance(orderUniqueId: String?, cashChange: Double?): CheckoutDetailFragment {
            val fragment = CheckoutDetailFragment()
            val args = Bundle()
            args.putString("orderUniqueId", orderUniqueId)
            args.putDouble("cashChange", cashChange?.toDouble()!!)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): CheckoutDetailFragment {
            return CheckoutDetailFragment()
        }
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

    fun printBluetooth() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity().applicationContext as Activity,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else {

            try {
//                printSomePrintable()
//                printSomePrintableKitchen()
                val bluetoothDevicesList: Array<BluetoothConnection> =
                    BluetoothPrintersConnections().list!!
                selectedDevice = bluetoothDevicesList[0]

                getAsyncEscPosPrinter(selectedDevice)

                getAsyncEscPosPrinterCopy(selectedDevice)

                viewModel.closeOrder(order.uniqueId)

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
        val username =
            userOperator!!.userName!!.substring(0, userOperator!!.userName!!.indexOf("@"))

        val address: String = userOperator?.address.toString()
        val format =SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm")



        val tanggal = DateTimeUtil.getDateWithFormat(order.businessDate, "dd/MM/YYYY")
        val jam = DateTimeUtil.getLocalDateWithFormat(order.createdAt, "HH:mm")

        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)

        var logoImg = "";
        if (user?.logo != null) {
            logoImg = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                theBitmap
            ) + "</img>\n";
        }

        try {
            if (qrCode.isNotEmpty()) {
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix =
                    multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE, 400, 400)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

//                    qrCodeM = "[C]<qrcode size='20'>" + qrCode + "</qrcode>\n"
                qrCodeM = "[L]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    bitmap
                ) + "</img>\n";
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        printContent = logoImg +
                "[L]\n" +
                "[C]" + address + "\n" +
                "[C]Employee : " + username + "\n" +
                "[C]" + format.format(Date())+"\n" +
                "[C]================================\n" +
                "[L]No Antrian: " + order.orderNo + "\n" +
                "[L]Order Bill No : " + order.orderBill[0].billNo + "\n" +
                "[C]================================\n" +
                header +
                menu +
                menuDisc +
                "[C]================================\n" +
                subTot +
                taxTotal +
                grandTot +
                "[C]================================\n" +
                pay +
                changeM +
                qrCodeM +
                "[L]<b>Payment Method</b>" + "[R]" + order.typePayment + "\n" +
                "[L]<b>Type</b>" + "[R]" + order.typeOrder + "\n" +
                "[C]================================\n" +
                "[C]THANK YOU\n" +
                "[C]" +
                "[C]"

        printer.textToPrint = printContent

        return printer
    }

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    fun getAsyncEscPosPrinterCopy(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val taxSales = if (order.taxSales.isNotEmpty()) order.taxSales[0] else null

        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING

        val username =
            userOperator!!.userName!!.substring(0, userOperator!!.userName!!.indexOf("@"))


        val address: String = userOperator?.address.toString()
        val disc = order.productSales[0].discount
        val discPrice = (order.productSales[0].priceOriginal * order.productSales[0].discount / 100)

        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:s")


        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        var logoImg = "";

        if (user?.logo != null) {
            logoImg = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                theBitmap
            ) + "</img>\n";
        }

        printKitchenContent = logoImg +
                "[C]~KITCHEN RECEIPT~\n" +
                "[L]\n" +
                "[C]" + address + "\n" +
                "[C]Employee : " + username + "\n" +
                "[C]" + format.format(Date()) + "\n" +
                "[C]================================\n" +
                "[L]No Antrian: " + order.orderNo + "\n" +
                "[L]Order Bill No : " + order.orderBill[0].billNo + "\n" +
                "[C]================================\n" +
                header +
                menuOnly +
                "[C]================================\n" +
                "[L]<b>Payment Method</b>" + "[R]" + order.typePayment + "\n" +
                "[L]<b>Type</b>" + "[R]" + order.typeOrder + "\n" +
                "[C]================================\n" +
                "[C]THANK YOU\n" +
                "[C]" +
                "[C]"

        printer.textToPrint = printKitchenContent

        return printer
    }

}