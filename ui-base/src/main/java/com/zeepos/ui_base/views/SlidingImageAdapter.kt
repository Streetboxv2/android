package com.zeepos.ui_base.views

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.R

/**
 * Created by Arif S. on 5/20/20
 */
class SlidingImageAdapter constructor(
    private val context: Context,
    private val images: List<String>
) : EnchantedViewPagerAdapter(images) {

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        if (images.isEmpty()) return Any()
        val itemPosition = position % images.size
        val imageLayout: View =
            inflater.inflate(R.layout.sliding_image_item, view, false)
        val imageView =
            imageLayout.findViewById<ImageView>(R.id.iv_pict)

        val imageUrl: String = ConstVar.PATH_IMAGE + images[itemPosition]

        if (imageUrl.isNotEmpty()) {
            GlideApp.with(context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                )
                .into(imageView)
        }

        view.addView(imageLayout)
        return imageLayout
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view == `object`
    }

    override fun restoreState(
        state: Parcelable?,
        loader: ClassLoader?
    ) {
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

}