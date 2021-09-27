package com.mazenrashed.printooth.ui

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mazenrashed.printooth.R

/**
 * Created by Arif S. on 2020-03-14
 */
class BluetoothDeviceAdapter(
    private val data: MutableList<BluetoothDevice>,
    private val listener: OnItemClickListener?
) : RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>() {

    private val mapData: MutableMap<String, BluetoothDevice> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.bluetooth_device_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.tvItemName.text = item.name
        holder.tvMacAddress.text = item.address

        holder.itemView.setOnClickListener {
            listener?.onItemClicked(item)
        }
    }

    fun clear() {
        data.clear()
        mapData.clear()
        notifyDataSetChanged()
    }

    fun remove(bluetoothDevice: BluetoothDevice) {
        data.remove(bluetoothDevice)
        notifyDataSetChanged()
    }

    fun contain(bluetoothDevice: BluetoothDevice): Boolean {
        if (data.contains(bluetoothDevice))
            return true
        return false
    }

    fun addData(bluetoothDevice: BluetoothDevice) {
        if (!mapData.containsKey(bluetoothDevice.address)) {
            data.add(bluetoothDevice)
            mapData[bluetoothDevice.address] = bluetoothDevice
            notifyDataSetChanged()
        }
    }

    fun addAllData(bluetoothDevices: MutableList<BluetoothDevice>) {
        for (bd in bluetoothDevices) {
            if (!mapData.containsKey(bd.address)) {
                data.add(bd)
                mapData[bd.address] = bd
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tv_bluetooth_name)
        val tvMacAddress: TextView = itemView.findViewById(R.id.tv_mac_address)
    }

    interface OnItemClickListener {
        fun onItemClicked(device: BluetoothDevice)
    }
}