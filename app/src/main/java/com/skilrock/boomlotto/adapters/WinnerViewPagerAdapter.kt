package com.skilrock.boomlotto.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skilrock.boomlotto.ui.fragment.DrawGamesWinnerListFragment
import com.skilrock.boomlotto.ui.fragment.InstantGamesWinnerListFragment

class WinnerViewPagerAdapter(fragmentActivity: FragmentActivity):
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            DrawGamesWinnerListFragment()
        } else {
            InstantGamesWinnerListFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

}