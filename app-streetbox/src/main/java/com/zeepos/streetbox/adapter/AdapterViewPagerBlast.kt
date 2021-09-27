package com.zeepos.streetbox.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chad.library.adapter.base.listener.InstanceFragment

class AdapterViewPagerBlast(
    val mutableListString: Array<String>,
    fragment: Fragment,
    val instanceFragment: InstanceFragment
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return mutableListString.size
    }

    override fun createFragment(position: Int): Fragment {
        return instanceFragment.instanceFragment(position)
    }
}