package id.streetbox.live.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeepos.models.ConstVar
import com.zeepos.models.master.Product
import com.zeepos.models.transaction.ProductSales
import com.zeepos.utilities.NumberUtil
import com.zeepos.utilities.hideView
import com.zeepos.utilities.loadImageUrl
import com.zeepos.utilities.showView
import id.streetbox.live.R
import com.example.dbroom.db.room.enitity.MenuItemStore
import kotlinx.android.synthetic.main.item_product.view.*

class AdapterMenu(
    val data: MutableList<Product> = mutableListOf(),
    val types: String,
    val getSaveListMenu: List<MenuItemStore>?
) :
    RecyclerView.Adapter<AdapterMenu.MyViewHolder>() {

    var onClickIncrease: OnClickIncrease? = null
    private val productSalesMap: HashMap<Long, ProductSales> = hashMapOf()

    @SuppressLint("ViewConstructor")
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.apply {
            itemView.apply {
                val productSales = productSalesMap[item.id]
                val priceBeforeDiscount = NumberUtil.formatToStringWithoutDecimal(item.price)
                val priceDisplay = if (item.priceAfterDiscount > 0) {
                    val priceDisc = item.price - (item.price * item.discount / 100)
                    NumberUtil.formatToStringWithoutDecimal(priceDisc)
                } else {
                    NumberUtil.formatToStringWithoutDecimal(item.price)
                }

                if (item.discount > 0) {
                    showView(tv_price_before_discount)
                    tv_price_before_discount.text = priceBeforeDiscount
                    tv_price_before_discount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    hideView(tv_price_before_discount)
                }

                if (productSales != null) {
                    tv_qty.text = productSales.qty.toString()
                } else tv_qty.text = "0"


                tv_name.text = item.name
                tv_price.text = priceDisplay
                tv_description.text = item.description

                if (types.equals("homevisit", ignoreCase = true)) {
                    hideView(iv_edit_note)
                }

                val imageUrl: String =
                    ConstVar.PATH_IMAGE + if (item.photo != null) item.photo else ConstVar.EMPTY_STRING

                iv_product.loadImageUrl(imageUrl, context)

                tv_increase.setOnClickListener {
                    val value = tv_qty.text.toString().toInt() + 1
                    onClickIncrease?.ClickIncrease(position, item, value, tv_qty)
                }

                tv_decrease.setOnClickListener {
                    val value = tv_qty.text.toString().toInt() - 1
                    if (value == 0)
                        onClickIncrease?.ClickDecrease(position, item, value, tv_qty)
                    else if (value == -1) {
                        tv_qty.text = "0"
                    } else onClickIncrease?.ClickDecrease(position, item, value, tv_qty)

                }

                iv_edit_note.setOnClickListener {
                    onClickIncrease?.ClickTvNoted(position, item)
                }

                getSaveListMenu?.forEach {
                    if (it.id == item.id.toInt())
                        tv_qty.text = it.qty.toString()
                }
            }
        }
    }

    interface OnClickIncrease {
        fun ClickIncrease(position: Int, product: Product, value: Int, tvQty: TextView)
        fun ClickDecrease(position: Int, product: Product, value: Int, tvQty: TextView)
        fun ClickTvNoted(position: Int, product: Product)
    }

    fun setProDuctSalesMap(productSalesList: List<ProductSales>) {
        productSalesMap.clear()
        productSalesList.forEach {
            productSalesMap[it.productId] = it
            println("respon angka ${it.qty}")
        }
        notifyDataSetChanged()
    }
}