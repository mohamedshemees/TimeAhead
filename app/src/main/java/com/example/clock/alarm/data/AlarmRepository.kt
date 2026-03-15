package com.example.clock.alarm.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.clock.alarm.data.dao.AlarmDao
import com.example.clock.alarm.data.mappers.toDomain
import com.example.clock.alarm.data.mappers.toEntity
import com.example.clock.alarm.domain.Alarm
import java.util.Calendar
import java.util.TimeZone

class AlarmRepository(
    private val alarmDao: AlarmDao,
) {

    suspend fun insert(alarm: Alarm) {
        alarmDao.insertAlarm(alarm.toEntity())
    }

    suspend fun update(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.toEntity())
    }

    suspend fun delete(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm.toEntity())
    }

    suspend fun deleteAlarms(alarms: List<Alarm>) {
        alarmDao.deleteAlarms(alarms.map { it.toEntity() })
    }

    fun getAllAlarms(): LiveData<List<Alarm>> {
        return alarmDao.getAllAlarms().map { entities ->
            entities.map { it.toDomain() }
        }
    }


    suspend fun getAlarmById(id: Int): Alarm? {
        return alarmDao.getAlarmById(id)?.toDomain()
    }

    fun getDefaultTimeMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
