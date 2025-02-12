package com.example.clock.data.repository

import android.content.Context
import android.media.RingtoneManager
import androidx.lifecycle.LiveData
import com.example.clock.data.local.AlarmDao
import com.example.clock.data.model.Alarm
import com.example.clock.ui.alarm.SoundPickerFragment.Ringtone
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

    fun getAllAlarmsSynchronous(): List<Alarm> {
        return alarmDao.getAllAlarmsSynchronous()
    }


        fun getSystemRingtones(context: Context) {
            val ringtoneManager = RingtoneManager(context)
            val ringtones = mutableListOf<Ringtone>()
            ringtoneManager.setType(RingtoneManager.TYPE_ALARM) // Use TYPE_RINGTONE for ringtones, TYPE_NOTIFICATION for notifications
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
            timeZone = TimeZone.getDefault() // Ensure it uses the local time zone
            set(Calendar.HOUR_OF_DAY, 6) // Set hour to 6 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }


}

