package id.streetbox.live.ui.main.orderhistory.orderhistorydetail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.entities.OrderHistory
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.*
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterListMenuDetailOrder
import id.streetbox.live.adapter.AdapterListMenuDetailOrderNearby
import id.streetbox.live.ui.main.orderhistory.OrderHistoryViewEvent
import kotlinx.android.synthetic.main.activity_order_history_detail.*
import kotlinx.android.synthetic.main.header_order_history_detail.*
import javax.inject.Inject

/**
 * Created by Arif S. on 8/10/20
 */
class OrderHistoryDetailActivity :
    BaseActivity<OrderHistoryViewEvent, OrderHistoryDetailViewModel>() {

    @Inject
    lateinit var gson: Gson
    private lateinit var orderHistory: OrderHistory
    private lateinit var orderHistoryDetailAdapter: OrderHistoryDetailAdapter

    override fun initResourceLayout(): Int {
        return R.layout.activity_order_history_detail
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(OrderHistoryDetailViewModel::class.java)

        val bundle = intent.extras
        if (bundle != null) {
            val data = bundle.getString("data")!!
            orderHistory = gson.fromJson(data, OrderHistory::class.java)

            val orderDetails = orderHistory.detail?.orderDetails ?: arrayListOf()
            orderHistoryDetailAdapter =
                OrderHistoryDetailAdapter(orderDetails.toMutableList())
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }
        initList()
    }

    private fun initMenuDetailOrder(rvMenuDetailOrder: RecyclerView, tvMenuDetilOrder: TextView) {
        val groupieAdapter = GroupAdapter<GroupieViewHolder>().apply {
            if (orderHistory.detail?.orderDetails?.isNotEmpty()!!) {
                if (orderHistory.detail?.orderDetails?.get(0)?.menus?.isNotEmpty()!!) {
                    showView(tvMenuDetilOrder)
                    orderHistory.detail?.orderDetails?.get(0)?.menus?.forEach {
                        add(AdapterListMenuDetailOrder(it))
                    }
                } else {
                    orderHistory.detail!!.orderDetails.forEach {
                        add(AdapterListMenuDetailOrderNearby(it))
                    }
                }
            } else hideView(tvMenuDetilOrder)
        }

        setRvAdapterVertikal(rvMenuDetailOrder, groupieAdapter)
    }

    override fun onEvent(useCase: OrderHistoryViewEvent) {
    }

    private fun initList() {
        rcv?.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryDetailActivity)
            adapter = orderHistoryDetailAdapter
            orderHistoryDetailAdapter.addHeaderView(getHeaderView())
            orderHistoryDetailAdapter.addFooterView(getFooterView())
        }

        orderHistoryDetailAdapter.setOnItemChildClickListener { adapter, view, position ->
        }
    }

    private fun getHeaderView(): View {
        val view: View =
            layoutInflater.inflate(R.layout.header_order_history_detail, rcv, false)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvTrxDate = view.findViewById<TextView>(R.id.tv_trx_date)
        val tvTrxNumber = view.findViewById<TextView>(R.id.tv_trx_number)
        val tvTrxGrandTotal = view.findViewById<TextView>(R.id.tv_trx_grand_total)
        val tvAddress = view.findViewById<TextView>(R.id.tv_address)
        val ivProduct = view.findViewById<ImageView>(R.id.iv_product)
        val tvTrxType = view.findViewById<TextView>(R.id.tv_trx_type)
        val tvMenuDetilOrder = view.findViewById<TextView>(R.id.tvListMenuDetailOrder)
        val rvMenuDetailOrder = view.findViewById<RecyclerView>(R.id.rvListMenuDetailOrder)
        val view1 = view.findViewById<View>(R.id.sp_1)

        val dateLong = DateTimeUtil.getDateFromString(
            orderHistory.date,
            DateTimeUtil.FORMAT_DATE_SERVER_2
        )?.time ?: 0

        tvTrxGrandTotal.text = "${NumberUtil.formatToStringWithoutDecimal(orderHistory.amount)}"
        val taxType =
            orderHistory.detail?.paymentDetails?.taxType ?: ConstVar.TAX_TYPE_EXCLUSIVE
        tvTrxNumber.text = "${orderHistory.trxId}"
        tvAddress.text = orderHistory.address
        tvName.text = orderHistory.merchantName
        tvTrxDate.text = "${DateTimeUtil.getDateWithFormat(dateLong, "dd MMM yyyy")}, " +
                "${DateTimeUtil.getDateWithFormat(dateLong, "HH:mm")} "

        if (orderHistory.types == ConstVar.TRANSACTION_TYPE_VISIT) {
            tvTrxType.visibility = View.VISIBLE
            tvTrxGrandTotal.visibility = View.GONE
        } else {
            tvTrxType.visibility = View.GONE
            tvTrxGrandTotal.visibility = View.VISIBLE
        }

        val imageUrl: String = ConstVar.PATH_IMAGE + orderHistory.logo

        GlideApp.with(this)
            .load(imageUrl)
            .into(ivProduct)

        if (!orderHistory.types.equals("ORDER", ignoreCase = true)) {
            initMenuDetailOrder(rvMenuDetailOrder, tvMenuDetilOrder)
        } else hideView(view1)

        return view
    }

    private fun getFooterView(): View {
        val view: View =
            layoutInflater.inflate(R.layout.footer_order_history_detail, rcv, false)

        val tvSubtotal = view.findViewById<TextView>(R.id.tv_subtotal)
        val tvTotalTax = view.findViewById<TextView>(R.id.tv_total_tax)
        val tvTaxLabel = view.findViewById<TextView>(R.id.tv_tax_label)
        val tvTotalPayment = view.findViewById<TextView>(R.id.tv_total_payment)
        val tvPaymentName = view.findViewById<TextView>(R.id.tv_payment_name)
        val tvNote = view.findViewById<TextView>(R.id.tv_note)
        val tvPhone = view.findViewById<TextView>(R.id.tv_phone)
        val ivQr = view.findViewById<ImageView>(R.id.iv_qr)
        val taxType =
            orderHistory.detail?.paymentDetails?.taxType ?: ConstVar.TAX_TYPE_EXCLUSIVE
        val taxTypeDisplay =
            if (taxType == ConstVar.TAX_TYPE_EXCLUSIVE) ConstVar.TAX_TYPE_EXCLUSIVE_DISPLAY else ConstVar.TAX_TYPE_INCLUSIVE_DISPLAY

        val totalTax: Double = orderHistory.detail?.paymentDetails?.tax ?: 0.0
        val calculate:Double = (totalTax/100) * orderHistory.detail?.paymentDetails?.total!!
        if (orderHistory.types.equals("ORDER") && orderHistory.detail?.paymentDetails?.isActive == true) {
            if(taxType == 0){
                tvTotalTax.text = "${NumberUtil.formatToStringWithoutDecimal(totalTax)}"
                tvSubtotal.text = "${NumberUtil.formatToStringWithoutDecimal(orderHistory.detail?.paymentDetails?.total!! - totalTax)}"
                tvTotalPayment.text =
                    "${NumberUtil.formatToStringWithoutDecimal(orderHistory.detail?.paymentDetails?.total!!)}"
                tvTaxLabel.text = "${orderHistory.detail?.paymentDetails?.taxName} ($taxTypeDisplay)"
            }else if(taxType == 1) {
                tvTotalTax.text = "${NumberUtil.formatToStringWithoutDecimal(totalTax)}"
                tvSubtotal.text = "${NumberUtil.formatToStringWithoutDecimal(orderHistory.detail?.paymentDetails?.total!!)}"
                tvTotalPayment.text =
                    "${NumberUtil.formatToStringWithoutDecimal(orderHistory.amount)}"
                tvTaxLabel.text = "${orderHistory.detail?.paymentDetails?.taxName} ($taxTypeDisplay)"

            }
        } else {
            tvTotalPayment.text =
                "${NumberUtil.formatToStringWithoutDecimal(orderHistory.detail?.paymentDetails?.total!!)}"
            tvSubtotal.text =
                "${NumberUtil.formatToStringWithoutDecimal(orderHistory.detail?.paymentDetails?.total!!)}"
            tvTaxLabel.visibility = View.GONE
            tvTotalTax.visibility = View.GONE
        }



        tvPaymentName.text = "${orderHistory.detail?.paymentName}"

        tvPhone.text = orderHistory.phone
        tvNote.text = orderHistory.notes

        Linkify.addLinks(tvPhone, Linkify.PHONE_NUMBERS)

       /* if (totalTax <= 0) {
            tvTotalTax.visibility = View.INVISIBLE
            tvTaxLabel.visibility = View.INVISIBLE
        }*/

        if (orderHistory.status == ConstVar.PAYMENT_STATUS_PENDING) {
            val content = orderHistory.qrCode ?: ConstVar.EMPTY_STRING

            if (content.isNotEmpty()) {
                val multiFormatWriter = MultiFormatWriter()
                try {
                    val bitMatrix =
                        multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200)
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
                    ivQr?.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }
            }
        }

        return view
    }

    companion object {
        fun getIntent(context: Context, bundle: Bundle): Intent {
            val intent = Intent(context, OrderHistoryDetailActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }
    }
}