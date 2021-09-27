package com.streetbox.pos.ui.setting.printersetting

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.streetbox.pos.R
import com.zeepos.ui_base.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_printer_setting.*

/**
 * Created by Arif S. on 7/20/20
 */
class PrinterSettingFragment : BaseFragment<PrinterSettingViewEvent, PrinterSettingViewModel>() {

    private lateinit var printerSettingAdapter: PrinterSettingAdapter

    override fun initResourceLayout(): Int {
        return R.layout.fragment_printer_setting
    }

    override fun init() {
        viewModel =
            ViewModelProvider(this, viewModeFactory).get(PrinterSettingViewModel::class.java)

        printerSettingAdapter = PrinterSettingAdapter()

        viewModel.getAllPrinter()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initList()

        fab.setOnClickListener {
            showDialog(CreatePrinterDialog())
        }
    }

    override fun onEvent(useCase: PrinterSettingViewEvent) {
        when (useCase) {
            is PrinterSettingViewEvent.GetAllPrinterSuccess -> printerSettingAdapter.setList(useCase.data)
        }
    }

    private fun initList() {
        rv_item?.apply {

            val lm = LinearLayoutManager(context)
            layoutManager = lm
            adapter = printerSettingAdapter
        }

        printerSettingAdapter.setOnItemClickListener { adapter, view, position ->

        }
    }
}