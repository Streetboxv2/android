package com.zeepos.streetbox.ui.operator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.listener.InstanceFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zeepos.models.ConstVar
import com.zeepos.streetbox.R
import com.zeepos.streetbox.adapter.AdapterViewPagerOperator
import com.zeepos.streetbox.ui.broadcast.blast.BlastFragment
import com.zeepos.streetbox.ui.operator.operatortask.OperatorTaskFragment
import com.zeepos.streetbox.ui.operatorfreetask.OperatorFreeTaskFragment
import com.zeepos.ui_base.ui.BaseFragment
import com.zeepos.utilities.SharedPreferenceUtil
import kotlinx.android.synthetic.main.operator_home.*

class OperatorHomeFragment : BaseFragment<OperatorHomeViewEvent, OperatorHomeViewModel>() {

    private var isSearchShow = false
    private lateinit var viewPagerAdapter: OperatorHomePagerAdapter
    var adapterViewPagerOperator: AdapterViewPagerOperator? = null

    private val titles =
        arrayOf("Regular Task", "Free Task", "Door to Door")

    val title =
        context?.let { SharedPreferenceUtil.getString(it,"title" ,ConstVar.EMPTY_STRING) }
            ?: ConstVar.EMPTY_STRING

    val body =
        context?.let { SharedPreferenceUtil.getString(it,"body" ,ConstVar.EMPTY_STRING) }
            ?: ConstVar.EMPTY_STRING



    val instanceFragment = object : InstanceFragment {

        override fun instanceFragment(position: Int): Fragment =
            when (position) {
                0 -> OperatorTaskFragment.getInstance()
                1 -> OperatorFreeTaskFragment.newInstance()
                2 -> BlastFragment.newInstance("", "")
                else -> BlastFragment.newInstance("","")
            }
    }
    override fun initResourceLayout(): Int {
        return R.layout.operator_home
    }

    override fun init() {

        viewModel = ViewModelProvider(this, viewModeFactory).get(OperatorHomeViewModel::class.java)
        viewPagerAdapter = OperatorHomePagerAdapter(childFragmentManager, instanceFragment)

    }



    override fun onViewReady(savedInstanceState: Bundle?) {


        viewpagerOperator.adapter = viewPagerAdapter
        if(title.equals("Incoming calll!")) {
            viewpagerOperator.setCurrentItem(3)
        }else{
            viewpagerOperator.setCurrentItem(0)
        }
        tab.setupWithViewPager(viewpagerOperator)
//        setTabViewPager()
    }


//    private fun setTabViewPager() {
//        adapterViewPagerOperator = AdapterViewPagerOperator(titles, this)
//
//        viewpagerOperator.adapter = adapterViewPagerOperator
//        TabLayoutMediator(tabOperator, viewpagerOperator,
//            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
//                tab.text = titles[position]
//
//
//            }
//        ).attach()
//    }

    override fun onEvent(useCase: OperatorHomeViewEvent) {
    }

    companion object {
        fun newInstance(): OperatorHomeFragment {
            return OperatorHomeFragment()
        }
    }
}