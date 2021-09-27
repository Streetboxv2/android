package com.zeepos.ui_base.views.alertdialog

import android.app.Dialog
import android.content.Context
import com.zeepos.ui_base.R

/**
 * Created by Arif S. on 6/26/20
 */
class SweetAlertDialog(context: Context, private val alertType: Int = 0) :
    Dialog(context, R.style.alert_dialog) {

    val NORMAL_TYPE = 0
    val ERROR_TYPE = 1
    val SUCCESS_TYPE = 2
    val WARNING_TYPE = 3

    init {
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }
}