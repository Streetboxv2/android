package com.zeepos.ui_base.views.clickbounce

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.zeepos.ui_base.views.clickbounce.ClickBounce
import com.zeepos.ui_base.views.clickbounce.ClickBounceAnim

/**
 * Created by Arif S. on 2019-11-03
 */
class ClickBounceAnimList(
    vararg views: View
) : ClickBounce {

    private val clickBounceAnimList = ArrayList<ClickBounceAnim>()

    init {
        for (view in views) {
            val clickBounceAnim = ClickBounceAnim.setAnimView(view)
            clickBounceAnim.setOnTouchEvent(null)
            this.clickBounceAnimList.add(clickBounceAnim)
        }


    }

    override fun setScale(scale: Float): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setScale(scale)
        }

        return this
    }

    override fun setScale(mode: ClickBounceAnim.Mode, scale: Float): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setScale(mode, scale)
        }

        return this
    }

    override fun setDurationPush(duration: Long): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setDurationPush(duration)
        }

        return this
    }

    override fun setDurationRelease(duration: Long): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setDurationRelease(duration)
        }

        return this
    }

    override fun setInterpolatorPush(interpolatorPush: AccelerateDecelerateInterpolator): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setInterpolatorPush(interpolatorPush)
        }

        return this
    }

    override fun setInterpolatorRelease(interpolatorRelease: AccelerateDecelerateInterpolator): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setInterpolatorRelease(interpolatorRelease)
        }

        return this
    }

    override fun setOnClickListener(clickListener: View.OnClickListener): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setOnClickListener(clickListener)
        }

        return this
    }

    override fun setOnLongClickListener(clickListener: View.OnLongClickListener): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            clickBounce.setOnLongClickListener(clickListener)
        }

        return this
    }

    override fun setOnTouchEvent(eventListener: View.OnTouchListener?): ClickBounce {
        for (clickBounce in clickBounceAnimList) {
            if (eventListener != null)
                clickBounce.setOnTouchEvent(eventListener)
        }

        return this
    }
}