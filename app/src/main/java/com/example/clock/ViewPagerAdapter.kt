package com.example.clock.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clock.alarm.ui.AlarmFragment
import com.example.clock.stopwatch.ui.StopwatchFragment
import com.example.clock.timer.ui.TimerFragment
import com.example.clock.worldClock.ui.WorldClockFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlarmFragment()
            1 -> WorldClockFragment()
            2 -> StopwatchFragment()
            3 -> TimerFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}


