package id.streetbox.live.ui.orderreview.pickup

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.NumberUtil
import id.streetbox.live.R

/**
 * Created by Arif S. on 7/25/20
 */
class PickUpOrderReviewAdapter(
    data: MutableList<ProductSales> = mutableListOf()
) :
    BaseQuickAdapter<ProductSales, BaseViewHolder>(
        R.layout.item_pickup_order_review, data
    ) {

    var onClickIncrease: OnClickIncreasePickup? = null

    init {
        addChildClickViewIds(R.id.tv_increase, R.id.tv_decrease, R.id.iv_edit_note)
    }

    override fun convert(holder: BaseViewHolder, item: ProductSales) {
        val ivProduct = holder.getView<ImageView>(R.id.iv_product)
        val imageUrl: String = ConstVar.PATH_IMAGE + item.photo

        holder.setText(R.id.tv_name, item.name)
        holder.setText(R.id.tv_price, NumberUtil.formatToStringWithoutDecimal(item.price))
        holder.setText(R.id.tv_qty, item.qty.toString())

        GlideApp.with(context)
            .load(imageUrl)
            .into(ivProduct)

        val tvIncrease = holder.getView<TextView>(R.id.tv_increase)
        val tvDecrease = holder.getView<TextView>(R.id.tv_decrease)
        val ivEditNote = holder.getView<ImageView>(R.id.iv_edit_note)
        val getQtyText = holder.getView<TextView>(R.id.tv_qty)

        tvIncrease.setOnClickListener {
            val value = getQtyText.text.toString().toInt() + 1
            if (value > item.qtyProduct) {

            } else {
                item.qty = item.qty + 1
                onClickIncrease?.ClickIncrease(item, value, getQtyText)
                notifyDataSetChanged()
            }
        }

        tvDecrease.setOnClickListener {
            val value = getQtyText.text.toString().toInt() - 1
            if (item.qty > 0) {
                item.qty = item.qty - 1
            }
            if (item.qty == 0) {
                data.remove(item)
            }
            onClickIncrease?.ClickDecrease(item, value, getQtyText)
        }

        ivEditNote.setOnClickListener {
            onClickIncrease?.ClickTvNoted(item)
        }

    }

//    override fun setOnItemChildClick(v: View, position: Int) {
//        val productSales = getItem(position)
//        when (v.id) {
//            R.id.tv_increase -> {
//                val getIncrese =
//                productSales.qty = productSales.qty + 1
//                println("respon Product Qty ${productSales.qty}")
//                notifyDataSetChanged()
//                super.setOnItemChildClick(v, position)
//            }
//
//            R.id.tv_decrease -> {
//                if (productSales.qty > 0) {
//                    productSales.qty = productSales.qty - 1
//                }
//
//                super.setOnItemChildClick(v, position)
//                if (productSales.qty == 0) {
//                    data.remove(productSales)
//                }
//            }
//            R.id.iv_edit_note -> super.setOnItemChildClick(v, position)
//        }
//    }

    interface OnClickIncreasePickup {
        fun ClickIncrease(productSales: ProductSales, value: Int, tvQty: TextView)
        fun ClickDecrease(productSales: ProductSales, value: Int, tvQty: TextView)
        fun ClickTvNoted(productSales: ProductSales)
    }
}