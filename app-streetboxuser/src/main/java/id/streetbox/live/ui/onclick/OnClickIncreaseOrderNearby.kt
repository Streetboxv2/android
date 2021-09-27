package id.streetbox.live.ui.onclick

import android.widget.TextView
import com.example.dbroom.db.room.enitity.MenuItemStore

interface OnClickIncreaseOrderNearby {
    fun ClickIncrease(position: Int, product: MenuItemStore, value: Int, tvQty: TextView)
    fun ClickDecrease(position: Int, product: MenuItemStore, value: Int, tvQty: TextView)
}