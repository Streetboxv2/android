package com.zeepos.ui_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.zeepos.ui_base.ui.BaseActivity
import com.zeepos.ui_login.LoginActivity
import com.zeepos.ui_login.LoginViewEvent
import com.zeepos.ui_login.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

import javax.inject.Inject

class ForgotPasswordActivity : BaseActivity<ForgotPasswordViewEvent, ForgotPasswordViewModel>() {


    override fun initResourceLayout(): Int {
        return R.layout.activity_forgot_password
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            return intent
        }
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ForgotPasswordViewModel::class.java)

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        btn_submit.setOnClickListener {
               viewModel.resetPassword(et_username.text.toString())
        }
    }

    override fun onEvent(useCase: ForgotPasswordViewEvent) {
        when (useCase) {
            is ForgotPasswordViewEvent.GetPasswordSuccess ->
                    goToLoginActivity()
            is ForgotPasswordViewEvent.GetPasswordFailed -> {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun goToLoginActivity(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Please check you email")
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { p0, _ ->
            p0?.dismiss()
            startActivity(LoginActivity.getIntent(this))
        }
        alertDialogBuilder.show()

    }

}
