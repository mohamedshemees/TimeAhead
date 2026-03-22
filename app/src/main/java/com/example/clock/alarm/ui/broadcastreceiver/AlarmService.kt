package com.example.clock.alarm.ui.broadcastreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.clock.R

class AlarmService : Service() {

    private var ringtone: Ringtone? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val alarmId = intent?.getIntExtra("alarmId", -1) ?: -1
        Log.d("AlarmService", "onStartCommand action: $action, alarmId: $alarmId")

        when (action) {
            ACTION_DISMISS -> {
                Log.d("AlarmService", "Dismissing alarm $alarmId")
                stopAlarm()
                stopSelf()
            }
            ACTION_SNOOZE -> {
                Log.d("AlarmService", "Snoozing alarm $alarmId")
                // TODO: Implement snooze logic (rescheduling)
                stopAlarm()
                stopSelf()
            }
            else -> {
                val label = intent?.getStringExtra("label") ?: "Alarm"
                val soundUri = intent?.getStringExtra("soundUri")
                Log.d("AlarmService", "Ringing alarm $alarmId with label: $label, soundUri: $soundUri")
                
                val notification = createNotification(alarmId, label)
                
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Log.d("AlarmService", "Starting foreground with mediaPlayback type")
                        startForeground(alarmId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                    } else {
                        Log.d("AlarmService", "Starting foreground (Legacy)")
                        startForeground(alarmId, notification)
                    }
                } catch (e: Exception) {
                    Log.e("AlarmService", "Error starting foreground service", e)
                }

                playRingtone(soundUri)
            }
        }

        return START_NOT_STICKY
    }

    private fun createNotification(alarmId: Int, title: String): android.app.Notification {
        Log.d("AlarmService", "Creating notification for $title")
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("alarm_channel", "Alarms", NotificationManager.IMPORTANCE_HIGH)
        nm.createNotificationChannel(channel)


        val dismissIntent = Intent(this, AlarmService::class.java).apply { action = ACTION_DISMISS }
        val dismissPendingIntent = PendingIntent.getService(this, alarmId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val snoozeIntent = Intent(this, AlarmService::class.java).apply { action = ACTION_SNOOZE }
        val snoozePendingIntent = PendingIntent.getService(this, alarmId + 1000, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val customView = RemoteViews(packageName, R.layout.item_alarm_notification)
        customView.setTextViewText(R.id.notification_title, title)
        customView.setTextViewText(R.id.notification_message, "Ringing...")
        

        customView.setOnClickPendingIntent(R.id.btn_dismiss, dismissPendingIntent)
        customView.setOnClickPendingIntent(R.id.btn_snooze, snoozePendingIntent)

        return NotificationCompat.Builder(this, "alarm_channel")
            .setSmallIcon(R.drawable.ic_alarm)
            .setCustomContentView(customView)
            .setCustomBigContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun playRingtone(uriString: String?) {
        try {
            val uri = if (uriString.isNullOrEmpty()) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            } else {
                Uri.parse(uriString)
            }
            Log.d("AlarmService", "Loading ringtone from URI: $uri")
            ringtone = RingtoneManager.getRingtone(this, uri)
            ringtone?.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            Log.d("AlarmService", "Starting ringtone playback")
            ringtone?.play()
        } catch (e: Exception) {
            Log.e("AlarmService", "Error playing ringtone", e)
        }
    }

    private fun stopAlarm() {
        Log.d("AlarmService", "Stopping ringtone and resetting state")
        ringtone?.stop()
        ringtone = null
    }

    override fun onDestroy() {
        Log.d("AlarmService", "onDestroy called")
        stopAlarm()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_DISMISS = "com.example.clock.DISMISS"
        const val ACTION_SNOOZE = "com.example.clock.SNOOZE"
    }
}
