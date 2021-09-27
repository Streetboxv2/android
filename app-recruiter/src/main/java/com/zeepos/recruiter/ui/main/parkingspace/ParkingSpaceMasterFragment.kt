package com.zeepos.recruiter.ui.main.parkingspace

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeepos.extension.hideKeyboard
import com.zeepos.models.entities.Menu
import com.zeepos.recruiter.R
import com.zeepos.recruiter.ui.main.form.FormParkingSpaceActivity
import com.zeepos.ui_base.ui.BaseFragment
import kotlinx.android.synthetic.main.landing_page.*

class ParkingSpaceMasterFragment : BaseFragment<ParkingSpaceMasterViewEvent, ParkingSpaceMasterViewModel>() {

    private lateinit var parkingSpaceMasterViewModel: ParkingSpaceMasterViewModel

    override fun initResourceLayout(): Int {
        return R.layout.landing_page
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ParkingSpaceMasterViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        btn_inputparkingspace.setOnClickListener{
            context?.let {
                startActivity(FormParkingSpaceActivity.getIntent(it))
            }
        }
    }

    override fun onEvent(useCase: ParkingSpaceMasterViewEvent) {
        dismissLoading()

    }

    companion object {
        fun newInstance(): ParkingSpaceMasterFragment {
            return ParkingSpaceMasterFragment()
        }
    }
}
