package com.example.clock

import android.app.Application
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.repository.AlarmRepository

class ClockApp : Application() {
    val repository: AlarmRepository by lazy {
        AlarmRepository(ClockDataBase.getDatabase(this).alarmDao())
    }


}