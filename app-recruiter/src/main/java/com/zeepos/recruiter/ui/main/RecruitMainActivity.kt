package com.zeepos.recruiter.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zeepos.recruiter.R
import com.zeepos.recruiter.ui.main.parkingspace.ParkingSpaceMasterFragment
import com.zeepos.recruiter.ui.main.profile.ProfileFragment
import com.zeepos.ui_base.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*


class RecruitMainActivity : BaseActivity<RecruitMainViewEvent, RecruitMainViewModel>() {

    override fun initResourceLayout(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(RecruitMainViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        addFragment(ParkingSpaceMasterFragment.newInstance(), R.id.fl_content)
        nav_view.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }

    private var navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_parkingspace -> {
                        replaceFragment(ParkingSpaceMasterFragment.newInstance(), R.id.fl_content)
                        return true
                    }

                    R.id.navigation_profile -> {
                        replaceFragment(ProfileFragment.newInstance(), R.id.fl_content)
                        return true
                    }
                }
                return false
            }
        }

    override fun onEvent(useCase: RecruitMainViewEvent) {
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, RecruitMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

}
