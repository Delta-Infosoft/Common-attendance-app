package com.i.common.attendance.ui.tutorial.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.i.common.attendance.ui.tutorial.data.TutorialData
import com.i.common.attendance.ui.tutorial.fragment.TutorialFragment

class TutorialPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val pages: List<TutorialData>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return TutorialFragment.newInstance(pages[position])
    }
}

