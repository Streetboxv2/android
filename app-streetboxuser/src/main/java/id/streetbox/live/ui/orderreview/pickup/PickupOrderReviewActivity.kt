package id.streetbox.live.ui.orderreview.pickup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.Order
import com.zeepos.models.transaction.OrderBill
import com.zeepos.models.transaction.ProductSales
import com.zeepos.payment.PaymentActivity
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.showToastExt
import id.streetbox.live.R
import kotlinx.android.synthetic.main.activity_pickup_order_review.*
import kotlinx.android.synthetic.main.item_footer_pickup_order_review.*

/**
 * Created by Arif S. on 7/24/20
 */
class PickupOrderReviewActivity :
    BaseActivity<PickupOrderReviewViewEvent, PickUpOrderReviewViewModel>() {

    private lateinit var pickUpOrderReviewAdapter: PickUpOrderReviewAdapter
    private var merchantId: Long = 0
    private var from: Int = 0
    private lateinit var order: Order
    var tvSubtotal: TextView? = null
    var tvTotalPayment: TextView? = null
    var subTotal: Double = 0.0

    val onClickIncrease: PickUpOrderReviewAdapter.OnClickIncreasePickup =
        object : PickUpOrderReviewAdapter.OnClickIncreasePickup {
            override fun ClickIncrease(productSales: ProductSales, value: Int, tvQty: TextView) {
                if (value > productSales.qtyProduct) {
                    showToastExt("Out of Stock", this@PickupOrderReviewActivity)
                } else {
                    tvQty.text = value.toString()
                    viewModel.updateOrRemoveProductSales(productSales)

                    val total = productSales.price * value
                    subTotal = total
                    tvSubtotal?.text = "Rp. ${NumberUtil.formatToStringWithoutDecimal(total)}"
                    tvTotalPayment?.text = "Rp. ${NumberUtil.formatToStringWithoutDecimal(total)}"

                }
            }

            override fun ClickDecrease(productSales: ProductSales, value: Int, tvQty: TextView) {
                tvQty.text = value.toString()
                viewModel.updateOrRemoveProductSales(productSales)
                val total = productSales.price * value
                subTotal = total
                tvSubtotal?.text = "Rp. ${NumberUtil.formatToStringWithoutDecimal(total)}"
                tvTotalPayment?.text = "Rp. ${NumberUtil.formatToStringWithoutDecimal(total)}"
            }

            override fun ClickTvNoted(productSales: ProductSales) {
                showProductNotesDialog(productSales)
            }

        }

    override fun initResourceLayout(): Int {
        return R.layout.activity_pickup_order_review
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(PickUpOrderReviewViewModel::class.java)

        pickUpOrderReviewAdapter = PickUpOrderReviewAdapter()
        pickUpOrderReviewAdapter.onClickIncrease = onClickIncrease

        merchantId = intent.getLongExtra(MERCHANT_ID, 0)
        from = intent.getIntExtra(FROM, 0)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        viewModel.getRecentOrder(merchantId)

        toolbar.setNavigationOnClickListener { finish() }

        if (from == 0) {
            tv_add_more.visibility = View.VISIBLE
            tv_add_more.setOnClickListener {
                finish()
            }
        } else {
            tv_add_more.visibility = View.INVISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_PAYMENT) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onEvent(useCase: PickupOrderReviewViewEvent) {
        when (useCase) {
            is PickupOrderReviewViewEvent.GetOrderSuccess -> {
                order = useCase.order
                initList()
                pickUpOrderReviewAdapter.setList(order.productSales)

            }
            PickupOrderReviewViewEvent.NoOrderFound -> {
            }
            is PickupOrderReviewViewEvent.UpdateProductSalesSuccess -> {
                pickUpOrderReviewAdapter.notifyDataSetChanged()
            }
            PickupOrderReviewViewEvent.OnRemoveProductSalesQtySuccess -> {
                pickUpOrderReviewAdapter.notifyDataSetChanged()
            }
            PickupOrderReviewViewEvent.UpdateOrderSuccess -> {
            }

        }
    }

    private fun initList() {
        rcv?.apply {
            layoutManager = LinearLayoutManager(this@PickupOrderReviewActivity)
            adapter = pickUpOrderReviewAdapter
            pickUpOrderReviewAdapter.addFooterView(getFooterView())
        }

//        pickUpOrderReviewAdapter.setOnItemChildClickListener { adapter, view, position ->
//            val productSales = adapter.getItem(position) as ProductSales
//
//            when (view.id) {
//                R.id.tv_increase,
//                R.id.tv_decrease
//                -> {
//                        viewModel.updateOrRemoveProductSales(productSales)
//                }
//                R.id.iv_edit_note -> {
//                    showProductNotesDialog(productSales)
//                }
//            }
//        }
    }

    private fun showProductNotesDialog(productSales: ProductSales) {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_note, null)
        dialog.setView(view)
        val etProductNote = view.findViewById<TextInputEditText>(R.id.et_product_notes)

        etProductNote.setText(productSales.notes)

        dialog.setPositiveButton(
            "Save"
        ) { d, _ ->
            productSales.notes = etProductNote.text.toString()
            d.dismiss()
            viewModel.updateOrRemoveProductSales(productSales)
        }

        dialog.setNegativeButton(
            "Cancel"
        ) { d, _ ->
            d.dismiss()
        }

        dialog.show()
    }

    private fun getFooterView(): View {
        val view: View =
            layoutInflater.inflate(R.layout.item_footer_pickup_order_review, rcv, false)
        val tm1 = view.findViewById<TextView>(R.id.tm_1)
        val tm2 = view.findViewById<TextView>(R.id.tm_2)
        val tm3 = view.findViewById<TextView>(R.id.tm_3)
        val tm4 = view.findViewById<TextView>(R.id.tm_4)
        val tm5 = view.findViewById<TextView>(R.id.tm_5)
        val etNotes = view.findViewById<TextInputEditText>(R.id.et_notes)
        tvSubtotal = view.findViewById(R.id.tv_subtotal)
        val tvTotalTax = view.findViewById<TextView>(R.id.tv_total_tax)
        tvTotalPayment = view.findViewById(R.id.tv_total_payment)
        val tvTaxLabel = view.findViewById<TextView>(R.id.tv_tax_label)
        val orderBill: OrderBill?

        val taxSales = if (order.taxSales.isNotEmpty()) order.taxSales[0] else null
        val type = taxSales?.type ?: ConstVar.TAX_TYPE_EXCLUSIVE
        val nameTax = taxSales?.name ?: ConstVar.EMPTY_STRING
        val taxTypeDisplay =
            if (type == ConstVar.TAX_TYPE_EXCLUSIVE) ConstVar.TAX_TYPE_EXCLUSIVE_DISPLAY else ConstVar.TAX_TYPE_INCLUSIVE_DISPLAY

        if (order.orderBill.isNotEmpty()) {
            orderBill = order.orderBill[0]
            tvTaxLabel.text = orderBill.taxName
            tv_total_tax.text = ""+orderBill.totalTax
            if(type < 1){
                tvSubtotal?.text =
                    "Rp. ${NumberUtil.formatToStringWithoutDecimal(orderBill!!.subTotal)}"
                tvTotalTax.text = "Rp. ${NumberUtil.formatToStringWithoutDecimal(orderBill.totalTax)}"
            }else{
                tvSubtotal?.text =
                    "Rp. ${NumberUtil.formatToStringWithoutDecimal((orderBill!!.subTotal) + (orderBill.totalTax))}"
                tvTotalTax.text = "Rp. ${NumberUtil.formatToStringWithoutDecimal(orderBill.totalTax)}"
            }

            tvTotalPayment?.text =
                "Rp. ${NumberUtil.formatToStringWithoutDecimal(orderBill.grandTotal)}"
            if (orderBill.totalTax <= 0) {
                tvTotalTax.visibility = View.INVISIBLE
                tvTaxLabel.visibility = View.INVISIBLE
            }

        }

        etNotes.setText(order.note)
        tvTaxLabel.text = "$nameTax ($taxTypeDisplay)"

        btn_next.setOnClickListener {
            val notes = etNotes.text.toString()
            order.note = notes
            order.grandTotal = subTotal
            viewModel.updateOrder(order)
            startActivityForResult(
                PaymentActivity.getIntent(this, order.uniqueId, order.grandTotal),
                REQ_CODE_PAYMENT
            )
        }

        tm1.setOnClickListener {
            etNotes.setText(tm1.text)
        }

        tm2.setOnClickListener {
            etNotes.setText(tm2.text)
        }

        tm3.setOnClickListener {
            etNotes.setText(tm3.text)
        }

        tm4.setOnClickListener {
            etNotes.setText(tm4.text)
        }

        tm5.setOnClickListener {
            etNotes.setText(tm5.text)
        }

        return view
    }

    companion object {
        private const val MERCHANT_ID = "merchant_id"
        private const val FROM = "from"
        private const val REQ_CODE_PAYMENT = 1002

        fun getIntent(context: Context, merchantId: Long, from: Int = 0): Intent {
            val intent = Intent(context, PickupOrderReviewActivity::class.java)
            intent.putExtra(MERCHANT_ID, merchantId)
            intent.putExtra(FROM, from)
            return intent
        }
    }
}