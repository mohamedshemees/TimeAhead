package com.example.clock

import android.content.Context
import com.example.clock.alarm.Ringtone

interface RingtoneProvider {
    suspend fun getSystemRingtones() : List<Ringtone>
}