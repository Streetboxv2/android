package com.zeepos.streetbox.ui.operator

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chad.library.adapter.base.listener.InstanceFragment
import com.zeepos.streetbox.ui.broadcast.blast.BlastFragment
import com.zeepos.streetbox.ui.operator.operatortask.OperatorTaskFragment
import com.zeepos.streetbox.ui.operatorfreetask.OperatorFreeTaskFragment

class OperatorHomePagerAdapter(fm: FragmentManager, val instanceFragment: InstanceFragment) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = instanceFragment.instanceFragment(position)

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Regular Task"
            1 -> return "Free Task"
            2 -> return "Door To Door"
        }

        return "Regular Task"
    }

    override fun getCount(): Int = 3
}