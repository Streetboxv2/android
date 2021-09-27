package com.mazenrashed.printooth.ui

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.R
import com.mazenrashed.printooth.data.DiscoveryCallback
import com.mazenrashed.printooth.data.PairedPrinter
import com.mazenrashed.printooth.utilities.Bluetooth
import kotlinx.android.synthetic.main.dialog_scan_bluetooth.*

/**
 * Created by Arif S. on 7/20/20
 */
class ScanBluetoothDialog : DialogFragment() {

    private lateinit var bluetooth: Bluetooth
    private lateinit var adapter: BluetoothDeviceAdapter
    private lateinit var dialogListener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(
            context?.let { ContextCompat.getDrawable(it, R.drawable.dialog_default_bg) })
        return inflater.inflate(R.layout.dialog_scan_bluetooth, container)
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bluetooth = Bluetooth(context)
        adapter = BluetoothDeviceAdapter(ArrayList(), itemClickListener)

        initDeviceCallback()

        rcv_bluetooth_device?.apply {
            adapter = this@ScanBluetoothDialog.adapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        btn_scan.setOnClickListener {
            startDiscovery()
        }

    }

    private fun startDiscovery() {

        if (!bluetooth.isEnabled) {
            bluetooth.enable()
        }

        adapter.clear()
        adapter.addAllData(bluetooth.pairedDevices)

        Handler().postDelayed({
            bluetooth.startScanning()
        }, 1000)
    }

    private val itemClickListener: BluetoothDeviceAdapter.OnItemClickListener =
        object : BluetoothDeviceAdapter.OnItemClickListener {
            override fun onItemClicked(device: BluetoothDevice) {
                if (bluetooth.pairedDevices.contains(device)) {
                    bluetooth.connectToAddress(device.address)
                }

                if (device.bondState == BluetoothDevice.BOND_BONDED) {
                    Printooth.setPrinter(device.name, device.address)
                    dismiss()
                } else if (device.bondState == BluetoothDevice.BOND_NONE)
                    bluetooth.pair(device)
                adapter.notifyDataSetChanged()

                dialogListener.onDeviceSelected(PairedPrinter(device.name, device.address))
            }
        }

    private fun initDeviceCallback() {
        bluetooth.setDiscoveryCallback(object : DiscoveryCallback {
            override fun onDiscoveryStarted() {
                adapter.clear()
                adapter.addAllData(bluetooth.pairedDevices)
                adapter.notifyDataSetChanged()
            }

            override fun onDiscoveryFinished() {
            }

            override fun onDeviceFound(device: BluetoothDevice) {
                if (!adapter.contain(device)) {
                    adapter.addData(device)
                }
            }

            override fun onDevicePaired(device: BluetoothDevice) {
                Printooth.setPrinter(device.name, device.address)
                Toast.makeText(context, "Device Paired", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }

            override fun onDeviceUnpaired(device: BluetoothDevice) {
                Toast.makeText(context, "Device unpaired", Toast.LENGTH_SHORT).show()
                var pairedPrinter = Printooth.getPairedPrinter()
                if (pairedPrinter != null && pairedPrinter.address == device.address)
                    Printooth.removeCurrentPrinter()
                adapter.remove(device)
                bluetooth.startScanning()
            }

            override fun onError(message: String) {
                Toast.makeText(context, "Error while pairing", Toast.LENGTH_SHORT)
                    .show()
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        bluetooth.onStart()
        startDiscovery()
    }

    override fun onStop() {
        super.onStop()
        bluetooth.onStop()
    }

    companion object {
        const val SCANNING_FOR_PRINTER = 115

        fun newInstance(dialogListener: DialogListener) = ScanBluetoothDialog().apply {
            this.dialogListener = dialogListener
        }
    }
}