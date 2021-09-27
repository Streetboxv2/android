package com.streetbox.pos.ui.checkout.checkoutdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.streetbox.pos.R
import com.streetbox.pos.async.AsyncBluetoothEscPosPrint
import com.streetbox.pos.async.AsyncEscPosPrinter
import com.streetbox.pos.ui.main.MainActivity
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderActivity
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.ui.BaseDialogFragment
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.SharedPreferenceUtil
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.dialog_checkout_success.*
import kotlinx.android.synthetic.main.dialog_checkout_success.btn_done
import kotlinx.android.synthetic.main.dialog_checkout_success.btn_kitchen_print

/**
 * Created by Arif S. on 7/15/20
 */
class CheckoutSuccessDialog : BaseDialogFragment() {
    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/
    val PERMISSION_BLUETOOTH = 1
    private var selectedDevice: BluetoothConnection? = null

    private var viewModel: CheckoutDetailViewModel? = null
    private lateinit var orderUniqueId: String
    private var cashChange: Double? = 0.0
    private var printed = false

    override fun initResourceLayout(): Int {
        return R.layout.dialog_checkout_success
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes

        if (activity != null)
            params?.width = Utils.getScreenWidth(requireActivity()) / 2
        else
            params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        dialog?.setCancelable(false)

        orderUniqueId = arguments?.getString(ID)!!
        cashChange = arguments?.getDouble(CASH_CHANGE)

        parentFragment?.let {
            viewModel = ViewModelProvider(it).get(CheckoutDetailViewModel::class.java)
        }

        tv_change.setText(cashChange?.let { NumberUtil.formatToStringWithoutDecimal(it) } )

        btn_done.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(activity!!)
            alertDialogBuilder.setMessage("Make sure the previous print process is complete")
            alertDialogBuilder.setPositiveButton(
                "Sure"
            ) { p0, _ ->
//            viewModel?.closeOrder(orderUniqueId)
//            Thread. sleep(1000)
                startActivity(context?.let { it1 -> MainActivity.getIntent(it1) })
//            dismiss()
//            activity?.setResult(Activity.RESULT_OK)
//            activity?.finish()
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

        if (printed == false) {
            Handler().postDelayed({
                printBluetooth()
            }, 1000)
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

        private const val ID = "orderUniqueId"
        private const val CASH_CHANGE = "cashChange"
        private const val PRINT_CONTENT: String = "PRINT_CONTENT"
        private const val PRINT_KITCHEN_CONTENT: String = "PRINT_KITCHEN_CONTENT"

        fun getInstance(orderUniqueId: String?, cashChange: Double, printContent: String, printKitchenContent: String): CheckoutSuccessDialog {
            val fragment = CheckoutSuccessDialog()
            val bundle = Bundle()
            bundle.putString(ID, orderUniqueId)
            bundle.putDouble(CASH_CHANGE, cashChange)
            bundle.putString(PRINT_CONTENT, printContent)
            bundle.putString(PRINT_KITCHEN_CONTENT, printKitchenContent)
            fragment.arguments = bundle
            return fragment
        }
    }
}