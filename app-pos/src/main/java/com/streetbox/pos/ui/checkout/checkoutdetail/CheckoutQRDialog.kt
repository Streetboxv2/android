package com.streetbox.pos.ui.checkout.checkoutdetail

import android.Manifest
import android.annotation.SuppressLint
import com.streetbox.pos.R

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.streetbox.pos.async.AsyncBluetoothEscPosPrint
import com.streetbox.pos.async.AsyncEscPosPrinter
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.ui.BaseDialogFragment
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.Utils
import io.objectbox.BoxStore.context
import kotlinx.android.synthetic.main.dialog_checkout_qr.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Arif S. on 7/17/20
 */
class CheckoutQRDialog : BaseDialogFragment() {
    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/
    val PERMISSION_BLUETOOTH = 1
    private var selectedDevice: BluetoothConnection? = null
    private var printed = false

    override fun initResourceLayout(): Int {
        return R.layout.dialog_checkout_qr
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {

        } catch (ex: ClassCastException) {

        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes

        if (activity != null) {
            val appType =
                SharedPreferenceUtil.getString(activity!!, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
                    ?: ConstVar.EMPTY_STRING

            if (appType == ConstVar.APP_CUSTOMER) {
                params?.width = (Utils.getScreenWidth(activity!!) / 1.3).toInt()
            } else {
                params?.width = Utils.getScreenWidth(activity!!) / 2
            }
        } else
            params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        val bundle = arguments!!
        val content = bundle.getString(QR_CONTENT)

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix =
                multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            iv_qr?.setImageBitmap(bitmap)

            activity?.let {
                val appType: String =
                    SharedPreferenceUtil.getString(it, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
                        ?: ConstVar.EMPTY_STRING

                if (appType == ConstVar.APP_CUSTOMER) {
                    val qrUri = Utils.bitmapToFile(it, bitmap, "QR_StreetBox")
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        btn_done.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(activity!!)
            alertDialogBuilder.setMessage("Make sure the previous print process is complete")
            alertDialogBuilder.setPositiveButton(
                "Sure"
            ) { p0, _ ->
                activity?.let {
                    val appType: String =
                        SharedPreferenceUtil.getString(it, ConstVar.APP_TYPE, ConstVar.EMPTY_STRING)
                            ?: ConstVar.EMPTY_STRING

                    if (appType == ConstVar.APP_CUSTOMER) {
                        saveQr()
                    }else {
                        dismiss()
                        activity?.setResult(Activity.RESULT_OK)
                        activity?.finish()
                    }
                }
            }
            alertDialogBuilder.show()
        }
        btn_kitchen_print.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(activity!!)
            alertDialogBuilder.setMessage("Make sure the previous print process is complete")
            alertDialogBuilder.setPositiveButton(
                "Sure"
            ) { p0, _ ->
                printBluetoothKitchen()
                p0?.dismiss()
            }
            alertDialogBuilder.show()
        }

        if (printed === false) {
            Handler().postDelayed({
                printBluetooth()
            }, 1000)
        }
    }


    fun saveQr(){
        val alertDialogBuilder = AlertDialog.Builder(context as Context)
        alertDialogBuilder.setMessage("Upload your QR to scan in your Gallery on Streetbox folder")
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { p0, _ ->
            p0?.dismiss()
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }
        alertDialogBuilder.show()

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
                val bluetoothDevicesList: Array<BluetoothConnection> =
                    BluetoothPrintersConnections().list!!
                selectedDevice = bluetoothDevicesList[0]

                AsyncBluetoothEscPosPrint(context).execute(getAsyncEscPosPrinter(selectedDevice))
                printed = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun printBluetoothKitchen() {
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
                val bluetoothDevicesList: Array<BluetoothConnection> =
                    BluetoothPrintersConnections().list!!
                selectedDevice = bluetoothDevicesList[0]

                AsyncBluetoothEscPosPrint(context).execute(getAsyncEscPosPrinterCopy(selectedDevice))
                btn_kitchen_print.visibility = View.GONE
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
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)

        val bundle = arguments!!
        val printContent = bundle.getString(PRINT_CONTENT)

        printer.textToPrint = printContent

        return printer
    }

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    fun getAsyncEscPosPrinterCopy(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)

        val bundle = arguments!!
        val printKitchenContent = bundle.getString(PRINT_KITCHEN_CONTENT)

        printer.textToPrint = printKitchenContent

        return printer
    }

    companion object {

        private const val QR_CONTENT: String = "QR_CONTENT"
        private const val PRINT_CONTENT: String = "PRINT_CONTENT"
        private const val PRINT_KITCHEN_CONTENT: String = "PRINT_KITCHEN_CONTENT"

        fun getInstance(qrContent: String, printContent: String, printKitchenContent: String): CheckoutQRDialog {
            val fragment = CheckoutQRDialog()
            val bundle = Bundle()
            bundle.putString(QR_CONTENT, qrContent)
            bundle.putString(PRINT_CONTENT, printContent)
            bundle.putString(PRINT_KITCHEN_CONTENT, printKitchenContent)
            fragment.arguments = bundle
            return fragment
        }
    }
}