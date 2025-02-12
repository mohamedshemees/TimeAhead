package com.example.clock.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.map
import androidx.room.Database
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.model.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Retrieve alarms from Room Database and re-schedule them
            CoroutineScope(Dispatchers.IO).launch {
               val alarmlist=ClockDataBase.getDatabase(context!!).alarmDao().getAllAlarmsSynchronous()
                for (alarm in alarmlist) {
                    scheduleAlarm(context, alarm)
                }
            }
        }
    }
    fun scheduleAlarm(context: Context, alarm:Alarm) {
        val idHashed="${alarm.timeInMillis}_${alarm.days}_${alarm.label}".hashCode()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {

            putExtra("alarmId", idHashed)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, idHashed, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (canScheduleExactAlarms(context)) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent)
            } else {
                requestExactAlarmPermission(context) // Request permission
            }
        } catch (e: SecurityException) {
        }
    }
    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
            context.startActivity(intent)
        }
    }
    private fun canScheduleExactAlarms(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Permission is not needed on older versions
        }
    }

}

