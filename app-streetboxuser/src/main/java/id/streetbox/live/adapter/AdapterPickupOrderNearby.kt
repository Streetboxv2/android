package id.streetbox.live.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zeepos.models.ConstVar
import com.zeepos.utilities.*
import id.streetbox.live.R
import com.example.dbroom.db.room.enitity.MenuItemStore
import id.streetbox.live.ui.onclick.OnClickIncreaseOrderNearby
import kotlinx.android.synthetic.main.item_product.view.*

class AdapterPickupOrderNearby(
    val mutableListProductSales: MutableList<MenuItemStore>,
    val isAddQty: Boolean,
    val onClickIncreaseOrder: OnClickIncreaseOrderNearby?
) :
    RecyclerView.Adapter<AdapterPickupOrderNearby.ViewHolder>() {
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
//                val priceDisplay = if (item.price > 0) {
//                    val priceDisc = item.price - (item.price * item.discount / 100)
//                    NumberUtil.formatToStringWithoutDecimal(priceDisc)
//                } else {
//                    NumberUtil.formatToStringWithoutDecimal(item.price)
//                }

//                if (item.discount > 0) {
//                    showView(tv_price_before_discount)
//                    tv_price_before_discount.text = priceBeforeDiscount
//                    tv_price_before_discount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
//                } else {
//                    hideView(tv_price_before_discount)
//                }


                tv_qty.text = item.qty.toString()
                tv_name.text = item.title
                tv_price.text = ConvertToRupiah.toRupiah("", item.price.toString(), false)

                val imageUrl: String =
                    ConstVar.PATH_IMAGE + if (item.image != null)
                        item.image else ConstVar.EMPTY_STRING

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
                    if (value == 0)
                        onClickIncreaseOrder?.ClickDecrease(position, item, value, tv_qty)
                    else if (value == -1) {
                        tv_qty.text = "0"
                    } else onClickIncreaseOrder?.ClickDecrease(position, item, value, tv_qty)

                }

            }
        }
    }

    override fun getItemCount(): Int {
        return mutableListProductSales.size
    }

    fun removeItem(menuItemStore: MenuItemStore, position: Int) {
        mutableListProductSales.remove(menuItemStore)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }
}