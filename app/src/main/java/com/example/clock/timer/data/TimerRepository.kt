package com.example.clock.timer.data

import android.content.Context
import android.media.RingtoneManager
import androidx.lifecycle.LiveData
import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.ui.SoundPickerFragment.Ringtone
import com.example.clock.timer.domain.TimerPreset
import java.util.Calendar
import java.util.TimeZone

class TimerRepository(
    private val TimerDao: TimerPresetDao,
) {
    var allRingtones: List<Ringtone> = emptyList()

    suspend fun insert(alarm: TimerPreset) {
        TimerDao.insertPreset(alarm)
    }

    suspend fun update(alarm: TimerPreset) {
        TimerDao.updatePreset(alarm)
    }

    suspend fun delete(alarm: TimerPreset) {
        TimerDao.deletePreset(alarm)
    }

    suspend fun deleteAlarms(alarms: List<TimerPreset>) {
        TimerDao.deletePresets(alarms)
    }

    fun getAllAlarms(): LiveData<List<TimerPreset>> {
        return TimerDao.getAllPresets()
    }

        fun getSystemRingtones(context: Context) {
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

