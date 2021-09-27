package id.streetbox.live.adapter

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.zeepos.models.entities.OrderDetail
import id.streetbox.live.R
import kotlinx.android.synthetic.main.layout_item_menu_detailorder.view.*

class AdapterListMenuDetailOrderNearby(val menuItem: OrderDetail) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.apply {
                tvItemMenuOrder.text = menuItem.productName
                tvQtyMenuOrder.text = "Quantity : " + menuItem.qty.toString()
            }
        }
    }

    override fun getLayout(): Int = R.layout.layout_item_menu_detailorder
}