package com.streetbox.pos.ui.checkout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.streetbox.pos.R
import com.streetbox.pos.ui.checkout.checkoutdetail.CheckoutDetailFragment
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.NumberUtil
import kotlinx.android.synthetic.main.activity_checkout.*

/**
 * Created by Arif S. on 7/12/20
 */
class CheckoutActivity : BaseActivity<CheckoutViewEvent, CheckoutViewModel>() {

    private lateinit var orderAdapter: CheckoutOrderAdapter
    private var orderUniqueId : String? = ConstVar.EMPTY_STRING
    private var cashChange : Double? = 0.0
    private var isActive:Boolean? = false


    override fun initResourceLayout(): Int {
        return R.layout.activity_checkout
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(CheckoutViewModel::class.java)
        orderAdapter = CheckoutOrderAdapter()

         orderUniqueId = intent.getStringExtra(ORDER_UNIQUE_ID)!!
        viewModel.getOrder(orderUniqueId!!)

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.title = "Order No : 0001"
        addFragment(CheckoutDetailFragment.getInstance(orderUniqueId,cashChange ), R.id.fl_detail)
        initList()
    }

    override fun onEvent(useCase: CheckoutViewEvent) {
        when (useCase) {
            is CheckoutViewEvent.GetOrderSuccess -> {
                toolbar.title = "Order No : ${useCase.order.orderNo}"
                orderAdapter.setList(useCase.order.productSales)
                val subtotal =  NumberUtil.formatToStringWithoutDecimal(""+useCase.order.orderBill.get(0).subTotal)
                tv_resSubtotal.setText(""+subtotal)
                val tax =  NumberUtil.formatToStringWithoutDecimal(""+useCase.order.orderBill.get(0).totalTax)
                val taxSales = if (useCase.order.taxSales.isNotEmpty()) useCase.order.taxSales[0] else null
                val type = taxSales?.type ?: 1
                var calculate:Double = 0.0
                calculate = useCase.order.orderBill[0].totalTax
                val nameTax = taxSales?.name?: ConstVar.EMPTY_STRING
                if(taxSales == null || taxSales.isActive == false){
                    tv_resTax.visibility = View.GONE
                    tv_tax.visibility = View.GONE
                    val grantTotal = useCase.order.orderBill.get(0).subTotal
                    tv_resGrandTotal.setText(NumberUtil.formatToStringWithoutDecimal("" + grantTotal))
                }else {
                    if (type == 1) {
                        tv_resTax.visibility = View.VISIBLE
                        tv_tax.visibility = View.VISIBLE
                        tv_tax.setText(nameTax + "(Inclusive)")
                        tv_resTax.setText(NumberUtil.formatToStringWithoutDecimal(calculate))
                        val grantTotal:Double = useCase.order.orderBill.get(0).subTotal
                        tv_resGrandTotal.setText(NumberUtil.formatToStringWithoutDecimal("" + grantTotal))
                    } else if (type == 0) {
                        tv_tax.visibility = View.VISIBLE
                        tv_tax.setText(nameTax + "(Exclusive)")
                        tv_resTax.setText(NumberUtil.formatToStringWithoutDecimal(calculate))
                        val grantTotal:Double =
                            useCase.order.orderBill.get(0).subTotal + calculate
                        tv_resGrandTotal.setText(NumberUtil.formatToStringWithoutDecimal("" + grantTotal))
                    }
                }

            }
        }
    }

    private fun initList() {
        rcv?.apply {
            val lm = LinearLayoutManager(context)
            layoutManager = lm
            adapter = orderAdapter
            val dividerItemDecoration = DividerItemDecoration(
                context,
                lm.orientation
            )
            addItemDecoration(dividerItemDecoration)
        }

    }

    companion object {
        private const val ORDER_UNIQUE_ID = "ORDER_UNIQUE_ID"
        private const val STATE = "STATE"

        fun getIntent(context: Context, orderUniqueId: String, state: String): Intent {
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putExtra(ORDER_UNIQUE_ID, orderUniqueId)
            intent.putExtra(STATE, state)
            return intent
        }
    }
}