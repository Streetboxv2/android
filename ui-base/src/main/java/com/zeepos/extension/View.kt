package com.zeepos.extension

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes

/**
 * Created by Arif S. on 5/12/20
 */

fun View.cancelTransition() {
    transitionName = null
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.GONE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

//fun ImageView.loadFromUrl(url: String) =
//    MyGlide.with(this.context.applicationContext)
//        .load(url)
//        .transition(DrawableTransitionOptions.withCrossFade())
//        .into(this)!!
//
//fun ImageView.loadUrlAndPostponeEnterTransition(url: String, activity: FragmentActivity) {
//    val target: Target<Drawable> = ImageViewBaseTarget(this, activity)
//    Glide.with(context.applicationContext).load(url).into(target)
//}
//
//private class ImageViewBaseTarget(var imageView: ImageView?, var activity: FragmentActivity?) :
//    BaseTarget<Drawable>() {
//    override fun removeCallback(cb: SizeReadyCallback?) {
//        imageView = null
//        activity = null
//    }
//
//    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>) {
//        imageView?.setImageDrawable(resource)
//        activity?.supportStartPostponedEnterTransition()
//    }
//
//    override fun onLoadFailed(errorDrawable: Drawable?) {
//        super.onLoadFailed(errorDrawable)
//        activity?.supportStartPostponedEnterTransition()
//    }
//
//    override fun getSize(cb: SizeReadyCallback) = cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL)
//}