package com.zeepos.ui_base.views.clickbounce

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator


/**
 * Created by Arif S. on 2019-11-03
 */
interface ClickBounce {
    fun setScale(scale: Float): ClickBounce

    fun setScale(mode: ClickBounceAnim.Mode, scale: Float): ClickBounce

    fun setDurationPush(duration: Long): ClickBounce

    fun setDurationRelease(duration: Long): ClickBounce

    fun setInterpolatorPush(interpolatorPush: AccelerateDecelerateInterpolator): ClickBounce

    fun setInterpolatorRelease(interpolatorRelease: AccelerateDecelerateInterpolator): ClickBounce

    fun setOnClickListener(clickListener: View.OnClickListener): ClickBounce

    fun setOnLongClickListener(clickListener: View.OnLongClickListener): ClickBounce

    fun setOnTouchEvent(eventListener: View.OnTouchListener?): ClickBounce
}