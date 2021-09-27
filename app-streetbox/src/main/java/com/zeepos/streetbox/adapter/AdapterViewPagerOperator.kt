package com.zeepos.streetbox.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zeepos.streetbox.ui.broadcast.blast.BlastFragment
import com.zeepos.streetbox.ui.operator.operatortask.OperatorTaskFragment
import com.zeepos.streetbox.ui.operatorfreetask.OperatorFreeTaskFragment
import java.lang.IllegalArgumentException

class AdapterViewPagerOperator(
    val mutableListString: Array<String>,
    fragment: Fragment
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return mutableListString.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
//            0 -> {
//                BlastFragment()
//            }
            0 -> {
                OperatorTaskFragment()
            }
            1 -> {
                OperatorFreeTaskFragment()
            }
            else -> {
                throw IllegalArgumentException("")
            }
        }
    }
}