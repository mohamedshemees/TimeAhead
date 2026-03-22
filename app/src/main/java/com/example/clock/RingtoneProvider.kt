package com.example.clock

import com.example.clock.alarm.ui.Ringtone

interface RingtoneProvider {
    suspend fun getSystemRingtones() : List<Ringtone>
}