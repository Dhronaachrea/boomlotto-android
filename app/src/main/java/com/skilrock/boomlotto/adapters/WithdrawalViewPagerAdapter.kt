package com.skilrock.boomlotto.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skilrock.boomlotto.ui.fragment.WithdrawalFragment
import com.skilrock.boomlotto.ui.fragment.WithdrawalPendingTransactionsFragment

class WithdrawalViewPagerAdapter(fragmentActivity: FragmentActivity):
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            WithdrawalFragment()
        } else {
            WithdrawalPendingTransactionsFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

}