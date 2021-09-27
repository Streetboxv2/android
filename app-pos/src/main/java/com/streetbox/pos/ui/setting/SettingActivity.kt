package com.streetbox.pos.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.streetbox.pos.R
import com.streetbox.pos.ui.setting.printersetting.PrinterSettingFragment
import com.zeepos.models.entities.ListData
import com.zeepos.ui_base.ui.BaseActivity
import kotlinx.android.synthetic.main.setting_list.*

/**
 * Created by Arif S. on 7/20/20
 */
class SettingActivity : BaseActivity<SettingViewEvent, SettingViewModel>() {
    override fun initResourceLayout(): Int {
        return R.layout.activity_setting
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(SettingViewModel::class.java)
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        addFragment(
            PrinterSettingFragment(),
            R.id.fl_detail,
            PrinterSettingFragment::class.java.simpleName
        )

        initList()
    }

    override fun onEvent(useCase: SettingViewEvent) {
    }

    private fun initList() {
        rcv_setting?.apply {
            val settingData: MutableList<ListData> = mutableListOf()
            settingData.add(ListData("Printer", R.drawable.ic_lock))
            settingData.add(ListData("Email", R.drawable.ic_email))

            val settingAdapter = SettingAdapter(settingData)
            val lm = LinearLayoutManager(context)
            layoutManager = lm
            adapter = settingAdapter
            val dividerItemDecoration = DividerItemDecoration(
                context,
                lm.orientation
            )
            addItemDecoration(dividerItemDecoration)

            settingAdapter.setOnItemClickListener { _, _, position ->
                when (position) {
                    0 -> {
                        replaceFragment(
                            PrinterSettingFragment(),
                            R.id.fl_detail,
                            PrinterSettingFragment::class.java.simpleName
                        )

                        settingAdapter.selectedPosition = position
                        settingAdapter.notifyDataSetChanged()
                    }
                    1 -> {
                        settingAdapter.selectedPosition = position
                        settingAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, SettingActivity::class.java)
            return intent
        }
    }
}