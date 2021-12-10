package com.zeepos.streetbox.ui.main.parkingspace

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeepos.models.master.ParkingSpace
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.parkingdetail.ParkingDetailActivity
import com.zeepos.ui_base.ui.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.parkingspace_fragment.*
import java.util.concurrent.TimeUnit

class ParkingSpaceFragment : BaseFragment<ParkingSpaceViewEvent, ParkingSpaceViewModel>() {

    private lateinit var parkingSpaceAdapter: ParkingSpaceAdapter
    private val compositeDisposable = CompositeDisposable()
    private var isLoadMore = true

    override fun initResourceLayout(): Int {
        return R.layout.parkingspace_fragment
    }

    override fun init() {
        viewModel = ViewModelProvider(this, viewModeFactory).get(ParkingSpaceViewModel::class.java)

        parkingSpaceAdapter = ParkingSpaceAdapter()
//        viewModel.getParkingSpace()
        viewModel.getParkingSpaceCloud()
    }

    override fun onViewReady(savedInstanceState: Bundle?) {

        swipe_refresh.setColorSchemeColors(Color.rgb(47, 223, 189))
        swipe_refresh.isRefreshing = true
        swipe_refresh.setOnRefreshListener {
            viewModel.getParkingSpaceCloud(true)
        }

        rcv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = parkingSpaceAdapter
        }

        parkingSpaceAdapter.setOnItemClickListener { adapter, _, position ->
            context?.let {
                val parkingSpace = adapter.getItem(position) as ParkingSpace
                startActivity(ParkingDetailActivity.getIntent(it, parkingSpace.id, false))
            }
        }

        parkingSpaceAdapter.loadMoreModule.setOnLoadMoreListener {
            if (isLoadMore) {
                viewModel.getParkingSpaceCloud()
            } else {
                parkingSpaceAdapter.loadMoreModule.loadMoreEnd()
            }

        }

        observeSearchView()

        sv_search.setOnClickListener {
            sv_search.isIconified = false
        }
    }

    private fun observeSearchView() {
        val disposable = fromView(sv_search)
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter {
                if (it.isEmpty()) {
                    parkingSpaceAdapter.data.clear()
                    viewModel.getParkingSpace()
                    isLoadMore = true
                    return@filter false
                }

                true
            }
            .distinctUntilChanged()
            .switchMap { viewModel.searchParkingSpace(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                parkingSpaceAdapter.setList(it)
                isLoadMore = false
            }, {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            })

        compositeDisposable.add(disposable)
    }

    private fun fromView(searchView: SearchView): Observable<String> {
        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                subject.onComplete()
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                subject.onNext(newText)
                return false
            }
        })

        return subject
    }

    override fun onEvent(useCase: ParkingSpaceViewEvent) {
        when (useCase) {
            is ParkingSpaceViewEvent.GetAllParkingSpaceSuccess -> {
                if (useCase.data.isNotEmpty()) {
                    if (swipe_refresh.isRefreshing) {
                        parkingSpaceAdapter.data.clear()
                    }

                    parkingSpaceAdapter.addData(useCase.data)
                    parkingSpaceAdapter.loadMoreModule.loadMoreComplete()
                } else {
                    parkingSpaceAdapter.loadMoreModule.loadMoreEnd()
                }

                swipe_refresh.isRefreshing = false
                dismissLoading()
            }
            is ParkingSpaceViewEvent.GetAllParkingSpaceFailed -> {
                parkingSpaceAdapter.loadMoreModule.loadMoreFail()
                swipe_refresh.isRefreshing = false
                dismissLoading()
                Toast.makeText(context, useCase.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    companion object {
        fun newInstance(): ParkingSpaceFragment {
            return ParkingSpaceFragment()
        }
    }
}
