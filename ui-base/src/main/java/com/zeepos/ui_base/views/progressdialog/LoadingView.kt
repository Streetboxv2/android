package com.zeepos.ui_base.views.progressdialog

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.zeepos.models.ConstVar
import com.zeepos.ui_base.R
import com.zeepos.ui_base.ui.BaseDialogLoadingFragment
import com.zeepos.utilities.SharedPreferenceUtil

/**
 * Created by Arif S. on 5/16/20
 */
class LoadingView : BaseDialogLoadingFragment() {
    private lateinit var operatingAnim: Animation
    private lateinit var graduallyTextView: GraduallyTextView
    private lateinit var background: RelativeLayout
    private lateinit var mouse: View
    private var viewText: String? = null
    private var color = 0
    private var isClickCancelAble = true
    private var mainDialog: Dialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val appType: String = SharedPreferenceUtil.getString(
            requireContext(),
            ConstVar.APP_TYPE,
            ConstVar.EMPTY_STRING
        )
            ?: ConstVar.EMPTY_STRING

        mainDialog = Dialog(requireActivity(), R.style.loading_dialog).apply {
            setContentView(R.layout.loading)
            setCanceledOnTouchOutside(isClickCancelAble)
            window?.setGravity(Gravity.CENTER)
        }

        operatingAnim = RotateAnimation(
            360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        operatingAnim.repeatCount = Animation.INFINITE
        operatingAnim.duration = 2000
        val lin = LinearInterpolator()
        operatingAnim.interpolator = lin
        mainDialog?.window?.decorView?.let { view ->
            background = view.findViewById(R.id.background)
            if (color != 0) {
                background.setBackgroundColor(color)
            }
            mouse = view.findViewById(R.id.mouse)
            graduallyTextView =
                view.findViewById<View>(R.id.graduallyTextView) as GraduallyTextView
            if (!TextUtils.isEmpty(viewText)) {
                graduallyTextView.setText(viewText)
            }

            when (appType) {
                ConstVar.APP_MERCHANT -> {
                    background.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.background)
                }
                ConstVar.APP_CUSTOMER -> {
                    background.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.loading_background_customer
                    )
                }
                else -> {
                    background.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.loading_background_pos)
                }
            }
        }
        return mainDialog!!
    }

    override fun onResume() {
        super.onResume()
        mouse.animation = operatingAnim
        graduallyTextView.startLoading()
    }

    override fun onPause() {
        operatingAnim.cancel()
        mouse.clearAnimation()
        graduallyTextView.stopLoading()
        super.onPause()
    }

    override fun onDestroyView() {
        if (mainDialog?.isShowing == true) {
            mainDialog?.dismiss()
            mainDialog = null
        }
        super.onDestroyView()
    }

    fun isShowing(): Boolean {
        return mainDialog?.isShowing == true
    }

    fun setText(str: String?) {
        viewText = str
    }

    fun setClickCancelAble(bo: Boolean) {
        isClickCancelAble = bo
    }

    fun setBackgroundColor(color: Int) {
        this.color = color
    }
}