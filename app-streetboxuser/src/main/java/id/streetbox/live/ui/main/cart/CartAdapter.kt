package id.streetbox.live.ui.main.cart

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.ProductSales
import id.streetbox.live.R
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.NumberUtil

/**
 * Created by Arif S. on 7/29/20
 */
class CartAdapter(data: MutableList<ProductSales> = mutableListOf()) :
    BaseQuickAdapter<ProductSales, BaseViewHolder>(
        R.layout.item_cart, data
    ) {

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
            .error(R.drawable.image_holder)
            .into(ivProduct)
    }

    override fun setOnItemChildClick(v: View, position: Int) {
        val productSales = getItem(position)

        when (v.id) {
            R.id.tv_increase -> {
                productSales.qty = productSales.qty + 1
                notifyDataSetChanged()
                super.setOnItemChildClick(v, position)
            }

            R.id.tv_decrease -> {
                if (productSales.qty > 0) {
                    productSales.qty = productSales.qty - 1
                }

                super.setOnItemChildClick(v, position)
                if (productSales.qty == 0) {
                    data.remove(productSales)
                }
            }
            R.id.iv_edit_note -> super.setOnItemChildClick(v, position)
        }
    }
}