package id.streetbox.live.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.streetbox.live.ui.main.orderhistory.history.HistoryFragment
import id.streetbox.live.ui.main.orderhistory.ongoing.OnGoingFragment

class AdapterViewPagerOrderHistory(val mutableListString: Array<String>, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return mutableListString.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                HistoryFragment()
            }
            1 -> {
                OnGoingFragment()
            }
            else -> {
                HistoryFragment()
            }
        }
    }
}