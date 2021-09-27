package id.streetbox.live.ui.editprofile

import android.os.Bundle
import android.view.View
import com.zeepos.ui_base.ui.BaseDialogFragment
import id.streetbox.live.R
import kotlinx.android.synthetic.main.change_password.*

/**
 * Created by Arif S. on 8/13/20
 */
class ChangePasswordDialog : BaseDialogFragment(), View.OnClickListener {
    override fun initResourceLayout(): Int {
        return R.layout.change_password
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        val args = Bundle()
        args.putString("password", et_changePassword.text.toString())
//        btn_changePassword.setOnClickListener {
//            val dialogInterface = parentFragment as ChangePasswordListener
//            dialogInterface.onChangePassword(et_changePassword.text.toString())
//            dismiss()
//        }
    }

    companion object {
        fun newInstance(): ChangePasswordDialog {
            return ChangePasswordDialog()
        }
    }

    override fun onClick(p0: View?) {
    }

    interface ChangePasswordListener {
        fun onChangePassword(password: String)
    }
}