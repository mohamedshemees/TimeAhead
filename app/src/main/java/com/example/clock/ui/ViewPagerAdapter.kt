package com.example.clock.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clock.ui.alarm.AlarmFragment
import com.example.clock.ui.stopwatch.StopwatchFragment
import com.example.clock.ui.timer.TimerFragment
import com.example.clock.ui.worldclock.WorldClock

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlarmFragment()
            1 -> WorldClock()
            2 -> StopwatchFragment()
            3 -> TimerFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}


