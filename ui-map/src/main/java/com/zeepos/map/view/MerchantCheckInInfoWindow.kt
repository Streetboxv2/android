package com.zeepos.map.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.zeepos.map.R
import com.zeepos.models.master.FoodTruck
import com.zeepos.ui_base.views.GlideApp


/**
 * Created by Arif S. on 8/6/20
 */
class MerchantCheckInInfoWindow(private val context: Context, private val foodTruck: FoodTruck) :
    GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View? {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.info_window_merchant_check_in, null)

        val ivLogo = view.findViewById<ImageView>(R.id.iv_logo)

        val logo =
            "https://www.netclipart.com/pp/m/289-2898333_vegetarian-food-logo-png.png"//dummy

        GlideApp.with(context)
            .asBitmap()
            .load(logo)
            .override(200, 200)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    ivLogo.setImageBitmap(resource)
                }

            })

        return view
    }
}