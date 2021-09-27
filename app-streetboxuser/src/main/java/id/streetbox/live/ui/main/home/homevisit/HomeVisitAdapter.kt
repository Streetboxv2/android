package id.streetbox.live.ui.main.home.homevisit

import android.graphics.Color
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.master.FoodTruck
import com.zeepos.ui_base.views.GlideApp
import id.streetbox.live.R

/**
 * Created by Arif S. on 6/14/20
 */
class HomeVisitAdapter(data: MutableList<FoodTruck> = mutableListOf()) :
    BaseQuickAdapter<FoodTruck, BaseViewHolder>(
        R.layout.food_truck_home_visit_item, data
    ), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: FoodTruck) {
        val imageView = holder.getView<ImageView>(R.id.iv_banner)
        val imageUrl: String = ConstVar.PATH_IMAGE + item.banner

        holder.setText(R.id.tv_merchant_name, item.merchantName)

        if (item.category?.isNotEmpty()!!)
            holder.setText(R.id.tv_merchant_category, item.category)

        if (item.categoryColor?.isNotEmpty()!!)
            holder.setBackgroundColor(
                R.id.tv_merchant_category,
                Color.parseColor(item.categoryColor)
            )

        if (item.banner != null) {
            GlideApp.with(context)
                .load(imageUrl)
                .into(imageView)
        }
    }
}