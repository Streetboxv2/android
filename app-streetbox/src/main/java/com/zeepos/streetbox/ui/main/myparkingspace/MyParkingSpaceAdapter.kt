package com.zeepos.streetbox.ui.main.myparkingspace

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.models.ConstVar
import com.zeepos.models.transaction.ParkingSales
import com.zeepos.streetbox.R
import com.zeepos.ui_base.views.GlideApp

/**
 * Created by Arif S. on 5/21/20
 */
class MyParkingSpaceAdapter(data: MutableList<ParkingSales> = arrayListOf()) :
    BaseQuickAdapter<ParkingSales, BaseViewHolder>(R.layout.my_parking_space_item, data),
    LoadMoreModule {

    init {
        addChildClickViewIds(R.id.btn_live_monitor)
    }

    override fun convert(holder: BaseViewHolder, item: ParkingSales) {
        val ivImage = holder.getView<ImageView>(R.id.iv_parking)
        val tvHomeVisit = holder.getView<TextView>(R.id.tv_homevisit)
        val btnLiveMonitor = holder.getView<Button>(R.id.btn_live_monitor)
        val tvRating = holder.getView<TextView>(R.id.tv_rating)
        val ivRating = holder.getView<ImageView>(R.id.iv_rating)
        val tvDescription = holder.getView<TextView>(R.id.tv_description)
        val tvPlatNo = holder.getView<TextView>(R.id.tv_platno)
        val imageUrls = item.images ?: arrayListOf()
        val imageUrl: String =
            ConstVar.PATH_IMAGE + if (imageUrls.isNotEmpty()) item.images!![0] else ConstVar.EMPTY_STRING
        val imageProfPic: String =
            ConstVar.PATH_IMAGE + if (imageUrls.isNotEmpty()) item.profilePicture!! else ConstVar.EMPTY_STRING

        var imageUrlPic: String = ConstVar.EMPTY_STRING

        holder.setText(R.id.tv_title, item.name)

        if(item.trxVisitSalesId > 0 && item.tasksId > 0 ){
            if(item.isTracking == false){
                btnLiveMonitor.visibility = View.GONE
            }else{
                btnLiveMonitor.visibility = View.VISIBLE
            }
            tvPlatNo.visibility = View.VISIBLE
            tvPlatNo.text="B 8888 STB"
            tvPlatNo.text = item.platNo
            tvHomeVisit.visibility = View.VISIBLE
            tvRating.visibility = View.GONE
            ivRating.visibility = View.GONE
           imageUrlPic = imageProfPic
            tvDescription.visibility = View.GONE

        } else if(item.trxVisitSalesId > 0 && item.tasksId < 1 ){
            if(item.isTracking == false){
                btnLiveMonitor.visibility = View.GONE
            }else{
                btnLiveMonitor.visibility = View.VISIBLE
            }
            tvPlatNo.visibility = View.INVISIBLE
            tvHomeVisit.visibility = View.VISIBLE
            tvRating.visibility = View.GONE
            ivRating.visibility = View.GONE
            imageUrlPic = imageProfPic
            tvDescription.visibility = View.GONE

        } else if(item.trxVisitSalesId < 1 && item.tasksId > 0 ){
            if(item.isTracking == false){
                btnLiveMonitor.visibility = View.INVISIBLE
            }else{
                btnLiveMonitor.visibility = View.VISIBLE
            }
            tvPlatNo.visibility = View.VISIBLE
            tvPlatNo.text="B 8889 STB"
            tvPlatNo.text = item.platNo
            tvHomeVisit.visibility = View.GONE
            tvRating.visibility = View.VISIBLE
            ivRating.visibility = View.VISIBLE
            tvDescription.visibility = View.VISIBLE
            holder.setText(R.id.tv_rating, "${item.rating}")
            holder.setText(R.id.tv_description, "${item.description}")
            imageUrlPic = imageUrl
        }else if(item.tasksId < 1 && item.trxVisitSalesId < 1){
            if(item.isTracking == false){
                btnLiveMonitor.visibility = View.GONE
            }else{
                btnLiveMonitor.visibility = View.VISIBLE
            }

            tvHomeVisit.visibility = View.GONE
            tvRating.visibility = View.VISIBLE
            ivRating.visibility = View.VISIBLE
            holder.setText(R.id.tv_rating, "${item.rating}")
            tvDescription.visibility = View.VISIBLE
            holder.setText(R.id.tv_description, "${item.description}")
            imageUrlPic = imageUrl
        }

        if (imageUrl.isNotEmpty()) {
            GlideApp.with(context)
                .load(imageUrlPic)
                .apply(RequestOptions().placeholder(R.drawable.image_holder))
                .into(ivImage)
        }

    }

}