package com.zeepos.recruiter.ui.main.profile

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.zeepos.recruiter.R
import com.zeepos.ui_base.ui.BaseFragment
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : BaseFragment<ProfileViewEvent, ProfileViewModel>() {
    override fun initResourceLayout(): Int {
        return R.layout.profile_fragment
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ProfileViewModel::class.java)
        viewModel.getProfile()

    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        val user = viewModel.getUserLocal()
        et_nama.setText(user!!.name)
        if (user.address == null) {
            user.address = ""
        }
        et_alamat.setText(user.address)
        et_nohp.setText(user.phone)

        btn_logout.setOnClickListener {
            viewModel.deleteSession()
//            activity?.let { it1 -> loginUiEvent.goToLoginActivityScreen(it1) }

        }

        btn_changepassword.setOnClickListener {
            viewModel.changePassword(et_pwd.text.toString())
        }
    }

    override fun onEvent(useCase: ProfileViewEvent) {

        when (useCase) {
            is ProfileViewEvent.GetProfileSuccess -> Toast.makeText(
                activity,
                "Success",
                Toast.LENGTH_LONG
            ).show()
            is ProfileViewEvent.GetProfileFailed -> {
                Toast.makeText(activity, useCase.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}