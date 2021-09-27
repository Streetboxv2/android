package com.zeepos.ui_base.views

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.button.MaterialButton
import com.tiagosantos.enchantedviewpager.EnchantedViewPagerAdapter
import com.zeepos.ui_base.R

/**
 * Created by Arif S. on 8/4/20
 */
class SlidingDrawableImageAdapter constructor(
    private val context: Context,
    private val images: List<Int>,
    private val itemChildClick: ItemChildClick
) : EnchantedViewPagerAdapter(images) {

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        if (images.isEmpty()) return Any()
        val itemPosition = position % images.size
        val imageLayout: View =
            inflater.inflate(R.layout.sliding_image_first_screen, view, false)
        val imageView =
            imageLayout.findViewById<ImageView>(R.id.image_view)
        val tvSkip = imageLayout.findViewById<TextView>(R.id.tv_skip)
        val tvTitle = imageLayout.findViewById<TextView>(R.id.tv_title)
        val tvDesc = imageLayout.findViewById<TextView>(R.id.tv_desc)
        val btnNext = imageLayout.findViewById<MaterialButton>(R.id.btn_next)

        if (position == 3) {
            btnNext.text = "Get Started"
        } else {
            btnNext.text = "Next"
        }

        when(position) {
            0 -> {
                tvTitle.text = "Nearby Food Truck"
                tvDesc.text = "Find your best dishes from your place"
            }
            1 -> {
                tvTitle.text = "No More Waiting"
                tvDesc.text = "Just order your dish and came when it's ready"
            }
            2 -> {
                tvTitle.text = "Cashless Payment"
                tvDesc.text = "We serve your payment without cash it's possible!"
            }
            3 -> {
                tvTitle.text = "Home Visit"
                tvDesc.text = "Bring food truck to your home for any occasion"
            }
        }

        btnNext.setOnClickListener {
            if (position == 3) {
                itemChildClick.onFinished()
            } else {
                itemChildClick.onNextClick(position)
            }

        }

        tvSkip.setOnClickListener {
            itemChildClick.onFinished()
        }

        imageView.setImageResource(images[position])

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

    interface ItemChildClick {
        fun onNextClick(currentPos: Int)
        fun onFinished()
    }

}