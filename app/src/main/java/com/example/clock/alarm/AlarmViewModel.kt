package com.example.clock.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.clock.ClockApp
import com.example.clock.alarm.data.AlarmRepository
import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.ui.broadcastreceiver.AlarmReceiver
import kotlinx.coroutines.launch

class AlarmViewModel(
    var repository: AlarmRepository,
    application: ClockApp
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
            //cancelAlarm(application, alarm)
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
        val alarmId = alarm.alarmId // Use stored ID
        Log.d("wow", "Cancelling alarm with ID: $alarmId")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
            putExtra("timeInMillis", alarm.timeInMillis)
            putExtra("days", alarm.days)
            putExtra("label", alarm.label)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            Log.d("wow", "Alarm $alarmId canceled successfully")
        } else {
            Log.w("wow", "No existing PendingIntent found for alarm $alarmId")
        }
    }


}