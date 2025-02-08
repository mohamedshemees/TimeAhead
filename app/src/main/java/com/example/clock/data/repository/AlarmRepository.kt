package com.example.clock.data.repository

import androidx.lifecycle.LiveData
import com.example.clock.data.local.AlarmDao
import com.example.clock.data.model.Alarm

class AlarmRepository(
    private val alarmDao: AlarmDao
) {
    val allAlarms: LiveData<List<Alarm>> = alarmDao.getAllAlarms()

    suspend fun insert(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun update(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun delete(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }
}
