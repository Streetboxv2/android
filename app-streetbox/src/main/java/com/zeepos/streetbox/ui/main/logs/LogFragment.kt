package com.zeepos.streetbox.ui.main.logs

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeepos.streetbox.R
import com.zeepos.ui_base.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_logs.*


/**
 * Created by Arif S. on 7/1/20
 */
class LogFragment : BaseFragment<LogViewEvent, LogViewModel>() {

    private lateinit var logsAdapter: LogsAdapter

    override fun initResourceLayout(): Int {
        return R.layout.fragment_logs
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(LogViewModel::class.java)
        logsAdapter = LogsAdapter()
        viewModel.getLogs()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        initList()
    }

    private fun initList() {
        rcv?.apply {
            val lm = LinearLayoutManager(context)
            layoutManager = lm
            adapter = logsAdapter
            val dividerItemDecoration = DividerItemDecoration(
                context,
                lm.orientation
            )
            addItemDecoration(dividerItemDecoration)
        }

        logsAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getLogs(true)
        }
    }

    override fun onEvent(useCase: LogViewEvent) {
        when (useCase) {
            is LogViewEvent.GetLogSuccess -> {
                if (useCase.data.isNotEmpty()) {
                    logsAdapter.addData(useCase.data)
                    logsAdapter.loadMoreModule.loadMoreComplete()
                } else {
                    logsAdapter.loadMoreModule.loadMoreEnd(true)
                }
            }
            LogViewEvent.GetLogsFailed -> logsAdapter.loadMoreModule.loadMoreFail()
        }
    }

    companion object {
        fun newInstance(): LogFragment {
            return LogFragment()
        }
    }
}