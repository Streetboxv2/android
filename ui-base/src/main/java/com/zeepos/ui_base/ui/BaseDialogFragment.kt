package com.zeepos.ui_base.ui

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.zeepos.ui_base.R
import com.zeepos.utilities.Utils

/**
 * Created by Arif S. on 6/15/20
 */
abstract class BaseDialogFragment : DialogFragment() {

    private lateinit var mView: View

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes

        if (activity != null)
            params?.width = Utils.getScreenWidth(activity!!) - 200
        else
            params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(
            context?.let { ContextCompat.getDrawable(it, R.drawable.dialog_default_bg) })
        mView = inflater.inflate(initResourceLayout(), container)
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onViewReady(savedInstanceState)
    }

    abstract fun initResourceLayout(): Int

    abstract fun onViewReady(savedInstanceState: Bundle?)
}