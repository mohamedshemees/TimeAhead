package com.example.clock.alarm.ui.broadcastreceiver


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.clock.R
import com.example.clock.alarm.domain.Alarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone.getTimeZone

class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val alarmId = intent.getIntExtra("alarmId", -1)
            val timeInMillis = intent.getLongExtra("timeInMillis", 0L)
            val days = intent.getStringExtra("days") ?: ""
            val label = intent.getStringExtra("label") ?: ""

            Log.d("wow", "Alarm fired: ID=$alarmId, Time=$timeInMillis, Days=$days, Label=$label")
            showNotification(context, label, "Alarm triggered at ${SimpleDateFormat("HH:mm").format(Date(timeInMillis))}")

            val alarm = Alarm(alarmId = alarmId, timeInMillis = timeInMillis, days = days, label = label)
            if (days.isNotEmpty() || alarm.getRepeatDays().isEmpty()) {
                scheduleNextAlarm(context, alarmId, alarm)
            } else {
                Log.d("wow", "One-time alarm, no rescheduling.")
            }
        }

        @SuppressLint("ScheduleExactAlarm")
        private fun scheduleNextAlarm(context: Context, alarmId: Int, alarm: Alarm) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            val repeatDays = alarm.getRepeatDays()

            val calendar = Calendar.getInstance(getTimeZone("UTC")).apply {
                timeInMillis = alarm.timeInMillis
            }
            val currentTime = Calendar.getInstance(getTimeZone("UTC")).timeInMillis

            val oneDayInMillis = 24 * 60 * 60 * 1000L
            var nextTimeInMillis = alarm.timeInMillis

            if (repeatDays.isEmpty()) {
                nextTimeInMillis += oneDayInMillis
            } else {
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
                val nextDayIndex = repeatDays.indexOfFirst { it > currentDayOfWeek }
                    .takeIf { it >= 0 } ?: repeatDays.first()
                val daysToAdd = (nextDayIndex - currentDayOfWeek + 7) % 7
                    .let { if (it == 0) 7 else it }
                nextTimeInMillis += daysToAdd * oneDayInMillis
            }

            if (nextTimeInMillis <= currentTime) {
                nextTimeInMillis += oneDayInMillis * (if (repeatDays.isEmpty()) 1 else 7)
            }

            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("alarmId", alarmId)
                putExtra("timeInMillis", nextTimeInMillis)
                putExtra("days", alarm.days)
                putExtra("label", alarm.label)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.getDefault()).apply {
                timeZone = getTimeZone("UTC")
            }
            Log.d("wow", "Rescheduling alarm $alarmId at: ${utcFormat.format(Date(nextTimeInMillis))}")
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTimeInMillis, pendingIntent)
        }

    companion object {
        fun showNotification(context: Context, title: String, message: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "alarm_channel",
                    "Alarms",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(title.hashCode(), builder.build()) // Unique ID per title
        }
    }
}