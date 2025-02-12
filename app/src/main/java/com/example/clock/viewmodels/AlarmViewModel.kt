package com.example.clock.viewmodels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clock.ClockApp
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.model.Alarm
import com.example.clock.data.repository.AlarmRepository
import com.example.clock.ui.alarm.AlarmReceiver
import com.example.clock.ui.alarm.SoundPickerFragment
import com.example.clock.ui.alarm.SoundPickerFragment.Ringtone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AlarmViewModel(
                     var repository: AlarmRepository,
                     private var application: ClockApp
    ) : AndroidViewModel(application) {

    lateinit var allAlarms: LiveData<List<Alarm>>

    fun insert(alarm: Alarm) = viewModelScope.launch {
        repository.insert(alarm)
    }

    fun update(alarm: Alarm) = viewModelScope.launch {
        repository.update(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch {
        repository.delete(alarm)
    }

    fun getAllAlarms() {
        allAlarms = repository.getAllAlarms()
    }

    fun deleteAlarms(alarms: List<Alarm>) = viewModelScope.launch {
        for (alarm in alarms) {
            cancelAlarm(application, alarm)
        }
        repository.deleteAlarms(alarms)
    }

    fun insertOrUpdateAlarm(updatedAlarm: Alarm, oldAlarm: Alarm)=viewModelScope.launch  {
        val existingAlarm = oldAlarm

        if (existingAlarm != null) {
            // If the primary key has changed, delete the old entry first
            if (existingAlarm.label != updatedAlarm.label ||
                existingAlarm.timeInMillis != updatedAlarm.timeInMillis ||
                existingAlarm.days != updatedAlarm.days) {

                repository.delete(existingAlarm) // Remove old alarm with old primary key
            }
        }

        // Insert the updated alarm (whether it's a new one or a replacement)
        repository.insert(updatedAlarm)
    }
    fun cancelAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmId="${alarm.timeInMillis}_${alarm.days}_${alarm.label}".
        hashCode()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_NO_CREATE  or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

}