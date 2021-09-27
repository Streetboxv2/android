package com.zeepos.map.ui.dialogs.merchantinfo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.zeepos.map.ui.merchantinstagram.MerchantInstagramFragment
import com.zeepos.map.ui.merchantschedule.MerchantScheduleFragment

/**
 * Created by Arif S. on 8/12/20
 */
class MerchantInfoPagerAdapter(
    fm: FragmentManager,
    private val data: String,
    private val merchantIg: String
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var merchantId: Long = 0

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> MerchantScheduleFragment.newInstance(data)
            1 -> MerchantInstagramFragment.newInstance(merchantIg)
            else -> MerchantScheduleFragment.newInstance(data)
        }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Schedule"
            1 -> return "Instagram"
        }

        return "Schedule"
    }

    override fun getCount(): Int = 2
}