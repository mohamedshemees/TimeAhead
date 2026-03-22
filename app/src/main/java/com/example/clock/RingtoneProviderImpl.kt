package com.example.clock

import android.media.RingtoneManager
import com.example.clock.alarm.ui.Ringtone

class RingtoneProviderImpl(private val context: ClockApp) : RingtoneProvider {

    override suspend fun getSystemRingtones(): List<Ringtone> {
        val ringtoneManager = RingtoneManager(context)
        val ringtones = mutableListOf<Ringtone>()
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM)
        
        val cursor = ringtoneManager.cursor
        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = ringtoneManager.getRingtoneUri(cursor.position)
            ringtones.add(Ringtone(title, uri))
        }
        
        return ringtones
    }
}
