package com.streetbox.pos.ui.main.onlineorder.orderbill

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View
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
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.data.printer.DefaultPrinter.Companion.ALIGNMENT_CENTER
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.streetbox.pos.R
import com.streetbox.pos.async.AsyncBluetoothEscPosPrint
import com.streetbox.pos.async.AsyncEscPosPrinter
import com.streetbox.pos.ui.main.MainActivity
import com.streetbox.pos.ui.main.MainViewModel
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderActivity
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderViewModel
import com.streetbox.pos.ui.main.onlineorder.orderbil.OrderBillViewModel
import com.streetbox.pos.worker.SyncTransactionWorker
import com.zeepos.models.ConstVar
import com.zeepos.models.factory.ObjectFactory
import com.zeepos.models.master.SyncData
import com.zeepos.models.master.User
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.fragment_checkout_detail.*
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_orderbill.*
import kotlinx.android.synthetic.main.fragment_orderbill.rcv_order
import kotlinx.android.synthetic.main.fragment_orderbill.tv_grand_total
import kotlinx.android.synthetic.main.fragment_orderbill.tv_no_order
import kotlinx.android.synthetic.main.fragment_orderbill.tv_tax
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class OrderBillFragment : BaseFragment<OrderBillViewEvent, OrderBillViewModel>() {

    private lateinit var orderBillAdapter: OrderBillAdapter
    private var onlineOrderViewModel: OnlineOrderViewModel? = null
    private var order: Order? = null
    private var orderAntrian: Order? = null
    private var trxId:String? = ConstVar.EMPTY_STRING
    private var user: User? = null
    private var printing: Printing? = null
    private var theBitmap: Bitmap? = null
    private var userOperator: User? = null
    private  var  type : Int = 0
    @Inject
    lateinit var gson: Gson
    private var menu:String = ""
    private var menuDisc:String = ""
    private var subTot: String=""
    private var grandTot: String=""
    private var taxTotal:String=""
    private var pay:String =""
    private var changeM:String =""
    private var qrCodeM:String = ""
    private var header:String=""



    /*==============================================================================================
   ======================================BLUETOOTH PART============================================
   ==============================================================================================*/
    val PERMISSION_BLUETOOTH = 1
    private var selectedDevice: BluetoothConnection? = null


    override fun initResourceLayout(): Int {
        return R.layout.fragment_orderbill
    }

    override fun init() {

//        checkPrinter()

        viewModel = ViewModelProvider(this, viewModeFactory).get(OrderBillViewModel::class.java)


        user = viewModel.getProfileMerchantLocal()

        userOperator = viewModel.getOperator()

        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (user?.logo != null) user?.logo else ConstVar.EMPTY_STRING

        GlideApp.with(this)
            .asBitmap()
            .load(imageUrl)
            .override(200,200)
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


        activity?.let {
            onlineOrderViewModel = ViewModelProvider(it).get(OnlineOrderViewModel::class.java)
        }


        onlineOrderViewModel?.orderObserver?.observe(this, Observer {
            this.order = it

            val data: MutableList<Order> = mutableListOf()
            data.add(order!!)

            orderBillAdapter.setList(order!!.productSales)

            trxId = data.get(0).trxId

            if(order!!.note.isNotEmpty()) {
                tv_notesOrder.visibility = View.VISIBLE
                tv_resNotesOrder.visibility= View.VISIBLE
                tv_resNotesOrder.setText(order!!.note)
            }



           if(order!!.phone != null ) {
                    tv_phoneCustomer.visibility = View.VISIBLE
                    tv_resPhoneCustomer.visibility = View.VISIBLE
                    tv_resPhoneCustomer.setText(order!!.phone)
            }


            if(order!!.orderBill.isEmpty()){

            }else {
                val tax = order!!.orderBill[0].totalTax
                val taxSales = if (order!!.taxSales.isNotEmpty()) order!!.taxSales[0] else null

                type = taxSales?.type ?: 1
                val nameTax = taxSales?.name ?: ConstVar.EMPTY_STRING
                var calculate:Double = 0.0
                calculate = order!!.orderBill[0].totalTax
                if (taxSales == null || order!!.taxSales[0].isActive == false) {
                    tv_taxLabel.visibility = View.GONE
                    tv_tax.visibility = View.GONE
                    tv_resSubTotal.setText("" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal ))
                    tv_grand_total.setText("" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal))
                } else {
                    if (type == 0) {
                        tv_taxLabel.visibility = View.VISIBLE
                        tv_tax.visibility = View.VISIBLE
                        tv_taxLabel.setText(nameTax + "(Excl.)")
                        tv_tax.setText("" + NumberUtil.formatToStringWithoutDecimal(calculate))
                        tv_grand_total.setText("" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal ))
                        tv_resSubTotal.setText("" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal - calculate))
                    } else if(type == 1) {
                        tv_taxLabel.visibility = View.VISIBLE
                        tv_tax.visibility = View.VISIBLE
                        tv_taxLabel.setText(nameTax + "(Incl.)")
                        tv_tax.setText("" + NumberUtil.formatToStringWithoutDecimal(calculate))
                        tv_grand_total.setText("" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal))
                        tv_resSubTotal.setText("" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal ))
                    }
                }


                tv_no_order.setText("OrderBill No: " + order!!.orderBill[0].billNo)

            }

        })
        orderBillAdapter = OrderBillAdapter()

    }

    private fun initList() {
        rcv_order?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderBillAdapter
        }
    }

    override fun onEvent(useCase: OrderBillViewEvent) {
        when (useCase) {

            is OrderBillViewEvent.GetProductSuccess -> {

            }
            is OrderBillViewEvent.GetOrderBillSuccess -> TODO()
            OrderBillViewEvent.CloseOnlineOrderSuccess -> {
                viewModel?.closeOrder(order!!.uniqueId)

            }
            is OrderBillViewEvent.CloseOnlineOrderFailed -> {
                Toast.makeText(context,"Failed",Toast.LENGTH_LONG).show()
            }
            OrderBillViewEvent.CloseOrderSuccess -> {
                val data: HashMap<String, Any> = hashMapOf()
                data["order"] = order!!
                data["orderBills"] = order!!.orderBill
                data["productSales"] = order!!.productSales
                data["paymentSales"] = order!!.paymentSales
                data["taxSales"] = order!!.taxSales

                val jsonText: String = gson.toJson(data)

                var syncData = ObjectFactory.createSync(
                    ConstVar.SYNC_TYPE_TRANSACTION,
                    jsonText,
                    order!!.businessDate
                )

                syncData = viewModel.saveSyncData(syncData)

                context?.let {
                    SyncTransactionWorker.syncTransactionData(it, syncData.uniqueId)
                }

                Handler().postDelayed({
                   startActivity(context?.let { OnlineOrderActivity.getIntent(it) })
                }, 1000)

            }
            OrderBillViewEvent.OnRemoveProductSuccess -> {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            onlineOrderViewModel?.getRecentOrder()
        }
    }

    companion object {
        fun newInstance(): OrderBillFragment {
            return OrderBillFragment()
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initList()
        onlineOrderViewModel?.getRecentOrder()
        btn_print.setOnClickListener{

            showLoading()
             formatReceiptformat()
             printBluetooth()
//            trxId?.let { it1 -> viewModel.closeOnlineOrder(it1) }
        }

        }

    private fun printSomePrintable() {
        val printables = getSomePrintables()
        printing?.print(printables)
        trxId?.let { it1 -> viewModel.closeOnlineOrder(it1) }
    }

    private fun checkPrinter() {
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(
                    context,
                    "Connecting with printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(
                    context,
                    "Order sent to printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(
                    context,
                    "Failed to connect printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: String) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(context, "Message: $message", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    fun formatReceiptformat(){
        for (i in order!!.productSales.indices) {

            header = "[L]QTY ITEM[R]PRICE\n"
            var productName = order!!.productSales[i].name
            if (productName.length > 15) {
                productName = order!!.productSales[i].name.substring(0,15) + "..."
            }
            val disc = order!!.productSales[i].discount
            val discPrice =
                (order!!.productSales[i].priceOriginal * order!!.productSales[i].discount / 100)
            if(disc > 0) {
                menuDisc += "[L]" +order!!.productSales[i].qty +"   "+ productName + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    order!!.productSales[i].price
                ) + "\n" +"[L]     discount " + NumberUtil.formatToStringWithoutDecimal(
                    discPrice)+"\n"

            }else{
                menu += "[L]"+ order!!.productSales[i].qty+"   "+ productName + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                    order!!.productSales[i].price
                ) + "\n"
            }
        }
        val taxSales = if (order!!.taxSales.isNotEmpty()) order!!.taxSales[0] else null
        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING
        if(taxSales == null || taxSales.isActive == false){

            subTot =  "[L]<b>Subtotal </b>" +"[R]"+ NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal) + "\n"
            grandTot = "[L]<b>Grand Total </b>" + "[R]" +  NumberUtil.formatToStringWithoutDecimal(
                order!!.grandTotal)+"\n"

        }else{
            var calculate:Double = 0.0
            calculate = order!!.orderBill[0].totalTax

            if (type == 0) {
                subTot =
                    "[L]<b>Subtotal </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal - calculate) + "\n"
                taxTotal =
                    "[L]<b>" + taxName + "</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        calculate
                    ) + "\n"
                grandTot =
                    "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        order!!.grandTotal
                    ) + "\n"

            }else if(type == 1){
                subTot =
                    "[L]<b>Subtotal </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal ) + "\n"
                taxTotal =
                    "[L]<b>" + taxName + "</b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        calculate
                    ) + "\n"
                grandTot =
                    "[L]<b>Grand Total </b>" + "[R]" + NumberUtil.formatToStringWithoutDecimal(
                        order!!.grandTotal
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

//                    Handler().postDelayed({
                        trxId?.let { it1 -> viewModel.closeOnlineOrder(it1) }
//                    }, 1000)


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
        val taxSales = if (order!!.taxSales.isNotEmpty()) order!!.taxSales[0] else null
        val trxs = trxId
        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING

        val username =
            userOperator!!.userName!!.substring(0, userOperator!!.userName!!.indexOf("@"));

        val address: String = userOperator?.address.toString()
        val disc = order!!.productSales[0].discount
        val discPrice = (order!!.productSales[0].priceOriginal * order!!.productSales[0].discount / 100)

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




        printer.setTextToPrint(
            logoImg +
                    "[L]\n" +
                    "[C]"+address+"\n" +
                    "[C]Employee : "+username+"\n" +
                    "[C] "+ format.format(order!!.createdAt) + "\n" +
                    "[C]================================\n"+
                    "[C]================================\n"+
                    "[L]No Antrian: "+order!!.orderNo+"\n"+
                    "[L]Transaction ID : " + order!!.trxId+"\n"+
                    "[L]Type Order: " + order!!.typeOrder+"\n"+
                    "[C]================================\n"+
                    header+
                    menu +
                    menuDisc +
                    "[C]================================\n"+
                    subTot +
                    taxTotal +
                    grandTot +
                    "[C]================================\n"+
                    pay +
                    changeM +
                    qrCodeM +
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

        val taxSales = if (order!!.taxSales.isNotEmpty()) order!!.taxSales[0] else null

        val taxName = taxSales?.name ?: ConstVar.EMPTY_STRING

        val address: String = userOperator?.address.toString()
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode

        val username = userOperator!!.userName!!.substring(0,userOperator!!.userName!!.indexOf("@"));

        add(
            ImagePrintable.Builder(theBitmap!!)
                .setAlignment(ALIGNMENT_CENTER)
                .build())


        add(
            TextPrintable.Builder()
                .setText(user!!.name!!)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(10)
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
                .setText("Employee  :"+username)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText(""+DateTimeUtil.getCurrentTimeStamp("dd-MM-yyyy hh:mm"))
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
                .setText("No Antrian   : "+order!!.orderNo)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("OrderBill No : "+order!!.orderBill[0].billNo)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setNewLinesAfter(1)
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .build()
        )

        for( i in order!!.productSales.indices) {
            add(
                TextPrintable.Builder()
                    .setText(order!!.productSales[i].name + "\n" + ""+order!!.productSales[i].qty+ " x " + NumberUtil.formatToStringWithoutDecimal(order!!.productSales[i].price))
                    .setAlignment(DefaultPrinter.LINE_SPACING_60)
                    .setNewLinesAfter(2)
                    .build()
            )
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
                        .setText("SubTotal              : "+  NumberUtil.formatToStringWithoutDecimal(order!!.orderBill[0].subTotal))
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )
        if(taxSales == null){
                add(
                    TextPrintable.Builder()
                        .setText("Grand Total         : "+ NumberUtil.formatToStringWithoutDecimal(order!!.orderBill[0].subTotal))
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )
        }else {

            if (type < 1) {

                add(
                    TextPrintable.Builder()
                        .setText(taxName + "(Excl.)     : " + NumberUtil.formatToStringWithoutDecimal(
                            order!!.orderBill[0].totalTax
                        )

                        )
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )


                add(
                    TextPrintable.Builder()
                        .setText("Grand Total          : " + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal + order!!.orderBill[0].totalTax))
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )

            } else if(type > 1){

                add(
                    TextPrintable.Builder()
                        .setText(taxName + "(Incl.)      : " + NumberUtil.formatToStringWithoutDecimal(
                            order!!.orderBill[0].totalTax
                        )

                        )
                        .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                        .setNewLinesAfter(1)
                        .build()
                )

                add(
                    TextPrintable.Builder()
                        .setText("Grand Total            : " + NumberUtil.formatToStringWithoutDecimal(order!!.grandTotal))
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
                .setText("Thank You")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )



    }


}