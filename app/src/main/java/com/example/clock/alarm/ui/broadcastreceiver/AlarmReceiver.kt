package com.example.clock.alarm.ui.broadcastreceiver


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.clock.R
import com.example.clock.alarm.domain.Alarm
import com.example.clock.worldClock.ui.TimeZoneItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.TimeZone.getTimeZone


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarmId", -1)
        val timeInMillis = intent.getLongExtra("timeInMillis", 0L)
        val days = intent.getStringExtra("days") ?: ""
        val label = intent.getStringExtra("label") ?: ""
        val soundOn = intent.getBooleanExtra("soundOn", true)
        val soundUri = intent.getStringExtra("soundUri")
        Log.d("ALARM", "Alarm fired (id=$alarmId, label=$label, days=$days)")
        Log.d("ALARM", "soundUri (id=$soundUri")

        showNotification(
            context,
            title = label.ifBlank { "Alarm" },
            message = "Alarm triggered at ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}",
            soundUri = soundUri
        )

        val alarm = Alarm(
            alarmId = alarmId,
            timeInMillis = timeInMillis,
            days = days,
            label = label,
            sound = Alarm.AlarmSound(soundOn = soundOn, soundUri = soundUri ?: "")
        )
        Log.d("ALARM", "${alarm.getRepeatDays()}")


        if (alarm.getRepeatDays().isNotEmpty()) {
            scheduleNextAlarm(context, alarmId, alarm)
        } else {
            Log.d("ALARM", "One-time alarm. Not rescheduling.")
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNextAlarm(context: Context, alarmId: Int, alarm: Alarm) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val repeatDays = alarm.getRepeatDays()
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

        Log.d(
            "ALARM",
            "Rescheduling alarm $alarmId to " +
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(nextTimeInMillis))
        )

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
            putExtra("timeInMillis", nextTimeInMillis)
            putExtra("days", alarm.days)
            putExtra("label", alarm.label)
            putExtra("soundOn", alarm.sound.soundOn)
            putExtra("soundUri", alarm.sound.soundUri)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTimeInMillis,
            pendingIntent
        )
    }

    companion object {
        fun showNotification(context: Context, title: String, message: String, soundUri: String? = null) {
            val nm = context.getSystemService(NotificationManager::class.java)

            val channel = NotificationChannel(
                "alarm_channel",
                "Alarms",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)

            val customView = RemoteViews(context.packageName, R.layout.item_alarm_notification)
            customView.setTextViewText(R.id.notification_title, title)
            customView.setTextViewText(R.id.notification_message, message)


            val notif = NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(R.drawable.ic_alarm)
                .setCustomContentView(customView)
                .setCustomBigContentView(customView)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build()

            nm.notify(title.hashCode(), notif)

            soundUri?.let {
                try {
                    val ringtone = RingtoneManager.getRingtone(context, Uri.parse(it))

                    ringtone.play()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}