package id.streetbox.live.ui.onclick

import android.widget.TextView
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.ProductSales

interface OnClickIncreaseOrder {
    fun ClickIncrease(position: Int, product: ProductSales, value: Int, tvQty: TextView)
    fun ClickDecrease(position: Int, product: ProductSales, value: Int, tvQty: TextView)
}