package com.streetbox.pos.ui.setting.printersetting

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.mazenrashed.printooth.data.PairedPrinter
import com.mazenrashed.printooth.ui.DialogListener
import com.mazenrashed.printooth.ui.ScanBluetoothDialog
import com.mazenrashed.printooth.ui.ScanningActivity
import com.streetbox.pos.R
import com.zeepos.ui_base.ui.BaseDialogFragment
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.dialog_create_printer.*

/**
 * Created by Arif S. on 7/20/20
 */
class CreatePrinterDialog : BaseDialogFragment() {

    private var viewModel: PrinterSettingViewModel? = null
    private lateinit var dialogListener: DialogListener
    private var scanBluetoothDialog: ScanBluetoothDialog? = null
    private var bluetoothDevice: BluetoothDevice? = null

    override fun initResourceLayout(): Int {
        return R.layout.dialog_create_printer
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes

        if (activity != null)
            params?.width = Utils.getScreenWidth(activity!!) / 2
        else
            params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.attributes = params as WindowManager.LayoutParams

        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        parentFragment?.let {
            viewModel = ViewModelProvider(it).get(PrinterSettingViewModel::class.java)
        }

        sp_printer_type?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val type = p0?.getItemAtPosition(p2) as String
                if (type == "Bluetooth") {
                    showScanBluetoothDialog()
                }
            }

        }

        btn_save.setOnClickListener {
            val name = et_name.text.toString()
            val length = et_max_char.text.toString()
            val header = et_printer_title.text.toString()
            val footer = et_printer_footer.text.toString()
//            val attribute = bluetoothDevice?.address ?: ""
//
//            if (name.isNotEmpty() && attribute.isNotEmpty() && selectedPrinterType.isNotEmpty()) {
//                val printerSetting = ObjectFactory.createPrinterSetting(
//                    name, selectedPrinterType, attribute, length.toInt(), "", header, footer
//                )
//
//                dialogListener.onEvent(printerSetting)
//            } else {
//                //TODO: Mandatory error
//            }
        }
    }

    private fun showScanBluetoothDialog() {
        childFragmentManager.let { it1 ->
            scanBluetoothDialog = ScanBluetoothDialog.newInstance(scanBluetoothDialogListener)
            scanBluetoothDialog?.show(it1, "")
        }
    }

    private val scanBluetoothDialogListener = object :
        DialogListener {

        override fun onDeviceSelected(pairedPrinter: PairedPrinter) {
            Log.i("Thread name : %s", Thread.currentThread().name)
            activity?.runOnUiThread {
                tv_printer_att?.text = pairedPrinter.address
            }
        }

        override fun onDismiss() {
        }

    }
}