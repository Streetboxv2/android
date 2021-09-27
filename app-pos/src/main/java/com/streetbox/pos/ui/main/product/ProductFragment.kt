package com.streetbox.pos.ui.main.product

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.mazenrashed.printooth.ui.ScanningActivity
import com.streetbox.pos.R
import com.streetbox.pos.ui.login.LoginActivity
import com.streetbox.pos.ui.main.MainViewModel
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderActivity
import com.streetbox.pos.ui.notification.MessageEvent
import com.streetbox.pos.ui.receipts.ReceiptActivity
import com.zeepos.models.ConstVar
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.Order
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.SharedPreferenceUtil
import kotlinx.android.synthetic.main.fragment_product.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.String


/**
 * Created by Arif S. on 2019-11-02
 */
class ProductFragment : BaseFragment<ProductViewEvent, ProductViewModel>(),
    Toolbar.OnMenuItemClickListener {

    private lateinit var productAdapter: ProductAdapter
    private var mainViewModel: MainViewModel? = null
    private lateinit var order: Order
    private var badgeCount: Int = 0
    private var tvOnlineCount: TextView? = null
    private var selectedDevice: BluetoothConnection? = null


    override fun initResourceLayout(): Int {
        return R.layout.fragment_product
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ProductViewModel::class.java)

        activity?.let {
            mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }

        mainViewModel?.orderObserver?.observe(this, Observer {
            this.order = it
        })

        productAdapter = ProductAdapter()
        viewModel.getAllProducts()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initList()
        val count = context?.let { SharedPreferenceUtil.getString(it, "count", "0") }
            ?: "0"

        EventBus.getDefault().register(this)

        badgeCount = Integer.parseInt(count)

        toolbar.inflateMenu(R.menu.menu_main)
        val menuItem = toolbar.menu.findItem(R.id.action_online_order) as MenuItem
        val actionView = menuItem.actionView

        val tvOnlineCount: TextView = actionView.findViewById<TextView>(R.id.tv_onlinecount)

        setupBadge(tvOnlineCount)

        actionView.findViewById<ImageView>(R.id.iv_onlineorder).setOnClickListener {
            startActivity(context?.let { it1 -> OnlineOrderActivity.getIntent(it1) })
            context?.let { SharedPreferenceUtil.setString(it, "count", "0") }
        }


        toolbar.setOnMenuItemClickListener(this)

    }

    private fun initList() {
        rcv_item?.apply {
            adapter = productAdapter
        }
        rcv_item?.setHasFixedSize(true)

        productAdapter.setOnItemClickListener { adapter, _, position ->
            val product = adapter.getItem(position) as Product

            mainViewModel?.addItem(product, order)
        }

    }


    override fun onEvent(useCase: ProductViewEvent) {
        when (useCase) {
            is ProductViewEvent.GetAllProductsSuccess -> {
                productAdapter.setList(useCase.products.sortedBy { it.name })
                viewModel.getTax()
            }
            is ProductViewEvent.GetSyncSuccess ->
                Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
            is ProductViewEvent.GetAllTransactionSuccess -> {
                Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
            }
            is ProductViewEvent.GetOnlineOrderSuccess -> {
            }
            is ProductViewEvent.GetTaxSuccess -> {
            }
        }
    }

    companion object {
        fun newInstance(): ProductFragment {
            return ProductFragment()
        }
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
//                R.id.action_select_printer -> {
//                    val i = Intent(activity, ScanningActivity::class.java)
//                    startActivityForResult(i, ScanningActivity.SCANNING_FOR_PRINTER)
//                    browseBluetoothDevice()
//                }

               /* R.id.action_sync -> {
                    viewModel.getAllTransaction()
                }*/

                R.id.action_online_order -> {
                    startActivity(context?.let { it1 -> OnlineOrderActivity.getIntent(it1) })
                }

                R.id.iv_onlineorder -> {
                    startActivity(context?.let { it1 -> OnlineOrderActivity.getIntent(it1) })
                }

                R.id.action_receipt -> {
                    context?.let { ctx ->
                        startActivity(ReceiptActivity.getIntent(ctx))
                    }
                }

                R.id.action_logout -> {
                    viewModel.deleteSession()
                    startActivity(context?.let { it1 -> LoginActivity.getIntent(it1) })

                }
                else -> {
                }
            }
        }
        return false
    }

    private fun setupBadge(tv_onlinecount: TextView) {
        if (tv_onlinecount != null) {
            if (badgeCount === 0) {
                if (tv_onlinecount.getVisibility() !== View.GONE) {
                    tv_onlinecount.setVisibility(View.GONE)
                }
            } else {
                tv_onlinecount.setText(
                    String.valueOf(
                        Math.min(
                            badgeCount,
                            99
                        )
                    )
                )
                if (tv_onlinecount.getVisibility() !== View.VISIBLE) {
                    tv_onlinecount.setVisibility(View.VISIBLE)
                    viewModel.getOnlineOrder()
                }
            }
        }
    }

    fun browseBluetoothDevice() {
        val bluetoothDevicesList: Array<BluetoothConnection> =
            BluetoothPrintersConnections().getList()!!
        if (bluetoothDevicesList != null) {
            val items =
                arrayOfNulls<kotlin.String>(bluetoothDevicesList.size + 1)
            items[0] = "Default printer"
            var i = 0
            for (device in bluetoothDevicesList) {
                items[++i] = device.getDevice().getName()
            }
            val alertDialog =
                AlertDialog.Builder(context)
            alertDialog.setTitle("Bluetooth printer selection")
            alertDialog.setItems(
                items
            ) { dialogInterface, i ->
                val index = i - 1
                if (index == -1) {
                    selectedDevice = null
                } else {
                    selectedDevice = bluetoothDevicesList[index]
                    SharedPreferenceUtil.setString(requireContext(), ConstVar.INDEX, "" + index)
                }

            }
            val alert = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) { /* Do something */
        badgeCount = badgeCount + 1
        context?.let { SharedPreferenceUtil.setString(it, "count", badgeCount.toString()) }

        val menuItem = toolbar.menu.findItem(R.id.action_online_order) as MenuItem
        val actionView = menuItem.actionView

        val tvOnlineCount: TextView = actionView.findViewById<TextView>(R.id.tv_onlinecount)

        setupBadge(tvOnlineCount)

    }
}