package id.streetbox.live.ui.main.home.nearby

import android.graphics.Color
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import com.zeepos.utilities.loadImageUrl
import id.streetbox.live.R

/**
 * Created by Arif S. on 6/14/20
 */
class NearByAdapter(data: MutableList<FoodTruck> = arrayListOf()) :
    BaseQuickAdapter<FoodTruck, BaseViewHolder>(
        R.layout.food_truck_item, data
    ), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: FoodTruck) {
        val imageView = holder.getView<ImageView>(R.id.iv_merchant_logo)

        holder.setText(R.id.tv_title, item.name)
        holder.setText(R.id.tv_description, item.address)
        holder.setText(R.id.tv_rating, "${item.rating}")
        holder.setText(R.id.tv_distance, "${item.distance} KM")
        holder.setText(R.id.tv_merchant_category, item.merchantCategory)

        if (item.categoryColor?.isNotEmpty()!!)
            holder.setBackgroundColor(
                R.id.tv_merchant_category,
                Color.parseColor(item.categoryColor)
            )

        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (item.logo != null) item.logo else ConstVar.EMPTY_STRING

        imageView.loadImageUrl(imageUrl, context)

        if (item.status == ConstVar.FOOD_TRUCK_STATUS_CHECK_IN) {
            holder.setGone(R.id.v_overly, true)
            isOverlay(true)
        } else {
            isOverlay(false)
            holder.setVisible(R.id.v_overly, true)
        }
    }

    fun isOverlay(overLay: Boolean): Boolean {
        return overLay
    }
}