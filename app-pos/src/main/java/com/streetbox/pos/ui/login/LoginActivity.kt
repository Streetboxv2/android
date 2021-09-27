package com.streetbox.pos.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.MainActivity
import com.streetbox.pos.ui.main.onlineorder.OnlineOrderActivity
import com.zeepos.ui_base.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_login_pos.*
import javax.inject.Inject

/**
 * Created by Arif S. on 5/1/20
 */
class LoginActivity : BaseActivity<LoginViewEvent, LoginViewModel>() {

    @Inject
    lateinit var gson: Gson

    override fun initResourceLayout(): Int {
        return R.layout.activity_login_pos
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(LoginViewModel::class.java)
        viewModel.checkIsLoggedIn()

    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        btn_login.setOnClickListener {
            val username = et_username.text.toString()
            val password = et_password.text.toString()

            showLoading()
            viewModel.login(username.trim(), password)
        }

    }

    override fun onEvent(useCase: LoginViewEvent) {
        when (useCase) {
            LoginViewEvent.GoToMainScreen ->
                startActivity(MainActivity.getIntent(this))
            LoginViewEvent.GoToOperatorMainScreen -> {
            }
            is LoginViewEvent.LoginFailed -> {

                if (useCase.errorMessage == "Http 401 Unauthorized") {
                    startActivity(MainActivity.getIntent(this))
                }else if (useCase.errorMessage == "java.lang.Throwable: No data found!") {
                    startActivity(MainActivity.getIntent(this))
                }
                else {
                    Toast.makeText(
                        applicationContext,
                        useCase.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            LoginViewEvent.DismissLoading -> dismissLoading()
        }

        dismissLoading()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }



}
