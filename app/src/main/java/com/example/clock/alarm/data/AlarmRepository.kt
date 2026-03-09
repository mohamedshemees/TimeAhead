package com.example.clock.alarm.data

import android.content.Context
import android.media.RingtoneManager
import androidx.lifecycle.LiveData
import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.ui.SoundPickerFragment.Ringtone
import java.util.Calendar
import java.util.TimeZone

class AlarmRepository(
    private val alarmDao: AlarmDao,
) {
    var allRingtones: List<Ringtone> = emptyList()

    suspend fun insert(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun update(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun delete(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    suspend fun deleteAlarms(alarms: List<Alarm>) {
        alarmDao.deleteAlarms(alarms)
    }

    fun getAllAlarms(): LiveData<List<Alarm>> {
        return alarmDao.getAllAlarms()
    }

        suspend fun getSystemRingtones(context: Context) {
            val ringtoneManager = RingtoneManager(context)
            val ringtones = mutableListOf<Ringtone>()
            ringtoneManager.setType(RingtoneManager.TYPE_ALARM)
            val cursor = ringtoneManager.cursor
            while (cursor.moveToNext()) {
                val ringtoneUri = ringtoneManager.getRingtoneUri(cursor.position)
                val ringtoneTitle = ringtoneManager.getRingtone(cursor.position).getTitle(context)
                val ringtone = Ringtone(ringtoneTitle, ringtoneUri)
                ringtones.add(ringtone)
            }
            allRingtones = ringtones
        }

        suspend fun getAlarm(label: String, time: String, days: String): Alarm? {
            return alarmDao.getAlarm(label, time, days)
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

