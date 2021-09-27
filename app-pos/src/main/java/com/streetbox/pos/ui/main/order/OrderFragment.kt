package com.streetbox.pos.ui.main.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.streetbox.pos.R
import com.streetbox.pos.ui.checkout.CheckoutActivity
import com.streetbox.pos.ui.main.MainViewModel
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.DateTimeUtil
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.Utils
import kotlinx.android.synthetic.main.fragment_order.*


/**
 * Created by Arif S. on 2019-11-02
 */
class  OrderFragment : BaseFragment<OrderViewEvent, OrderViewModel>() {

    private lateinit var orderAdapter: OrderAdapter
    private var mainViewModel: MainViewModel? = null
    private lateinit var order: Order

    override fun initResourceLayout(): Int {
        return R.layout.fragment_order
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(OrderViewModel::class.java)

        activity?.let {
            mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }

        orderAdapter = OrderAdapter()

        mainViewModel?.productSalesObserver?.observe(this, Observer {

            if(orderAdapter.data.size == 0){
                orderAdapter.addData(it)
            }
            else {
                orderAdapter.setList(order.productSales)
            }

            tv_total_order_count?.text = "Count : ${orderAdapter.itemCount}"
            viewModel.calculateOrder(order)
            scrollOrderListToBottom()
        })

        mainViewModel?.orderObserver?.observe(this, Observer {
            this.order = it
            orderAdapter.setList(it.productSales)

            if (it.productSales.isNotEmpty()) {
                viewModel.calculateOrder(order)
            }

            tv_no_order?.text = "No. Order : ${it.orderNo}"
            tv_total_order_count?.text = "Count : ${orderAdapter.itemCount}"
            tv_grand_total?.text = "0"

        })

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initList()
        btn_payment.setOnClickListener {
            if (order.productSales.size > 0) {
                context?.let {
                    startActivityForResult(CheckoutActivity.getIntent(it,this. order.uniqueId,"0"), REQ_CODE)
                }
            } else {
                Toast.makeText(context, "Please add at least one item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mainViewModel?.getRecentOrder()
        }
    }

    private fun initList() {
        rcv_order?.apply {
            val lm = LinearLayoutManager(context)
            layoutManager = lm
            adapter = orderAdapter
            val dividerItemDecoration = DividerItemDecoration(
                context,
                lm.orientation
            )
            addItemDecoration(dividerItemDecoration)
        }

        orderAdapter.setOnItemChildClickListener { adapter, view, position ->
            var productSales = adapter.getItem(position) as ProductSales

            when (view.id) {
                R.id.rl_remove -> {
                    if(productSales.qty > 1 ) {
                        productSales.qty = productSales.qty - 1
                        viewModel.removeQtyProduct(productSales)

                        if (productSales.discount > 0){

                            val grandTotal = order.grandTotal - productSales.priceAfterDiscount
                            tv_grand_total.text = ""+NumberUtil.formatToStringWithoutDecimal(grandTotal)
                        }else{
                            val grandTotal = order.grandTotal - productSales.price
                            tv_grand_total.text = ""+NumberUtil.formatToStringWithoutDecimal(grandTotal)
                        }

                        orderAdapter.remove(productSales)



                    }else if(productSales.qty <2) {
                        orderAdapter.remove(productSales)
                        viewModel.removeProductSales(productSales)
                        if (productSales.discount > 0){

                            val grandTotal = order.grandTotal - productSales.priceAfterDiscount
                            tv_grand_total.text = ""+NumberUtil.formatToStringWithoutDecimal(grandTotal)
                        }else{
                            val grandTotal = order.grandTotal - productSales.price
                            tv_grand_total.text = ""+NumberUtil.formatToStringWithoutDecimal(grandTotal)
                        }
                    }
                }
            }
        }

    }

    private fun scrollOrderListToBottom() {
        val scrollPos: Int = orderAdapter.itemCount.minus(1)
        rcv_order?.scrollToPosition(scrollPos)
    }

    override fun onEvent(useCase: OrderViewEvent) {
        when (useCase) {
            is OrderViewEvent.OnCalculateDone -> {
                val orderBill = useCase.orderBill
                this.order = orderBill.order.target//update order to get updated product sales
                val grandTotal = NumberUtil.formatToStringWithoutDecimal(orderBill.subTotal)
                tv_grand_total?.text = grandTotal
            }
            OrderViewEvent.OnRemoveProductSuccess -> {
                tv_total_order_count?.text = "Count : ${orderAdapter.itemCount}"
                viewModel.calculateOrder(order)
//                orderAdapter.setList(order.productSales)
            }
            OrderViewEvent.OnRemoveProductQtySuccess -> {

                    viewModel.calculateOrder(order)
                    orderAdapter . setList (order.productSales)
        }
        }
    }

    companion object {
        private const val REQ_CODE = 1001

        fun newInstance(): OrderFragment {
            return OrderFragment()
        }
    }

}