package com.example.clock

import android.content.Context
import android.media.RingtoneManager
import com.example.clock.alarm.Ringtone

class RingtoneProviderImpl(private val context: Context) : RingtoneProvider {

    override suspend fun getSystemRingtones() : List<Ringtone> {
        val ringtoneProvider = RingtoneManager(context)
        val ringtones = mutableListOf<Ringtone>()
        ringtoneProvider.setType(RingtoneManager.TYPE_ALARM)
        val cursor = ringtoneProvider.cursor
        while (cursor.moveToNext()) {
            val ringtoneUri = ringtoneProvider.getRingtoneUri(cursor.position)
            val ringtoneTitle = ringtoneProvider.getRingtone(cursor.position).getTitle(context)
            val ringtone = Ringtone(ringtoneTitle, ringtoneUri)
            ringtones.add(ringtone)
        }
        return ringtones

    }
}