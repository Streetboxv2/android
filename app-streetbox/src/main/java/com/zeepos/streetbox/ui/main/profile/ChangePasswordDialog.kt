package com.zeepos.streetbox.ui.main.profile

import android.os.Bundle
import android.view.View
import com.zeepos.streetbox.R
import com.zeepos.ui_base.ui.BaseDialogFragment
import kotlinx.android.synthetic.main.change_password.*

class ChangePasswordDialog : BaseDialogFragment(), View.OnClickListener {
    override fun initResourceLayout(): Int {
        return R.layout.change_password
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        val args = Bundle()
        args.putString("password", et_changePassword.text.toString())
        btn_changePassword.setOnClickListener {
            val dialogInterface = parentFragment as ChangePasswordListener
            dialogInterface.onChangePassword(et_changePassword.text.toString())
            dismiss()
        }
    }

    companion object {
        fun newInstance(): ChangePasswordDialog {
            return ChangePasswordDialog()
        }
    }

    override fun onClick(v: View?) {

    }

}