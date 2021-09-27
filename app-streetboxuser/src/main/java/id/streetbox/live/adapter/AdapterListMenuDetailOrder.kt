package id.streetbox.live.adapter

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.zeepos.models.entities.MenuItemOrder
import id.streetbox.live.R
import kotlinx.android.synthetic.main.layout_item_menu_detailorder.view.*

class AdapterListMenuDetailOrder(val menuItem: MenuItemOrder) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.apply {
                tvItemMenuOrder.text = menuItem.name
                tvQtyMenuOrder.text = "Quantity : " + menuItem.quantity.toString()
            }
        }
    }

    override fun getLayout(): Int = R.layout.layout_item_menu_detailorder
}