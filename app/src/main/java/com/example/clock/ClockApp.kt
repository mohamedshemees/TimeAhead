package com.example.clock

import android.app.Application
import com.example.clock.worldClock.data.ClockDataBase
import com.example.clock.alarm.data.AlarmRepository
import com.example.clock.timer.data.TimerRepository
import com.example.clock.timer.ui.TimerViewModel
import com.example.clock.worldClock.data.WorldClockRepository
import com.example.clock.worldClock.WorldClockViewModel

class ClockApp : Application() {
    lateinit var worldClockViewModel: WorldClockViewModel
    lateinit var timerViewModel: TimerViewModel

    val alarmRepository: AlarmRepository by lazy {
        AlarmRepository(ClockDataBase.getDatabase(this).alarmDao())
    }
    val ringtoneProvider: RingtoneProvider by lazy {
        RingtoneProviderImpl(this)
    }
    val clockRepository: WorldClockRepository by lazy {
        WorldClockRepository(ClockDataBase.getDatabase(this).worldClockDao())
    }
    val timerRepository: TimerRepository by lazy {
        TimerRepository(ClockDataBase.getDatabase(this).timerDao())
    }
}