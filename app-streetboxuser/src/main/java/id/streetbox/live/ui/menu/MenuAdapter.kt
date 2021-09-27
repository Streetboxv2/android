package id.streetbox.live.ui.menu

import android.graphics.Paint
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.orhanobut.hawk.Hawk
import com.zeepos.models.ConstVar
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.ProductSales
import com.zeepos.ui_base.views.GlideApp
import com.zeepos.utilities.NumberUtil
import id.streetbox.live.R

/**
 * Created by Arif S. on 6/24/20
 */
class MenuAdapter(data: MutableList<Product> = mutableListOf()) :
    BaseQuickAdapter<Product, BaseViewHolder>(R.layout.item_product, data) {

    private val productSalesMap: HashMap<Long, ProductSales> = hashMapOf()

    init {
        addChildClickViewIds(R.id.tv_increase, R.id.tv_decrease, R.id.iv_edit_note)
    }

    var validationQty: ValidationQty? = null
    override fun convert(holder: BaseViewHolder, item: Product) {
        val productSales = productSalesMap[item.id]
        val priceBeforeDiscount = NumberUtil.formatToStringWithoutDecimal(item.price)
        val priceDisplay = if (item.priceAfterDiscount > 0) {
            val priceDisc = item.price - (item.price * item.discount / 100)
            NumberUtil.formatToStringWithoutDecimal(priceDisc)
        } else {
            NumberUtil.formatToStringWithoutDecimal(item.price)
        }

        if (item.discount > 0) {
            holder.setVisible(R.id.tv_price_before_discount, true)
            holder.setText(R.id.tv_price_before_discount, priceBeforeDiscount)
            val tv = holder.getView<TextView>(R.id.tv_price_before_discount)
            tv.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        } else {
            holder.setGone(R.id.tv_price_before_discount, true)
        }

        holder.setText(R.id.tv_name, item.name)
        holder.setText(R.id.tv_price, priceDisplay)
        holder.setText(R.id.tv_description, item.description)

        val getTypes = Hawk.get<String>("types")

        if (getTypes.equals("homevisit", ignoreCase = true)) {
            holder.setGone(R.id.iv_edit_note, true)
        }

        if (productSales != null) {
            val tvQty = holder.getView<TextView>(R.id.tv_qty)
            if (productSales.qty > item.qty) {
                println("respon ")
            } else {
                holder.setText(R.id.tv_qty, productSales.qty.toString())
                validationQty?.validation(tvQty, item)
            }
        } else holder.setText(R.id.tv_qty, "0")

        val ivProduct = holder.getView<ImageView>(R.id.iv_product)
        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (item.photo != null) item.photo else ConstVar.EMPTY_STRING

        GlideApp.with(context)
            .load(imageUrl)
            .into(ivProduct)

    }

    fun setProDuctSalesMap(productSalesList: List<ProductSales>) {
        productSalesMap.clear()
        productSalesList.forEach {
            productSalesMap[it.productId] = it
            println("respon angka ${it.qty}")
        }
        notifyDataSetChanged()
    }

    interface ValidationQty {
        fun validation(tvQty: TextView, product: Product)
    }

}