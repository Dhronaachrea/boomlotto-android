package com.skilrock.boomlotto.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skilrock.boomlotto.ui.fragment.AddBankFragment
import com.skilrock.boomlotto.ui.fragment.WithdrawIdVerificationFragment

class WithdrawVerificationViewPagerAdapter(fragmentActivity: FragmentActivity, private val shiftToBank:()->Unit):
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            WithdrawIdVerificationFragment(shiftToBank)
        } else {
            AddBankFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

}