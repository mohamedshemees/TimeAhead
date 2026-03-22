package com.example.clock.alarm.ui.broadcastreceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.clock.ClockApp
import com.example.clock.alarm.data.AlarmRepository
import com.example.clock.alarm.domain.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AlarmReceiver(
    private val alarmRepository: AlarmRepository = ClockApp.instance.alarmRepository
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "onReceive triggered with action: ${intent.action}")
        val pendingResult = goAsync()
        val alarmId = intent.getIntExtra("alarmId", -1)
        Log.d("AlarmReceiver", "Alarm ID from intent: $alarmId")

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val alarm = if (alarmId != -1) {
                    Log.d("AlarmReceiver", "Fetching alarm with ID: $alarmId")
                    alarmRepository.getAlarmById(alarmId)
                } else {
                    Log.w("AlarmReceiver", "Alarm ID is -1, skipping fetch")
                    null
                }

                Log.d("AlarmReceiver", "Alarm found: ${alarm != null}")

                // Start Foreground Service to handle notification and sound
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    action = intent.action
                    putExtras(intent)
                    if (alarm != null) {
                        putExtra("soundUri", alarm.sound.soundUri)
                        putExtra("label", alarm.label)
                    }
                }
                Log.d("AlarmReceiver", "Starting AlarmService...")
                context.startForegroundService(serviceIntent)

                // Handle rescheduling for repeating alarms
                if (intent.action != ACTION_DISMISS && intent.action != ACTION_SNOOZE) {
                    if (alarm != null && alarm.days.isNotEmpty()) {
                        Log.d("AlarmReceiver", "Alarm has repeat days: ${alarm.days}, scheduling next...")
                        scheduleNextAlarm(context, alarmId, alarm)
                    } else {
                        Log.d("AlarmReceiver", "One-time alarm or no repeat days, not rescheduling")
                    }
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error processing alarm", e)
            } finally {
                Log.d("AlarmReceiver", "Finishing goAsync()")
                pendingResult.finish()
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNextAlarm(context: Context, alarmId: Int, alarm: Alarm) {
        val alarmManager = context.getSystemService(android.app.AlarmManager::class.java)
        val repeatDays = alarm.getRepeatDays()
        if (repeatDays.isEmpty()) {
            Log.w("AlarmReceiver", "repeatDays is empty in scheduleNextAlarm")
            return
        }

        val now = Calendar.getInstance()
        val next = Calendar.getInstance()

        next.timeInMillis = alarm.timeInMillis
        next.set(Calendar.YEAR, now.get(Calendar.YEAR))
        next.set(Calendar.MONTH, now.get(Calendar.MONTH))
        next.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

        val currentDayIndex = now.get(Calendar.DAY_OF_WEEK) - 1
        val sortedDays = repeatDays.sorted()
        val nextDay = sortedDays.firstOrNull { it > currentDayIndex } ?: sortedDays.first()

        var daysToAdd = nextDay - currentDayIndex
        if (daysToAdd <= 0) daysToAdd += 7

        next.add(Calendar.DAY_OF_MONTH, daysToAdd)

        val nextTimeInMillis = next.timeInMillis

        Log.d("AlarmReceiver", "Rescheduling alarm $alarmId to " +
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(nextTimeInMillis)))

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
            putExtra("timeInMillis", nextTimeInMillis)
            putExtra("days", alarm.days)
            putExtra("label", alarm.label)
            putExtra("soundOn", alarm.sound.soundOn)
            putExtra("soundUri", alarm.sound.soundUri)
        }

        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            nextTimeInMillis,
            pendingIntent
        )
    }

    companion object {
        private const val ACTION_SNOOZE = "com.example.clock.SNOOZE"
        private const val ACTION_DISMISS = "com.example.clock.DISMISS"
    }
}
