package com.example.clock.alarm.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.clock.ClockApp
import com.example.clock.RingtoneProvider
import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.data.AlarmRepository
import com.example.clock.alarm.ui.broadcastreceiver.AlarmReceiver
import kotlinx.coroutines.launch

class AlarmViewModel(
    var repository: AlarmRepository,
    var ringtoneProvider: RingtoneProvider,
    application: ClockApp
) : AndroidViewModel(application) {
init {
    viewModelScope.launch {
        ringtoneProvider.getSystemRingtones()
    }
}
    lateinit var alarms: LiveData<List<Alarm>>
    lateinit var ringtones: List<Ringtone>


    fun getAllAlarms() {
        alarms = repository.getAllAlarms()
    }
    fun update(alarm: Alarm) = viewModelScope.launch {
        repository.update(alarm)
    }
    fun deleteAlarms(alarms: List<Alarm>) = viewModelScope.launch {
        repository.deleteAlarms(alarms)
    }
    fun cancelAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmId = alarm.alarmId
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