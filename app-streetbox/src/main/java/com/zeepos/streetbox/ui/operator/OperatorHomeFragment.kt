package com.zeepos.streetbox.ui.operator

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zeepos.streetbox.R
import com.zeepos.streetbox.adapter.AdapterViewPagerOperator
import com.zeepos.ui_base.ui.BaseFragment
import kotlinx.android.synthetic.main.operator_home.*
import java.text.FieldPosition

class OperatorHomeFragment : BaseFragment<OperatorHomeViewEvent, OperatorHomeViewModel>() {

    private var isSearchShow = false
    private lateinit var viewPagerAdapter: OperatorHomePagerAdapter
    var adapterViewPagerOperator: AdapterViewPagerOperator? = null
    private val titles =
        arrayOf("Regular Task", "Free Task","Door To Door")
//        arrayOf("Regular Task", "Free Task")
    override fun initResourceLayout(): Int {
        return R.layout.operator_home
    }

    override fun init() {
        val bundle = arguments
        val lat = bundle!!.getDouble("lat")

        viewModel = ViewModelProvider(this, viewModeFactory).get(OperatorHomeViewModel::class.java)
        viewPagerAdapter = OperatorHomePagerAdapter(childFragmentManager, bundle)
       
    }


    override fun onViewReady(savedInstanceState: Bundle?) {
        val bundle = arguments
        val lat = bundle!!.getDouble("lat")
        val type = bundle.getInt("type")

        setTabViewPager()
    }

    private fun setTabViewPager() {
        adapterViewPagerOperator = AdapterViewPagerOperator(titles, this)
        viewpagerOperator.adapter = adapterViewPagerOperator
        TabLayoutMediator(tabOperator, viewpagerOperator,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }
        ).attach()
    }

    override fun onEvent(useCase: OperatorHomeViewEvent) {
    }

    companion object {
        fun newInstance(): OperatorHomeFragment {
            return OperatorHomeFragment()
        }
    }
}