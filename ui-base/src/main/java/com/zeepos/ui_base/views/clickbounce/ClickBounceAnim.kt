package com.zeepos.ui_base.views.clickbounce

import android.animation.*
import android.graphics.Rect
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import java.lang.ref.WeakReference


/**
 * Created by Arif S. on 2019-11-03
 */
class ClickBounceAnim(
    private val view: View,
    private val defaultScale: Float = view.scaleX,
    private val weakView: WeakReference<View> = WeakReference(view)
) : ClickBounce {

    enum class Mode {
        MODE_SCALE,
        MODE_STATIC_DP
    }

    private var mode = Mode.MODE_SCALE
    private var pushScale = DEFAULT_PUSH_SCALE
    private var pushStatic = DEFAULT_PUSH_STATIC
    private var durationPush = DEFAULT_PUSH_DURATION
    private var durationRelease = DEFAULT_RELEASE_DURATION
    private var interpolatorPush = DEFAULT_INTERPOLATOR
    private var interpolatorRelease = DEFAULT_INTERPOLATOR
    private lateinit var scaleAnimSet: AnimatorSet

    init {
        weakView.get()?.isClickable = true
    }


    companion object {
        fun setAnimView(view: View): ClickBounceAnim {
            val clickBounceAnim = ClickBounceAnim(view)
            clickBounceAnim.setOnTouchEvent(null)
            return clickBounceAnim
        }

        fun setAnimView(vararg views: View): ClickBounceAnimList {
            return ClickBounceAnimList(*views)
        }

        val DEFAULT_INTERPOLATOR = AccelerateDecelerateInterpolator()
        val DEFAULT_PUSH_SCALE = 0.97f
        val DEFAULT_PUSH_STATIC = 2f
        val DEFAULT_PUSH_DURATION: Long = 50
        val DEFAULT_RELEASE_DURATION: Long = 125
    }

    override fun setScale(scale: Float): ClickBounce {
        if (mode == Mode.MODE_SCALE) {
            pushScale = scale
        } else if (mode == Mode.MODE_STATIC_DP) {
            pushStatic = scale
        }
        return this
    }

    override fun setScale(mode: Mode, scale: Float): ClickBounce {
        this.mode = mode
        this.setScale(scale)
        return this
    }

    override fun setDurationPush(duration: Long): ClickBounce {
        this.durationPush = duration
        return this
    }

    override fun setDurationRelease(duration: Long): ClickBounce {
        this.durationRelease = duration
        return this
    }

    override fun setInterpolatorPush(interpolatorPush: AccelerateDecelerateInterpolator): ClickBounce {
        this.interpolatorPush = interpolatorPush
        return this
    }

    override fun setInterpolatorRelease(interpolatorRelease: AccelerateDecelerateInterpolator): ClickBounce {
        this.interpolatorRelease = interpolatorRelease
        return this
    }

    override fun setOnClickListener(clickListener: View.OnClickListener): ClickBounce {
        weakView.get()?.setOnClickListener(clickListener)
        return this
    }

    override fun setOnLongClickListener(clickListener: View.OnLongClickListener): ClickBounce {
        weakView.get()?.setOnLongClickListener(clickListener)
        return this
    }

    override fun setOnTouchEvent(eventListener: View.OnTouchListener?): ClickBounce {
        if (eventListener == null) {
            weakView.get()?.setOnTouchListener(object : View.OnTouchListener {
                var isOutSide: Boolean = false
                var rect: Rect? = null

                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                    if (view.isClickable) {
                        val i = motionEvent.action
                        if (i == MotionEvent.ACTION_DOWN) {
                            isOutSide = false
                            rect = Rect(
                                view.left,
                                view.top,
                                view.right,
                                view.bottom
                            )
                            makeDecisionAnimScale(
                                view,
                                mode,
                                pushScale,
                                pushStatic,
                                durationPush,
                                interpolatorPush,
                                i
                            )
                        } else if (i == MotionEvent.ACTION_MOVE) {
                            if (rect != null
                                && !isOutSide
                                && !rect!!.contains(
                                    view.left + motionEvent.x.toInt(),
                                    view.top + motionEvent.y.toInt()
                                )
                            ) {
                                isOutSide = true
                                makeDecisionAnimScale(
                                    view,
                                    mode,
                                    defaultScale,
                                    0f,
                                    durationRelease,
                                    interpolatorRelease,
                                    i
                                )
                            }
                        } else if (i == MotionEvent.ACTION_CANCEL || i == MotionEvent.ACTION_UP) {
                            makeDecisionAnimScale(
                                view,
                                mode,
                                defaultScale,
                                0f,
                                durationRelease,
                                interpolatorRelease,
                                i
                            )
                        }
                    }
                    return false
                }
            })

        } else {
            weakView.get()?.setOnTouchListener { v, motionEvent ->
                eventListener.onTouch(weakView.get(), motionEvent)
            }
        }

        return this
    }

    private fun makeDecisionAnimScale(
        view: View,
        mode: Mode,
        pushScale: Float,
        pushStatic: Float,
        duration: Long,
        interpolator: TimeInterpolator,
        action: Int
    ) {
        var tmpScale = pushScale
        if (mode == Mode.MODE_STATIC_DP) {
            tmpScale = getScaleFromStaticSize(pushStatic)
        }
        animScale(view, tmpScale, duration, interpolator)
    }

    private fun animScale(
        view: View,
        scale: Float,
        duration: Long,
        interpolator: TimeInterpolator
    ) {

        view.animate().cancel()
        if (::scaleAnimSet.isInitialized)
            scaleAnimSet.cancel()

        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale)
        scaleX.interpolator = interpolator
        scaleX.duration = duration
        scaleY.interpolator = interpolator
        scaleY.duration = duration

        scaleAnimSet = AnimatorSet()
        scaleAnimSet
            .play(scaleX)
            .with(scaleY)
        scaleX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        })
        scaleX.addUpdateListener {
            val p = view.parent as View
            p?.invalidate()
        }
        scaleAnimSet.start()
    }

    private fun getScaleFromStaticSize(sizeStaticDp: Float): Float {
        if (sizeStaticDp <= 0) return defaultScale

        val sizePx = dpToPx(sizeStaticDp)
        if (getViewWidth() > getViewHeight()) {
            if (sizePx > getViewWidth()) return 1.0f
            val pushWidth = getViewWidth() - sizePx * 2
            return pushWidth / getViewWidth()
        } else {
            if (sizePx > getViewHeight()) return 1.0f
            val pushHeight = getViewHeight() - sizePx * 2
            return pushHeight / getViewHeight().toFloat()
        }
    }

    private fun getViewHeight(): Int {
        return weakView.get()?.measuredHeight ?: 0
    }

    private fun getViewWidth(): Int {
        return weakView.get()?.measuredWidth ?: 0
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            weakView.get()?.resources?.displayMetrics
        )
    }
}