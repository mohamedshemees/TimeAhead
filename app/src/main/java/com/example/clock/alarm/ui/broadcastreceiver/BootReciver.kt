package com.example.clock.alarm.ui.broadcastreceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.clock.worldClock.data.ClockDataBase
import com.example.clock.alarm.domain.Alarm
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.util.Log
import com.example.clock.alarm.ui.utils.AlarmUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            Log.w("BootReceiver", "Invalid context or intent action, skipping alarm rescheduling")
            return
        }

        // Use a supervised coroutine scope to reschedule alarms
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alarmDao = ClockDataBase.getDatabase(context).alarmDao()
                val alarmList = alarmDao.getAllAlarmsSynchronous()
                Log.d("BootReceiver", "Found ${alarmList.size} alarms to reschedule")

                alarmList.filter { it.Enabled }.forEach { alarm ->
                    rescheduleAlarm(context, alarm)
                }
            } catch (e: Exception) {
                Log.e("BootReceiver", "Failed to reschedule alarms: ${e.message}")
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun rescheduleAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmId = alarm.hashCode()

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
            putExtra("timeInMillis", alarm.timeInMillis)
            putExtra("days", alarm.days)
            putExtra("label", alarm.label)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Adjust time to the next occurrence if it’s in the past
        val nextTimeInMillis = AlarmUtils.getNextValidTime(alarm)
        if (nextTimeInMillis <= System.currentTimeMillis()) {
            Log.w("BootReceiver", "Alarm $alarmId time is in the past: $nextTimeInMillis, skipping")
            return
        }

        try {
            if (canScheduleExactAlarms(context)) {
                val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                Log.d("BootReceiver", "Rescheduling alarm $alarmId at ${utcFormat.format(Date(nextTimeInMillis))}")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTimeInMillis,
                    pendingIntent
                )
            } else {
                Log.w("BootReceiver", "Exact alarm permission denied, requesting...")
                requestExactAlarmPermission(context)
            }
        } catch (e: SecurityException) {
            Log.e("BootReceiver", "SecurityException while scheduling alarm $alarmId: ${e.message}")
        }
    }



    private fun canScheduleExactAlarms(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required for BroadcastReceiver
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("BootReceiver", "Failed to request exact alarm permission: ${e.message}")
            }
        }
    }
}


