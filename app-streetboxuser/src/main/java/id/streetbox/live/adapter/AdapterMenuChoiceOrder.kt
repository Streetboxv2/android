package id.streetbox.live.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.ProductSales
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.hideView
import com.zeepos.utilities.loadImageUrl
import com.zeepos.utilities.showView
import id.streetbox.live.R
import id.streetbox.live.ui.onclick.OnClickIncreaseOrder
import kotlinx.android.synthetic.main.item_product.view.*

class AdapterMenuChoiceOrder(
    val mutableListProductSales: List<ProductSales>,
    val isAddQty: Boolean,
    val onClickIncreaseOrder: OnClickIncreaseOrder?
) :
    RecyclerView.Adapter<AdapterMenuChoiceOrder.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_menu_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mutableListProductSales[position]
        holder.apply {
            itemView.apply {
                val priceBeforeDiscount = NumberUtil.formatToStringWithoutDecimal(item.priceOriginal - (item.priceOriginal * item.discount / 100))
                val priceDisplay = item.priceOriginal.toString()
                if (item.discount > 0) {
                    showView(tv_price_before_discount)
                    tv_price_before_discount.text = priceDisplay
                    tv_price_before_discount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    hideView(tv_price_before_discount)
                }


                tv_name.text = item.name
                tv_price.text = priceBeforeDiscount

                val imageUrl: String =
                    ConstVar.PATH_IMAGE + if (item.photo != null) item.photo else ConstVar.EMPTY_STRING

                iv_product.loadImageUrl(imageUrl, context)

                if (isAddQty) {
                    showView(ll_qty)
                } else hideView(ll_qty)

                tv_increase.setOnClickListener {
                    val value = tv_qty.text.toString().toInt() + 1
                    onClickIncreaseOrder?.ClickIncrease(position, item, value, tv_qty)
                }

                tv_decrease.setOnClickListener {
                    val value = tv_qty.text.toString().toInt() - 1
                    onClickIncreaseOrder?.ClickDecrease(position, item, value, tv_qty)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return mutableListProductSales.size
    }
}