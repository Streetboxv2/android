package com.zeepos.streetbox.ui.operator

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.zeepos.streetbox.ui.broadcast.blast.BlastFragment
import com.zeepos.streetbox.ui.operator.operatortask.OperatorTaskFragment
import com.zeepos.streetbox.ui.operatorfreetask.OperatorFreeTaskFragment

class OperatorHomePagerAdapter(fm: FragmentManager, data: Bundle) :

    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var data: Bundle? = data
    override fun getItem(position: Int): Fragment =
        when (position) {

            0 -> OperatorTaskFragment.getInstance(data)
            1 -> OperatorFreeTaskFragment.newInstance()

            else -> OperatorTaskFragment.getInstance(data)
        }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {

            0 -> return "Reguler Task"
            1 -> return "Free Task"

        }

        return "Reguler Task"
    }


    override fun getCount(): Int = 2
}